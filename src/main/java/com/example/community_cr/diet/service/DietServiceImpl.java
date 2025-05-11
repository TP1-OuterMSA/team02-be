package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.example.community_cr.diet.controller.dto.response.WeeklyNutritionDto;
import com.example.community_cr.diet.controller.dto.response.WeeklyNutritionResponse;
import com.example.community_cr.diet.entity.Diet;
import com.example.community_cr.diet.entity.DietFood;
import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.repository.DietFoodRepository;
import com.example.community_cr.diet.repository.DietRepository;
import com.example.community_cr.diet.repository.FoodRepository;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DietServiceImpl implements DietService {
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
		// 이미 diet에 포함된 foodCode들을 추출
		Set<String> existingDietFoodCodes = diet.getFoods().stream()
			.map(dietFood -> dietFood.getFood().getFoodCode())
			.collect(Collectors.toSet());

		// 새롭게 추가할 foodCode만 필터링
		List<String> foodCodes = dto.getFoods().stream()
			.map(FoodRequest::getFoodCode)
			.filter(foodCode -> !existingDietFoodCodes.contains(foodCode))
			.toList();

		if (foodCodes.isEmpty()) {
			throw new IllegalArgumentException("새롭게 추가할 음식이 존재하지 않습니다.");
		}

		List<Food> foods = foodRepository.findAllByFoodCodeIn(foodCodes);

		// 존재하지 않는 음식 코드 검증
		if (foods.size() != foodCodes.size()) {
			Set<String> existingFoodCodes = foods.stream()
				.map(Food::getFoodCode)
				.collect(Collectors.toSet());

			List<String> notFoundFoodCodes = foodCodes.stream()
				.filter(foodCode -> !existingFoodCodes.contains(foodCode))
				.toList();

			throw new IllegalArgumentException("존재하지 않는 음식 코드 : " + String.join(", ", notFoundFoodCodes));
		}

		Map<String, FoodRequest> foodRequestMap = dto.getFoods().stream()
			.collect(Collectors.toMap(FoodRequest::getFoodCode, Function.identity()));

		// DietFood 생성
		List<DietFood> dietFoodsToAdd = foods.stream()
			.map(food -> {
				FoodRequest foodRequest = foodRequestMap.get(food.getFoodCode());
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

	public WeeklyNutritionResponse getWeeklyNutrition(long userId, LocalDate date, int count) {
		List<WeeklyNutritionDto> result = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			LocalDate endDate = date.minusWeeks(i).with(java.time.DayOfWeek.SUNDAY);
			LocalDate startDate = endDate.minusDays(6);

			List<Diet> weeklyDiets = dietRepository.findAllByUserIdAndDateBetween(userId, startDate, endDate);

			double totalCarb = 0, totalProtein = 0, totalFat = 0, totalKcal = 0;

			for (Diet diet : weeklyDiets) {
				for (DietFood df : diet.getFoods()) {
					Food food = df.getFood();
					double ratio = df.getIntakeWeight() / food.getFoodWeight(); // 섭취 비율

					totalCarb += food.getCarb() * ratio;
					totalProtein += food.getProtein() * ratio;
					totalFat += food.getFat() * ratio;
					totalKcal += food.getKcal() * ratio;
				}
			}

			double totalMacro = totalCarb + totalProtein + totalFat;

			double carbRatio = (totalMacro == 0) ? 0 : (totalCarb / totalMacro) * 100;
			double proteinRatio = (totalMacro == 0) ? 0 : (totalProtein / totalMacro) * 100;
			double fatRatio = (totalMacro == 0) ? 0 : (totalFat / totalMacro) * 100;

			result.add(WeeklyNutritionDto.builder()
				.startDate(startDate.format(DateTimeFormatter.ofPattern("MM-dd")))
				.endDate(endDate.format(DateTimeFormatter.ofPattern("MM-dd")))
				.carb(Math.round(carbRatio * 100.0) / 100.0)
				.protein(Math.round(proteinRatio * 100.0) / 100.0)
				.fat(Math.round(fatRatio * 100.0) / 100.0)
				.kcal(Math.round(totalKcal * 100.0) / 100.0)
				.build());

		}

		return WeeklyNutritionResponse.builder().nutritions(result).build();
	}

}
