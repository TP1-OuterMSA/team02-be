package com.example.community_cr.food.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.example.community_cr.food.controller.dto.FoodApiResponse;
import com.example.community_cr.food.controller.dto.FoodResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodServiceImpl implements FoodService {

	private final RestTemplate restTemplate;

	@Value("${api.food.key}")
	private String apiKey;

	@Value("${api.food.base}")
	private String apiBase;

	@Value("${api.food.path}")
	private String apiPath;

	@Override
	public Optional<List<FoodResponse>> getFoods(int pageNo, int pageSize) {
		String apiUrl = String.format(
			"%s%s",
			apiBase,
			apiPath
		);

		String url = UriComponentsBuilder.fromUriString(apiUrl)
			.queryParam("service_Type", "json")
			.queryParam("Page_No", pageNo)
			.queryParam("Page_Size", pageSize)
			.queryParam("serviceKey", apiKey)
			.build(true)
			.toUriString();
		return foodApiResponseToFoodResponse(url);
	}

	@Override
	public Optional<List<FoodResponse>> getFoods(int pageNo, int pageSize, String foodName) {
		String apiUrl = String.format(
			"%s%s",
			apiBase,
			apiPath
		);
		String encodedFoodName = UriUtils.encode(foodName, StandardCharsets.UTF_8);

		String url = UriComponentsBuilder.fromUriString(apiUrl)
			.queryParam("service_Type", "json")
			.queryParam("Page_No", pageNo)
			.queryParam("Page_Size", pageSize)
			.queryParam("food_Name", encodedFoodName)
			.queryParam("serviceKey", apiKey)
			.build(true)
			.toUriString();
		return foodApiResponseToFoodResponse(url);
	}

	private Optional<List<FoodResponse>> foodApiResponseToFoodResponse(String uri) {
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

		return Optional.of(foodApiResponse.getResponse().getList().stream()
			.map(FoodResponse::from)
			.toList());
	}
}
