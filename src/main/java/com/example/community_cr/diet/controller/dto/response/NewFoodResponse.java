package com.example.community_cr.diet.controller.dto.response;

import com.example.community_cr.diet.controller.dto.response.api.FoodInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewFoodResponse {
	private String foodName;
	private double foodWeight;
	private double kcal;

	public static NewFoodResponse from(FoodInfo foodInfo) {
		return NewFoodResponse.builder()
			.foodName(foodInfo.getName())
			.foodWeight(foodInfo.getWeight())
			.kcal(foodInfo.getKcal())
			.build();
	}
}
