package com.example.community_cr.diet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.community_cr.diet.controller.dto.request.ApiRequest;
import com.example.community_cr.diet.controller.dto.request.FoodRequest;
import com.example.community_cr.diet.controller.dto.response.NewFoodResponse;
import com.example.community_cr.diet.controller.dto.response.api.ApiResponse;
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

	// @Value("${api.food.key}")
	// private String apiKey;
	//
	// @Value("${api.food.base}")
	// private String apiBase;
	//
	// @Value("${api.food.foodPath}")
	// private String apiFoodPath;
	//
	// @Value("${api.food.nutritionPath}")
	// private String apiNutritionPath;

	@Value("${api.ai.key}")
	private String aiApiKey;

	@Value("${api.ai.model}")
	private String aiApiModel;

	@Value("${api.ai.url}")
	private String aiApiUrl;

	@Value("${api.ai.nutrition-system-message}")
	private String aiApiNutritionSystemMessage;

	@Value("${api.ai.food-system-message}")
	private String aiApiFoodSystemMessage;

	// @Override
	// public List<FoodResponse> getFoods(int pageNo, int pageSize) {
	// 	String apiUrl = String.format(
	// 		"%s%s",
	// 		apiBase,
	// 		apiFoodPath
	// 	);
	//
	// 	String uri = UriComponentsBuilder.fromUriString(apiUrl)
	// 		.queryParam("service_Type", "json")
	// 		.queryParam("Page_No", pageNo)
	// 		.queryParam("Page_Size", pageSize)
	// 		.queryParam("serviceKey", apiKey)
	// 		.build(true)
	// 		.toUriString();
	//
	// 	List<FoodResponse> foodResponses = getFoodResponseFromUrl(uri);
	// 	return setFoodResponsesKcal(foodResponses);
	// }
	//
	// @Override
	// public List<FoodResponse> getFoods(int pageNo, int pageSize, String foodName) {
	// 	// 메뉴 조회
	// 	String foodApiUrl = String.format(
	// 		"%s%s",
	// 		apiBase,
	// 		apiFoodPath
	// 	);
	// 	String encodedFoodName = UriUtils.encode(foodName, StandardCharsets.UTF_8);
	//
	// 	String uri = UriComponentsBuilder.fromUriString(foodApiUrl)
	// 		.queryParam("service_Type", "json")
	// 		.queryParam("Page_No", pageNo)
	// 		.queryParam("Page_Size", pageSize)
	// 		.queryParam("food_Name", encodedFoodName)
	// 		.queryParam("serviceKey", apiKey)
	// 		.build(true)
	// 		.toUriString();
	//
	// 	List<FoodResponse> foodResponses = getFoodResponseFromUrl(uri);
	// 	return setFoodResponsesKcal(foodResponses);
	// }
	//
	// private List<FoodResponse> setFoodResponsesKcal(List<FoodResponse> foodResponses) {
	// 	// 메뉴별 영양 성분 조회 (칼로리 확인을 위해)
	// 	List<String> foodCodes = foodResponses.stream()
	// 		.map(FoodResponse::getFoodCode)
	// 		.toList();
	// 	List<NutritionItem> nutritionItems = getNutritionItems(foodCodes);
	//
	// 	Map<String, NutritionItem> nutritionMap = nutritionItems.stream()
	// 		.collect(Collectors.toMap(NutritionItem::getMainFoodCode, Function.identity()));
	//
	// 	foodResponses.forEach(foodResponse -> {
	// 		NutritionItem matched = nutritionMap.get(foodResponse.getFoodCode());
	// 		if (matched != null) {
	// 			double matchedFoodWeight = matched.getIdntLists().stream()
	// 				.mapToDouble(IdntList::getFoodWeight)
	// 				.sum();
	// 			double matchedKcal = matched.getIdntLists().stream()
	// 				.mapToDouble(IdntList::getEnergyQy)
	// 				.sum();
	// 			double kcal =
	// 				(foodResponse.getFoodWeight() / matchedFoodWeight) * matchedKcal;
	// 			foodResponse.setKcal(kcal);
	// 		}
	// 	});
	//
	// 	return foodResponses;
	// }
	//
	// @Override
	// public void saveNutrition(List<FoodRequest> foodRequests) {
	// 	List<String> foodCodes = foodRequests.stream()
	// 		.map(FoodRequest::getFoodCode)
	// 		.toList();
	// 	List<String> existingFoodCodes = foodRepository.findExistingFoodCodes(foodCodes);
	//
	// 	List<String> notFoundFoodCodes = foodCodes.stream()
	// 		.filter(foodCode -> !existingFoodCodes.contains(foodCode))
	// 		.toList();
	//
	// 	List<Food> foods = getNutritionItems(notFoundFoodCodes).stream()
	// 		.map(NutritionItem::toEntity)
	// 		.toList();
	//
	// 	foodRepository.saveAll(foods);
	// }

	@Override //테스트 못해봤음
	public List<NewFoodResponse> getFoods(String foodName) {
		if (foodName.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}

		ApiRequest apiRequest = new ApiRequest(aiApiModel, aiApiFoodSystemMessage, foodName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + aiApiKey);

		HttpEntity<ApiRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
		ApiResponse apiResponse = restTemplate.postForObject(aiApiUrl, requestEntity, ApiResponse.class);

		assert apiResponse != null;
		assert apiResponse.getChoices()
			.stream()
			.map(ApiResponse.Choice::getMessage)
			.map(ApiResponse.Message::getContent)
			.toList()
			.get(0) != null;

		String content = apiResponse.getChoices()
			.stream()
			.map(ApiResponse.Choice::getMessage)
			.map(ApiResponse.Message::getContent)
			.toList()
			.get(0);

		String cleanedJson = content
			.replace("```json", "")
			.replace("```", "")
			.trim()
			.replace("'", "\""); // '짜장면' → "짜장면"

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
			.map(NewFoodResponse::from)
			.toList();
	}

	@Override
	public void saveNutritionTest(List<String> foodNames) {
		if (foodNames.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}

		String userMessage = String.join(", ", foodNames);
		ApiRequest apiRequest = new ApiRequest(aiApiModel, aiApiNutritionSystemMessage, userMessage);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + aiApiKey);

		HttpEntity<ApiRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
		ApiResponse apiResponse = restTemplate.postForObject(aiApiUrl, requestEntity, ApiResponse.class);

		assert apiResponse != null;
		assert apiResponse.getChoices()
			.stream()
			.map(ApiResponse.Choice::getMessage)
			.map(ApiResponse.Message::getContent)
			.toList()
			.get(0) != null;

		String content = apiResponse.getChoices()
			.stream()
			.map(ApiResponse.Choice::getMessage)
			.map(ApiResponse.Message::getContent)
			.toList()
			.get(0);

		String cleanedJson = content
			.replace("```json", "")
			.replace("```", "")
			.trim()
			.replace("'", "\""); // '짜장면' → "짜장면"

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

		// 여기부터는 테스트 못해봤음
		List<Food> foods = nutritionInfos.stream()
			.map(nutritionInfo ->
				Food.builder()
					.foodWeight(nutritionInfo.getWeight())
					.carb(nutritionInfo.getCarb())
					.foodName(nutritionInfo.getName())
					.protein(nutritionInfo.getProtein())
					.fat(nutritionInfo.getFat())
					.kcal(nutritionInfo.getKcal())
					.build())
			.toList();
		foodRepository.saveAll(foods);
	}

	@Override
	public void saveNutrition(List<FoodRequest> foodRequests) {
		List<String> foodNames = foodRequests.stream()
			.map(FoodRequest::getFoodName)
			.toList();

		if (foodNames.isEmpty()) {
			throw new IllegalArgumentException("음식이 입력되지 않았습니다.");
		}

		String userMessage = String.join(", ", foodNames);
		ApiRequest apiRequest = new ApiRequest(aiApiModel, aiApiNutritionSystemMessage, userMessage);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + aiApiKey);

		HttpEntity<ApiRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
		ApiResponse apiResponse = restTemplate.postForObject(aiApiUrl, requestEntity, ApiResponse.class);

		assert apiResponse != null;
		assert apiResponse.getChoices()
			.stream()
			.map(ApiResponse.Choice::getMessage)
			.map(ApiResponse.Message::getContent)
			.toList()
			.get(0) != null;

		String content = apiResponse.getChoices()
			.stream()
			.map(ApiResponse.Choice::getMessage)
			.map(ApiResponse.Message::getContent)
			.toList()
			.get(0);

		String cleanedJson = content
			.replace("```json", "")
			.replace("```", "")
			.trim()
			.replace("'", "\""); // '짜장면' → "짜장면"

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

		// 여기부터는 테스트 못해봤음
		List<Food> foods = nutritionInfos.stream()
			.map(nutritionInfo ->
				Food.builder()
					.foodWeight(nutritionInfo.getWeight())
					.carb(nutritionInfo.getCarb())
					.foodName(nutritionInfo.getName())
					.protein(nutritionInfo.getProtein())
					.fat(nutritionInfo.getFat())
					.kcal(nutritionInfo.getKcal())
					.build())
			.toList();
		foodRepository.saveAll(foods);
	}

	// private List<FoodResponse> getFoodResponseFromUrl(String uri) {
	// 	ResponseEntity<String> response;
	// 	try {
	// 		response =
	// 			restTemplate.exchange(new URI(uri), HttpMethod.GET, null, String.class);
	// 	} catch (URISyntaxException e) {
	// 		throw new IllegalStateException("외부 API 요청 중 URL 생성에 문제가 생겼습니다.");
	// 	}
	//
	// 	FoodApiResponse foodApiResponse;
	// 	try {
	// 		ObjectMapper objectMapper = new ObjectMapper();
	// 		foodApiResponse = objectMapper.readValue(response.getBody(), FoodApiResponse.class);
	// 	} catch (JsonProcessingException e) {
	// 		if (!response.getStatusCode().is2xxSuccessful()) {
	// 			throw new IllegalStateException("외부 API 요청 중 문제가 생겼습니다.");
	// 		}
	//
	// 		assert response.getBody() != null;
	// 		Pattern errorPattern = Pattern.compile("<returnReasonCode>(\\d+)</returnReasonCode>");
	// 		Matcher errorMatcher = errorPattern.matcher(response.getBody());
	// 		Pattern resultPattern = Pattern.compile("\"resultCode\":\"(\\d+)\"");
	// 		Matcher resultMatcher = resultPattern.matcher(response.getBody());
	//
	// 		String code;
	// 		if (errorMatcher.find()) {
	// 			code = errorMatcher.group(1);
	// 			throw new IllegalArgumentException(
	// 				"음식조회 API 에러 발생\n에러 코드: : " + code
	// 					+ "\n(4: HTTP 에러\n22: API 제한 횟수 초과\n23: 동시 요청 제한 횟수 초과\n30: 잘못된 서비스)");
	// 		} else if (resultMatcher.find()) {
	// 			code = resultMatcher.group(1);
	// 			if (Integer.parseInt(code) == 301) {
	// 				return List.of();
	// 			} else {
	// 				throw new IllegalArgumentException("결과 코드 : " + code);
	// 			}
	// 		} else {
	// 			throw new IllegalStateException("공공데이터 api 응답을 처리하던 중 문제가 발생했습니다.\n" + response.getBody());
	// 		}
	// 	}
	//
	// 	assert foodApiResponse != null;
	// 	assert foodApiResponse.getResponse() != null;
	//
	// 	return foodApiResponse.getResponse().getList().stream()
	// 		.map(FoodResponse::from)
	// 		.toList();
	// }
	//
	// private List<NutritionItem> getNutritionItems(List<String> foodCodes) {
	// 	String nutritionApiUrl = String.format(
	// 		"%s%s",
	// 		apiBase,
	// 		apiNutritionPath
	// 	);
	//
	// 	List<String> uris = foodCodes.stream()
	// 		.map(foodCode -> UriComponentsBuilder.fromUriString(nutritionApiUrl)
	// 			.queryParam("serviceKey", apiKey)
	// 			.queryParam("food_Code", foodCode)
	// 			.build(true)
	// 			.toUriString())
	// 		.toList();
	//
	// 	List<CompletableFuture<NutritionApiResponse>> futures = uris.stream()
	// 		.map(this::getNutritionApiResponseFromUrl)
	// 		.filter(Objects::nonNull)
	// 		.toList();
	//
	// 	CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	//
	// 	return futures.stream()
	// 		.map(CompletableFuture::join)
	// 		.map(NutritionApiResponse::getNutritionBody)
	// 		.map(NutritionApiResponse.NutritionBody::getNutritionItems)
	// 		.map(NutritionApiResponse.NutritionItems::getNutritionItem)
	// 		.toList();
	// }
	//
	// // 병렬적으로 url 수행하는 메서드
	// @Async
	// protected CompletableFuture<NutritionApiResponse> getNutritionApiResponseFromUrl(String url) {
	// 	ResponseEntity<String> response;
	// 	try {
	// 		response =
	// 			restTemplate.exchange(new URI(url), HttpMethod.GET, null, String.class);
	// 	} catch (URISyntaxException e) {
	// 		throw new IllegalStateException("외부 API 요청 중 URL 생성에 문제가 생겼습니다.");
	// 	}
	//
	// 	NutritionApiResponse nutritionApiResponse;
	// 	try {
	// 		if (!response.getStatusCode().is2xxSuccessful()) {
	// 			throw new IllegalArgumentException("응답을 받아오는데 실패했습니다.");
	// 		}
	// 		StringReader reader = new StringReader(Objects.requireNonNull(response.getBody()));
	//
	// 		JAXBContext context = JAXBContext.newInstance(NutritionApiResponse.class);
	// 		Unmarshaller unmarshaller = context.createUnmarshaller();
	// 		nutritionApiResponse = (NutritionApiResponse)unmarshaller.unmarshal(reader);
	// 	} catch (JAXBException e) {
	// 		if (!response.getStatusCode().is2xxSuccessful()) {
	// 			throw new IllegalStateException("외부 API 요청 중 문제가 생겼습니다.");
	// 		}
	//
	// 		Pattern pattern = Pattern.compile("<returnReasonCode>(\\d+)</returnReasonCode>");
	// 		Matcher matcher = pattern.matcher(response.getBody());
	//
	// 		if (matcher.find()) {
	// 			String code = matcher.group(1);
	// 			throw new IllegalArgumentException(
	// 				"영양분석 API 에러 발생\n에러 코드: : " + code
	// 					+ "\n(4: HTTP 에러\n22: API 제한 횟수 초과\n23: 동시 요청 제한 횟수 초과\n30: 잘못된 서비스)");
	// 		} else {
	// 			return null;
	// 		}
	// 	}
	//
	// 	return CompletableFuture.completedFuture(nutritionApiResponse);
	// }
}
