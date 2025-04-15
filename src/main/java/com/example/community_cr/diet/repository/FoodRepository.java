package com.example.community_cr.diet.repository;

import com.example.community_cr.diet.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByFoodCode(String foodCode);
}
