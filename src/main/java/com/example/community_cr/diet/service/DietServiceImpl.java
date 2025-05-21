package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
import com.example.community_cr.diet.controller.dto.request.FoodRequest;
import com.example.community_cr.diet.controller.dto.response.DietResponse;
import com.example.community_cr.diet.controller.dto.response.NutritionAnalysisResponse;
import com.example.community_cr.diet.entity.Diet;
import com.example.community_cr.diet.entity.DietFood;
import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.repository.DietFoodRepository;
import com.example.community_cr.diet.repository.DietRepository;
import com.example.community_cr.diet.repository.FoodRepository;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DietServiceImpl implements DietService {
	private final AiApiConfig aiApiConfig;
	private final AiApiService aiApiService;

	private final DietRepository dietRepository;
	private final DietFoodRepository dietFoodRepository;
	private final FoodRepository foodRepository;
	private final UserRepository userRepository;

	@Override
	public Optional<DietResponse> saveDiet(long userId, DietRequest dto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 데이터가 존재하지 않습니다."));
		Optional<Diet> existDiet = dietRepository.findByUserIdAndDateAndType(userId, dto.getDate(), dto.getMealType());
		if (existDiet.isPresent()) {
			addDietFoodFromDietRequest(existDiet.get(), dto);
			return Optional.of(DietResponse.from(existDiet.get(), user.getRecommendKcal()));
		}

		Diet diet = Diet.of(user, dto.getDate(), dto.getMealType());
		dietRepository.save(diet);

		addDietFoodFromDietRequest(diet, dto);

		return Optional.of(DietResponse.from(diet, user.getRecommendKcal()));
	}

	private void addDietFoodFromDietRequest(Diet diet, DietRequest dto) {
		// 이미 diet에 포함된 foodName들을 추출
		Set<String> existingDietFoodNames = diet.getFoods().stream()
			.map(DietFood::getFood)
			.map(Food::getFoodName)
			.collect(Collectors.toSet());

		// 새롭게 추가할 foodName 필터링
		List<String> foodNames = dto.getFoods().stream()
			.map(FoodRequest::getFoodName)
			.filter(foodName -> !existingDietFoodNames.contains(foodName))
			.toList();

		if (foodNames.isEmpty()) {
			throw new IllegalArgumentException("새롭게 추가할 음식이 존재하지 않습니다.");
		}

		List<Food> foods = foodRepository.findAllByFoodNameIn(foodNames);

		// 존재하지 않는 음식 코드 검증
		if (foods.size() != foodNames.size()) {
			Set<String> existingFoodNames = foods.stream()
				.map(Food::getFoodName)
				.collect(Collectors.toSet());

			List<String> notFoundFoodNames = foodNames.stream()
				.filter(foodName -> !existingFoodNames.contains(foodName))
				.toList();

			throw new IllegalArgumentException("존재하지 않는 음식 코드 : " + String.join(", ", notFoundFoodNames));
		}

		Map<String, FoodRequest> foodRequestMap = dto.getFoods().stream()
			.collect(Collectors.toMap(FoodRequest::getFoodName, Function.identity()));

		// DietFood 생성
		List<DietFood> dietFoodsToAdd = foods.stream()
			.map(food -> {
				FoodRequest foodRequest = foodRequestMap.get(food.getFoodName());
				if (foodRequest != null) {
					return DietFood.from(
						foodRequest.getIntakeWeight(),
						foodRequest.getIntakeKcal(),
						diet, food);
				}
				return null;
			})
			.filter(Objects::nonNull)
			.toList();

		diet.addFoods(dietFoodsToAdd);
		dietFoodRepository.saveAll(dietFoodsToAdd);
	}

	@Override
	public Optional<DietResponse> getDiet(long userId, long dietId) {
		Diet diet = dietRepository.findById(dietId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단 ID입니다."));
		User user = diet.getUser();
		if (user.getId() != userId) {
			throw new IllegalArgumentException("자신의 식단만 조회할 수 있습니다.");
		}
		return Optional.of(DietResponse.from(diet, user.getRecommendKcal()));
	}

	@Override
	public List<DietResponse> getDiets(long userId, LocalDate date) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));

		List<Diet> dietList = dietRepository.findAllByDateAndUserId(date, userId);

		double recommendKcal = user.getRecommendKcal();
		return dietList.stream()
			.map(diet -> DietResponse.from(diet, recommendKcal))
			.toList();
	}

	@Override
	public List<LocalDate> getDietDates(long userId, YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1); // 2025-04-01
		LocalDate endDate = yearMonth.atEndOfMonth(); // 2025-04-30

		return dietRepository.findDietDatesBetween(startDate, endDate);
	}

	@Override
	public void deleteDiet(long userId, long dietId) {
		Diet diet = dietRepository.findById(dietId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단입니다."));

		if (diet.getUser().getId() != userId) {
			throw new IllegalArgumentException("자신의 식단만 삭제할 수 있습니다.");
		}

		dietFoodRepository.deleteAll(diet.getFoods());

		dietRepository.delete(diet);
	}

	@Override
	public void deleteDietFood(long userId, long dietFoodId) {
		DietFood dietFood = dietFoodRepository.findById(dietFoodId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단 음식입니다."));

		Diet diet = dietFood.getDiet();

		if (diet.getUser().getId() != userId) {
			throw new IllegalArgumentException("자신의 식단 음식만 삭제할 수 있습니다.");
		}

		dietFoodRepository.delete(dietFood);

		if (!dietFoodRepository.existsByDietId(diet.getId())) {
			dietRepository.delete(diet);
		}
	}

	@Override
	public Optional<DayNutritionAnalysisResponse> dayAnalyzeNutrition(long userId, LocalDate startDate,
		LocalDate endDate) {
		List<Diet> dietList = dietRepository.findAllByUserIdAndDateBetween(userId, startDate, endDate);

		Map<LocalDate, List<Diet>> groupedByDate = dietList.stream()
			.collect(Collectors.groupingBy(Diet::getDate, TreeMap::new, Collectors.toList()));

		List<NutritionAnalysisResponse> nutritionAnalysisResponses = new ArrayList<>();

		for (LocalDate tempDate = startDate; tempDate.isBefore(endDate.plusDays(1)); tempDate = tempDate.plusDays(1)) {
			List<Diet> dateDietList = groupedByDate.get(tempDate);
			if (dateDietList == null) {
				nutritionAnalysisResponses.add(NutritionAnalysisResponse.empty(tempDate));
				continue;
			}
			double dayTotalKcal = 0;
			double dayCarb = 0;
			double dayProtein = 0;
			double dayFat = 0;
			for (Diet diet : dateDietList) {
				for (DietFood dietFood : diet.getFoods()) {
					Food food = dietFood.getFood();
					double ratio = dietFood.getIntakeWeight() / food.getFoodWeight();
					dayTotalKcal += dietFood.getIntakeKcal();
					dayCarb += food.getCarb() * ratio;
					dayProtein += food.getProtein() * ratio;
					dayFat += food.getFat() * ratio;
				}
			}
			double carbRatio = (dayCarb * 4) / dayTotalKcal * 100;
			double proteinRatio = (dayProtein * 4) / dayTotalKcal * 100;
			double fatRatio = (dayFat * 9) / dayTotalKcal * 100;
			nutritionAnalysisResponses.add(NutritionAnalysisResponse.builder()
				.date(tempDate)
				.totalKcal(dayTotalKcal)
				.carb(carbRatio)
				.protein(proteinRatio)
				.fat(fatRatio)
				.build());
		}

		return Optional.of(DayNutritionAnalysisResponse.from(nutritionAnalysisResponses));
	}

	@Override
	public Optional<NutritionAnalysisResponse> analyzeNutrition(long userId, LocalDate date) {
		List<Diet> dietList = dietRepository.findAllByDateAndUserId(date, userId);

		if (dietList.isEmpty()) {
			return Optional.of(NutritionAnalysisResponse.empty(date));
		}

		double totalKcal = 0, carb = 0, protein = 0, fat = 0;

		for (Diet diet : dietList) {
			for (DietFood dietFood : diet.getFoods()) {
				Food food = dietFood.getFood();
				double ratio = dietFood.getIntakeWeight() / food.getFoodWeight();

				totalKcal += dietFood.getIntakeKcal();
				carb += food.getCarb() * ratio;
				protein += food.getProtein() * ratio;
				fat += food.getFat() * ratio;
			}
		}

		NutritionAnalysisResponse nutritionAnalysisResponse = NutritionAnalysisResponse.builder()
			.date(date)
			.totalKcal(totalKcal)
			.carb(carb)
			.protein(protein)
			.fat(fat)
			.build();

		return Optional.of(nutritionAnalysisResponse);
	}

	public WeeklyNutritionResponse getWeeklyNutrition(long userId, LocalDate date, int count) {
		List<WeeklyNutritionDto> result = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			LocalDate startDate = date.plusWeeks(i).with(java.time.DayOfWeek.SUNDAY);
			LocalDate endDate = startDate.plusDays(6);

			List<Diet> weeklyDiets = dietRepository.findAllByUserIdAndDateBetween(userId, startDate, endDate);

			double weekCarb = 0, weekProtein = 0, weekFat = 0, weekKcal = 0;

			for (Diet diet : weeklyDiets) {
				for (DietFood df : diet.getFoods()) {
					Food food = df.getFood();
					double ratio = df.getIntakeWeight() / food.getFoodWeight(); // 섭취 비율

					weekCarb += food.getCarb() * ratio;
					weekProtein += food.getProtein() * ratio;
					weekFat += food.getFat() * ratio;
					weekKcal += food.getKcal() * ratio;
				}
			}
			double carbRatio = (weekKcal == 0) ? 0 : (weekCarb * 4 / weekKcal) * 100;
			double proteinRatio = (weekKcal == 0) ? 0 : (weekProtein * 4 / weekKcal) * 100;
			double fatRatio = (weekKcal == 0) ? 0 : (weekFat * 9 / weekKcal) * 100;

			result.add(WeeklyNutritionDto.builder()
				.startDate(startDate)
				.endDate(endDate)
				.carb(carbRatio)
				.protein(proteinRatio)
				.fat(fatRatio)
				.kcal(weekKcal)
				.build());
		}

		return WeeklyNutritionResponse.from(result);
	}

	@Override
	public EvaluateNutritionResponse getDayNutritionEvaluate(EvaluateNutritionRequest evaluateNutritionRequest) {
		List<String> dayNutritionList = evaluateNutritionRequest.getEvaluateNutritionList().stream()
			.map(Object::toString)
			.toList();

		String userMessage = String.join(", ", dayNutritionList);
		String systemMessage = String.format(aiApiConfig.getDayNutritionEvaluateSystemMessage(),
			dayNutritionList.size());
		ApiRequest apiRequest = new ApiRequest(aiApiConfig.getModel(), systemMessage, userMessage);

		EvaluateInfo evaluateInfo = getEvaluateByAiApiRequest(apiRequest, aiApiConfig.getNutritionEvaluateKey());

		List<Food> foods = evaluateInfo.getRecommendFoods().stream()
			.map(NutritionInfo::toEntity)
			.toList();
		foodRepository.saveAll(foods);

		String evaluate = evaluateInfo.getEvaluate();
		List<FoodResponse> foodResponses = foods.stream()
			.map(FoodResponse::from)
			.toList();
		return EvaluateNutritionResponse.of(evaluate, foodResponses);
	}

	@Override
	public EvaluateNutritionResponse getWeekNutritionEvaluate(
		EvaluateNutritionRequest evaluateNutritionRequest) {
		List<String> weekNutritionList = evaluateNutritionRequest.getEvaluateNutritionList().stream()
			.map(Object::toString)
			.toList();

		String userMessage = String.join(", ", weekNutritionList);
		String systemMessage = String.format(aiApiConfig.getWeekNutritionEvaluateSystemMessage(),
			weekNutritionList.size());
		ApiRequest apiRequest = new ApiRequest(aiApiConfig.getModel(), systemMessage, userMessage);

		EvaluateInfo evaluateInfo = getEvaluateByAiApiRequest(apiRequest, aiApiConfig.getNutritionEvaluateKey());

		List<Food> foods = evaluateInfo.getRecommendFoods().stream()
			.map(NutritionInfo::toEntity)
			.toList();
		foodRepository.saveAll(foods);

		String evaluate = evaluateInfo.getEvaluate();
		List<FoodResponse> foodResponses = foods.stream()
			.map(FoodResponse::from)
			.toList();
		return EvaluateNutritionResponse.of(evaluate, foodResponses);
	}

	private EvaluateInfo getEvaluateByAiApiRequest(ApiRequest apiRequest, String apiKey) {
		String cleanedJson = aiApiService.getCleanedJsonFromAiApiRequest(apiRequest, apiKey);

		ObjectMapper mapper = new ObjectMapper();
		EvaluateInfo evaluateInfo;
		try {
			evaluateInfo = mapper.readValue(cleanedJson, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			log.info("Json 파싱 에러 : {}", cleanedJson);
			throw new IllegalStateException("일일 식단 분석에 실패했습니다. 다시 시도해주세요.");
		}

		return evaluateInfo;
	}

}
