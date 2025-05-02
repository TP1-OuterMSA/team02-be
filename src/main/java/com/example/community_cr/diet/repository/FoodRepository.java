package com.example.community_cr.diet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.diet.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
	@Query("SELECT f.foodName FROM Food f WHERE f.foodName IN :foodNames")
	List<String> findExistingFoodNames(@Param("foodNames") List<String> foodNames);

	List<Food> findAllByFoodNameIn(List<String> foodNames);
}
