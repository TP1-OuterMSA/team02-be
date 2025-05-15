package com.example.community_cr.mealMatch.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.mealMatch.match.entity.MealPost;

public interface MealPostRepository extends JpaRepository<MealPost, Long> {
}
