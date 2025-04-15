package com.example.community_cr.food.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodResponse {
	private String foodCode;
	private String upperFoodGroupName;
	private String foodGroupName;
	private String foodName;
	private String foodWeight;

	public static FoodResponse from(FoodItem foodItem) {
		return FoodResponse.builder()
			.foodCode(foodItem.getFd_Code())
			.upperFoodGroupName(foodItem.getUpper_Fd_Grupp_Nm())
			.foodGroupName(foodItem.getFd_Grupp_Nm())
			.foodName(foodItem.getFd_Nm())
			.foodWeight(foodItem.getFd_Wgh())
			.build();
	}
}
