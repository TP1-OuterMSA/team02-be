package com.example.community_cr.diet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.diet.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
	// List<Food> findAllByFoodCodeIn(List<String> foods);
	//
	// @Query("SELECT f.foodCode FROM Food f WHERE f.foodCode IN :foodCodes")
	// List<String> findExistingFoodCodes(@Param("foodCodes") List<String> foodCodes);
	List<Food> findAllByFoodNameIn(List<String> foodNames);
}
