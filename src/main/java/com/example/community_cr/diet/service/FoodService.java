package com.example.community_cr.diet.service;

import java.util.List;

import com.example.community_cr.diet.controller.dto.request.FoodRequest;
import com.example.community_cr.diet.controller.dto.response.NewFoodResponse;

public interface FoodService {
	// List<FoodResponse> getFoods(int pageNo, int pageSize, String foodName);
	List<NewFoodResponse> getFoods(String foodName);

	// void saveNutrition(List<FoodRequest> foods);

	void saveNutritionTest(List<String> foods);

	void saveNutrition(List<FoodRequest> foodRequests);
}
