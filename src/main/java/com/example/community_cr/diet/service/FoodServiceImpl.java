package com.example.community_cr.diet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.community_cr.diet.controller.dto.request.FoodRequest;
import com.example.community_cr.diet.controller.dto.request.api.ApiRequest;
import com.example.community_cr.diet.controller.dto.response.FoodResponse;
import com.example.community_cr.diet.controller.dto.response.api.AiResponse;
import com.example.community_cr.diet.controller.dto.response.api.FoodInfo;
import com.example.community_cr.diet.controller.dto.response.api.NutritionInfo;
import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.repository.FoodRepository;
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
	private final RestTemplate restTemplate;
	private final FoodRepository foodRepository;

	@Value("${api.ai.key}")
	private String aiApiKey;

	@Value("${api.ai.model}")
	private String aiApiModel;

	@Value("${api.ai.url}")
	private String aiApiUrl;

	@Value("${api.ai.nutrition-system-message}")
	private String aiApiNutritionSystemMessage;

	@Value("${api.ai.food-system-message-format}")
	private String aiApiFoodSystemMessageFormat;

	@Override
	public List<FoodResponse> getFoods(int count, String foodName) {
		if (foodName.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}
		String aiApiFoodSystemMessage = String.format(aiApiFoodSystemMessageFormat, count);

		ApiRequest apiRequest = new ApiRequest(aiApiModel, aiApiFoodSystemMessage, foodName);
		String cleanedJson = getCleanedJsonFromAiApiRequest(apiRequest);

		ObjectMapper mapper = new ObjectMapper();
		List<FoodInfo> foodInfos;
		try {
			foodInfos = mapper.readValue(cleanedJson, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("메뉴를 불러오는데 실패했습니다. 다시 시도해주세요.");
		}
		if (foodInfos.isEmpty()) {
			throw new IllegalArgumentException("메뉴를 불러오는데 실패했습니다. 다시 시도해주세요.");
		}

		return foodInfos.stream()
			.map(FoodResponse::from)
			.toList();
	}

	@Override
	public void saveNutrition(List<FoodRequest> foodRequests) {
		if (foodRequests.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}

		List<String> foodNames = foodRequests.stream()
			.map(FoodRequest::getFoodName)
			.toList();
		List<String> existingFoodNames = foodRepository.findExistingFoodNames(foodNames);
		List<String> notFoundFoodRequests = foodRequests.stream()
			.filter(foodRequest -> !existingFoodNames.contains(foodRequest.getFoodName()))
			.map(Object::toString)
			.toList();

		if (notFoundFoodRequests.isEmpty()) {
			return;
		}

		String userMessage = String.join(", ", notFoundFoodRequests);
		ApiRequest apiRequest = new ApiRequest(aiApiModel, aiApiNutritionSystemMessage, userMessage);
		String cleanedJson = getCleanedJsonFromAiApiRequest(apiRequest);

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
	}

	private String getCleanedJsonFromAiApiRequest(ApiRequest apiRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + aiApiKey);

		HttpEntity<ApiRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
		AiResponse aiApiResponse = restTemplate.postForObject(aiApiUrl, requestEntity, AiResponse.class);

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
