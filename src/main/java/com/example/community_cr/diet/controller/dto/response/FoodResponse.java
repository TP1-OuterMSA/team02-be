package com.example.community_cr.diet.controller.dto.response;

import com.example.community_cr.diet.controller.dto.response.api.FoodInfo;
import com.example.community_cr.diet.entity.Food;

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
			.foodName(foodInfo.getName().replace(" ", ""))
			.foodWeight(foodInfo.getWeight())
			.kcal(foodInfo.getKcal())
			.build();
	}

	public static FoodResponse from(Food food) {
		return FoodResponse.builder()
			.foodName(food.getFoodName())
			.foodWeight(food.getFoodWeight())
			.kcal(food.getKcal())
			.build();
	}
}
