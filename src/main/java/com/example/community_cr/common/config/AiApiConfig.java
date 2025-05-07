package com.example.community_cr.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class AiApiConfig {
	@Value("${api.ai.search-food-key}")
	private String aiSearchFoodKey;
	@Value("${api.ai.get-nutrition-key}")
	private String aiNutritionKey;
	@Value("${api.ai.school-meal-key}")
	private String aiSchoolMealKey;
	@Value("${api.ai.nutrition-system-message}")
	private String nutritionSystemMessage;
	@Value("${api.ai.school-meal-nutrition-system-message}")
	private String schoolMealNutritionSystemMessage;
	@Value("${api.ai.food-system-message-format}")
	private String foodSystemMessageFormat;
	@Value("${api.ai.model}")
	private String aiModel;
	@Value("${api.ai.url}")
	private String aiUrl;
}
