package com.example.community_cr.diet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.diet.entity.DietFood;

public interface DietFoodRepository extends JpaRepository<DietFood, Long> {
	boolean existsByDietId(long dietId);
}
