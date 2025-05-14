package com.example.community_cr.mate.matching.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.mate.matching.entity.MealRequest;

public interface MatchRepository extends JpaRepository<MealRequest, Long> {
}
