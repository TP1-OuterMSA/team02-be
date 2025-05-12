package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.common.config.AiApiConfig;
import com.example.community_cr.common.service.AiApiService;
import com.example.community_cr.diet.controller.dto.request.FoodRequest;
import com.example.community_cr.common.exception.ApiErrorException;
import com.example.community_cr.diet.controller.dto.request.api.ApiRequest;
import com.example.community_cr.diet.controller.dto.response.FoodResponse;
import com.example.community_cr.diet.controller.dto.response.api.FoodInfo;
import com.example.community_cr.diet.controller.dto.response.api.NutritionInfo;
import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.entity.MealType;
import com.example.community_cr.diet.repository.FoodRepository;
import com.example.community_cr.school_meal.entity.Meal;
import com.example.community_cr.school_meal.entity.MealMenu;
import com.example.community_cr.school_meal.entity.Menu;
import com.example.community_cr.school_meal.repository.MealRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FoodServiceImpl implements FoodService {
	private final AiApiConfig aiApiConfig;
	private final AiApiService aiApiService;

	private final FoodRepository foodRepository;
	private final MealRepository mealRepository;

	@Override
	public List<FoodResponse> getFoods(int count, String foodName) {
		if (foodName.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}
		String aiApiFoodSystemMessage = String.format(aiApiConfig.getFoodSystemMessageFormat(), count);

		ApiRequest apiRequest = new ApiRequest(aiApiConfig.getModel(), aiApiFoodSystemMessage, foodName);
		String cleanedJson = aiApiService.getCleanedJsonFromAiApiRequest(apiRequest, aiApiConfig.getSearchFoodKey());

		log.info(cleanedJson);
		ObjectMapper mapper = new ObjectMapper();
		List<FoodInfo> foodInfos;
		try {
			foodInfos = mapper.readValue(cleanedJson, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("메뉴를 불러오는데 실패했습니다. 다시 시도해주세요.");
		}
		if (foodInfos.isEmpty()) {
			throw new IllegalStateException("메뉴를 불러오는데 실패했습니다. 다시 시도해주세요.");
		}

		return foodInfos.stream()
			.map(FoodResponse::from)
			.toList();
	}

	@Override
	public List<FoodResponse> getSchoolMeal(LocalDate date, MealType mealType) {
		if (mealType == MealType.SNACK) {
			throw new IllegalArgumentException("식사 종류로 간식은 입력할 수 없습니다.");
		}

		Meal meal = mealRepository.findByDayInfoAndMealType(date.format(DateTimeFormatter.ISO_LOCAL_DATE), mealType)
			.orElseThrow(() -> new IllegalArgumentException("아직 해당 날짜의 학식 정보가 입력되지 않았습니다."));

		List<String> menuNames = meal.getMealMenus().stream()
			.map(MealMenu::getMenu)
			.map(Menu::getName)
			.toList();

		List<Food> existFoods = foodRepository.findAllByFoodNameIn(menuNames);
		List<String> foodNames = existFoods.stream()
			.map(Food::getFoodName)
			.toList();

		if (foodNames.size() != menuNames.size()) {
			List<String> notFoundMenuNames = menuNames.stream()
				.filter(menuName -> !foodNames.contains(menuName))
				.toList();

			List<Food> foods = getFoodNutritionByAiApi(notFoundMenuNames, aiApiConfig.getNutritionSystemMessage(),
				aiApiConfig.getSchoolMealKey());
			existFoods.addAll(foods);
		}

		return existFoods.stream()
			.map(FoodResponse::from)
			.toList();
	}

	@Override
	public void saveDietNutrition(List<String> foodNames) {
		List<String> notFoundFoodNames = getNotExistsMenuName(foodNames);

		getFoodNutritionByAiApi(notFoundFoodNames, aiApiConfig.getNutritionSystemMessage(),
			aiApiConfig.getAnalyzeNutritionKey());
	}

	@Override
	public List<Food> saveSchoolMealNutrition(List<String> foodNames) {
		List<String> notFoundFoodNames = getNotExistsMenuName(foodNames);

		return getFoodNutritionByAiApi(notFoundFoodNames, aiApiConfig.getSchoolMealNutritionSystemMessage(),
			aiApiConfig.getSchoolMealKey());
	}

	private List<String> getNotExistsMenuName(List<String> foodNames) {
		if (foodNames.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}

		List<String> existingFoodNames = foodRepository.findExistingFoodNames(foodNames);
		return foodNames.stream()
			.filter(foodName -> !existingFoodNames.contains(foodName))
			.map(Object::toString)
			.toList();
	}

	private List<Food> getFoodNutritionByAiApi(List<String> foodInfos, String systemMessage, String apiKey) {
		String userMessage = String.join(", ", foodInfos);
		ApiRequest apiRequest = new ApiRequest(aiApiConfig.getModel(), systemMessage, userMessage);
		String cleanedJson = aiApiService.getCleanedJsonFromAiApiRequest(apiRequest, apiKey);

		ObjectMapper mapper = new ObjectMapper();
		List<NutritionInfo> nutritionInfos;
		try {
			nutritionInfos = mapper.readValue(cleanedJson, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("영양 성분을 분석하는데 실패했습니다. 다시 시도해주세요.");
		}
		if (nutritionInfos.isEmpty()) {
			throw new IllegalArgumentException("영양 성분을 분석하는데 실패했습니다. 다시 시도해주세요.");
		}

		List<Food> foods = nutritionInfos.stream()
			.map(NutritionInfo::toEntity)
			.toList();
		foodRepository.saveAll(foods);
		return foods;
	}
}
