package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.util.List;

import com.example.community_cr.diet.controller.dto.response.FoodResponse;
import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.entity.MealType;

public interface FoodService {
	List<FoodResponse> getFoods(int count, String foodName);

	void saveDietNutrition(List<String> foodNames);

	List<Food> saveSchoolMealNutrition(List<String> foodNames);

	List<FoodResponse> getSchoolMeal(LocalDate date, MealType mealType);
}
