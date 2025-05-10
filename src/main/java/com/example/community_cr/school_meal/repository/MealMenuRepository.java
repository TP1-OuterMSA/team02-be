package com.example.community_cr.school_meal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.school_meal.entity.MealMenu;

public interface MealMenuRepository extends JpaRepository<MealMenu, Long> {
}
