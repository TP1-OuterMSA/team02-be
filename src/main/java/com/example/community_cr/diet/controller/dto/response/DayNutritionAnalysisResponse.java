package com.example.community_cr.diet.controller.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayNutritionAnalysisResponse {
	private List<NutritionAnalysisResponse> nutrition;
	private String evaluate;
	private List<FoodResponse> recommendFoods;

	public static DayNutritionAnalysisResponse from(List<NutritionAnalysisResponse> nutrition, String evaluate,
		List<FoodResponse> recommendFoods) {
		return DayNutritionAnalysisResponse.builder()
			.nutrition(nutrition)
			.evaluate(evaluate)
			.recommendFoods(recommendFoods)
			.build();
	}

	public static DayNutritionAnalysisResponse empty() {
		return DayNutritionAnalysisResponse.builder()
			.nutrition(List.of())
			.evaluate("내용이 존재하지 않습니다.")
			.recommendFoods(List.of())
			.build();
	}
}
