package com.example.community_cr.diet.service;

import java.util.List;

import com.example.community_cr.diet.controller.dto.request.FoodRequest;
import com.example.community_cr.diet.controller.dto.response.FoodResponse;

public interface FoodService {
	List<FoodResponse> getFoods(int count, String foodName);

	void saveNutrition(List<FoodRequest> foodRequests);
}
