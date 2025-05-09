package com.example.community_cr.common.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.community_cr.common.config.AiApiConfig;
import com.example.community_cr.common.exception.ApiErrorException;
import com.example.community_cr.diet.controller.dto.request.api.ApiRequest;
import com.example.community_cr.diet.controller.dto.response.api.AiResponse;
import com.example.community_cr.diet.controller.dto.response.api.ApiErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class AiApiService {
	private final AiApiConfig aiApiConfig;
	private final RestTemplate restTemplate;

	public String getCleanedJsonFromAiApiRequest(ApiRequest apiRequest, String apiKey) {
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
}
