package com.example.community_cr.school_meal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.diet.entity.MealType;
import com.example.community_cr.school_meal.entity.Meal;

public interface MealRepository extends JpaRepository<Meal, Long> {
	Optional<Meal> findByDayInfoAndMealType(String date, MealType mealType);
}
