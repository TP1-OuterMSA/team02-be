package com.example.community_cr.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "api.ai")
public class AiApiConfig {
	private String searchFoodKey;
	private String analyzeNutritionKey;
	private String schoolMealKey;

	private String nutritionSystemMessage;
	private String schoolMealNutritionSystemMessage;
	private String foodSystemMessageFormat;

	private String model;
	private String url;
}
