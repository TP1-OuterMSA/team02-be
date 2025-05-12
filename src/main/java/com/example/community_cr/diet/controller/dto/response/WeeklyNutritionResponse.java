package com.example.community_cr.diet.controller.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WeeklyNutritionResponse {
	private List<WeeklyNutritionDto> nutritions;

	private String evaluate;
	private List<FoodResponse> recommendFoods;
}
