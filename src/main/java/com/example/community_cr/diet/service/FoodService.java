package com.example.community_cr.diet.service;

import java.util.List;

import com.example.community_cr.diet.controller.dto.response.FoodResponse;

public interface FoodService {
	List<FoodResponse> getFoods(int pageNo, int pageSize);

	List<FoodResponse> getFoods(int pageNo, int pageSize, String foodName);

	void saveNutrition(List<String> foodCodes);
}
