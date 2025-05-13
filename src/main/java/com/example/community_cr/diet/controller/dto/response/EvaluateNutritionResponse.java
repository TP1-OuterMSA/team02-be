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
public class EvaluateNutritionResponse {
	private String evaluate;
	private List<FoodResponse> recommendFoods;

	public static EvaluateNutritionResponse of(String evaluate, List<FoodResponse> recommendFoods) {
		return EvaluateNutritionResponse.builder()
			.evaluate(evaluate)
			.recommendFoods(recommendFoods)
			.build();
	}
}
