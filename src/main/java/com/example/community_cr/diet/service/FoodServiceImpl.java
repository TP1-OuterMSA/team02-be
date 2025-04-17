package com.example.community_cr.diet.service;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.example.community_cr.diet.controller.dto.response.FoodResponse;
import com.example.community_cr.diet.controller.dto.response.api.Nutrition.NutritionApiResponse;
import com.example.community_cr.diet.controller.dto.response.api.Nutrition.NutritionItem;
import com.example.community_cr.diet.controller.dto.response.api.food.FoodApiResponse;
import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.repository.FoodRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FoodServiceImpl implements FoodService {

	private final RestTemplate restTemplate;
	private final FoodRepository foodRepository;

	@Value("${api.food.key}")
	private String apiKey;

	@Value("${api.food.base}")
	private String apiBase;

	@Value("${api.food.foodPath}")
	private String apiFoodPath;

	@Value("${api.food.nutritionPath}")
	private String apiNutritionPath;

	@Override
	public List<FoodResponse> getFoods(int pageNo, int pageSize) {
		String apiUrl = String.format(
			"%s%s",
			apiBase,
			apiFoodPath
		);

		String url = UriComponentsBuilder.fromUriString(apiUrl)
			.queryParam("service_Type", "json")
			.queryParam("Page_No", pageNo)
			.queryParam("Page_Size", pageSize)
			.queryParam("serviceKey", apiKey)
			.build(true)
			.toUriString();
		return getFoodResponseFromUrl(url);
	}

	@Override
	public List<FoodResponse> getFoods(int pageNo, int pageSize, String foodName) {
		String apiUrl = String.format(
			"%s%s",
			apiBase,
			apiFoodPath
		);
		String encodedFoodName = UriUtils.encode(foodName, StandardCharsets.UTF_8);

		String uri = UriComponentsBuilder.fromUriString(apiUrl)
			.queryParam("service_Type", "json")
			.queryParam("Page_No", pageNo)
			.queryParam("Page_Size", pageSize)
			.queryParam("food_Name", encodedFoodName)
			.queryParam("serviceKey", apiKey)
			.build(true)
			.toUriString();
		return getFoodResponseFromUrl(uri);
	}

	@Override
	public void saveNutrition(List<String> foodCodes) {
		String apiUrl = String.format(
			"%s%s",
			apiBase,
			apiNutritionPath
		);

		List<String> existingFoodCodes = foodRepository.findExistingFoodCodes(foodCodes);

		List<String> notFoundFoodCodes = foodCodes.stream()
			.filter(foodCode -> !existingFoodCodes.contains(foodCode))
			.toList();

		List<String> uris = notFoundFoodCodes.stream()
			.map(foodCode -> UriComponentsBuilder.fromUriString(apiUrl)
				.queryParam("serviceKey", apiKey)
				.queryParam("food_Code", foodCode)
				.build(true)
				.toUriString())
			.toList();

		List<CompletableFuture<NutritionApiResponse>> futures = uris.stream()
			.map(this::fetchData)
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		List<Food> foods = futures.stream()
			.map(CompletableFuture::join)
			.map(NutritionApiResponse::getNutritionBody)
			.map(NutritionApiResponse.NutritionBody::getNutritionItems)
			.map(NutritionApiResponse.NutritionItems::getNutritionItem)
			.map(NutritionItem::toEntity)
			.toList();

		foodRepository.saveAll(foods);
	}

	private List<FoodResponse> getFoodResponseFromUrl(String uri) {
		ResponseEntity<String> response;
		try {
			response =
				restTemplate.exchange(new URI(uri), HttpMethod.GET, null, String.class);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException();
		}

		FoodApiResponse foodApiResponse;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			foodApiResponse = objectMapper.readValue(response.getBody(), FoodApiResponse.class);
		} catch (JsonProcessingException e) {
			if (response.getStatusCode().is2xxSuccessful()) {
				throw new IllegalArgumentException("해당하는 음식 정보가 존재하지 않습니다.");
			}
			throw new IllegalArgumentException();
		}

		assert foodApiResponse != null;
		assert foodApiResponse.getResponse() != null;

		return foodApiResponse.getResponse().getList().stream()
			.map(FoodResponse::from)
			.toList();
	}

	// 병렬적으로 url 수행하는 메서드
	@Async
	protected CompletableFuture<NutritionApiResponse> fetchData(String url) {
		ResponseEntity<String> response;
		try {
			response =
				restTemplate.exchange(new URI(url), HttpMethod.GET, null, String.class);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}

		NutritionApiResponse nutritionApiResponse;
		try {
			JAXBContext context = JAXBContext.newInstance(NutritionApiResponse.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new IllegalArgumentException("응답을 받아오는데 실패했습니다.");
			}
			StringReader reader = new StringReader(Objects.requireNonNull(response.getBody()));

			nutritionApiResponse = (NutritionApiResponse)unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
		return CompletableFuture.completedFuture(nutritionApiResponse);
	}
}
