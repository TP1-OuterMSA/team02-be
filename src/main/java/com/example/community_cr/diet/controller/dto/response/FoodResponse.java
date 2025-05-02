package com.example.community_cr.diet.controller.dto.response;

import com.example.community_cr.diet.controller.dto.response.api.FoodInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodResponse {
	private String foodName;
	private double foodWeight;
	private double kcal;

	public static FoodResponse from(FoodInfo foodInfo) {
		return FoodResponse.builder()
			.foodName(foodInfo.getName())
			.foodWeight(foodInfo.getWeight())
			.kcal(foodInfo.getKcal())
			.build();
	}
}
