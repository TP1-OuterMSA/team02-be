package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.community_cr.common.config.AiApiConfig;
import com.example.community_cr.common.exception.ApiErrorException;
import com.example.community_cr.diet.controller.dto.request.api.ApiRequest;
import com.example.community_cr.diet.controller.dto.response.FoodResponse;
import com.example.community_cr.diet.controller.dto.response.api.AiResponse;
import com.example.community_cr.diet.controller.dto.response.api.ApiErrorResponse;
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
	private final RestTemplate restTemplate;

	private final FoodRepository foodRepository;
	private final MealRepository mealRepository;

	@Override
	public List<FoodResponse> getFoods(int count, String foodName) {
		if (foodName.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}
		String aiApiFoodSystemMessage = String.format(aiApiConfig.getFoodSystemMessageFormat(), count);

		ApiRequest apiRequest = new ApiRequest(aiApiConfig.getModel(), aiApiFoodSystemMessage, foodName);
		String cleanedJson = getCleanedJsonFromAiApiRequest(apiRequest, aiApiConfig.getSearchFoodKey());

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
		String cleanedJson = getCleanedJsonFromAiApiRequest(apiRequest, apiKey);

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

	private void processApiResponseError(String aiApiResponseContent) {
		ObjectMapper mapper = new ObjectMapper();
		ApiErrorResponse apiErrorResponse;
		try {
			apiErrorResponse = mapper.readValue(aiApiResponseContent, ApiErrorResponse.class);
		} catch (JsonProcessingException e) {
			throw new ApiErrorException(null,
				"API 응답 처리 중 알 수 없는 오류가 발생했습니다. 다시 시도해주세요.\n message: " + aiApiResponseContent);
		}
		assert apiErrorResponse != null;
		if (apiErrorResponse.getError().getCode() == 429) {
			throw new ApiErrorException(429, "API 일일 한도가 초과되었습니다. 내일 다시 시도해주세요.");
		}
		throw new ApiErrorException(apiErrorResponse);
	}

	private String getCleanedJsonFromAiApiRequest(ApiRequest apiRequest, String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + apiKey);

		HttpEntity<ApiRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
		String aiApiResponseContent = restTemplate.postForObject(aiApiConfig.getUrl(), requestEntity, String.class);

		ObjectMapper mapper = new ObjectMapper();
		AiResponse aiApiResponse;
		try {
			aiApiResponse = mapper.readValue(aiApiResponseContent, AiResponse.class);
		} catch (JsonProcessingException e) {
			log.info("start : {}", aiApiResponseContent);
			processApiResponseError(aiApiResponseContent);
			log.info("end");
			throw new ApiErrorException(null,
				"API 응답 처리 중 알 수 없는 오류가 발생했습니다. 다시 시도해주세요.\n message: " + aiApiResponseContent);
		}

		assert aiApiResponse != null;
		assert aiApiResponse.getChoices()
			.stream()
			.map(AiResponse.Choice::getMessage)
			.map(AiResponse.Message::getContent)
			.toList()
			.get(0) != null;

		String content = aiApiResponse.getChoices()
			.stream()
			.map(AiResponse.Choice::getMessage)
			.map(AiResponse.Message::getContent)
			.toList()
			.get(0);

		return content
			.replace("```json", "")
			.replace("```", "")
			.trim()
			.replace("'", "\""); // '짜장면' → "짜장면"
	}
}
