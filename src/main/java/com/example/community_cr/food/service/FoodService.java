package com.example.community_cr.food.service;

import java.util.List;
import java.util.Optional;

import com.example.community_cr.food.controller.dto.FoodResponse;

public interface FoodService {
	Optional<List<FoodResponse>> getFoods(int pageNo, int pageSize);

	Optional<List<FoodResponse>> getFoods(int pageNo, int pageSize, String foodName);
}
