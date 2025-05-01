package com.example.community_cr.diet.controller.dto.response.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NutritionInfo {
	private int id;
	private String name;
	private double weight;
	private double kcal;
	private double carb;
	private double protein;
	private double fat;
	private double vitamin;
	private double calcium;
}

