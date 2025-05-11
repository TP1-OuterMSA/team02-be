package com.example.community_cr.diet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.community_cr.diet.entity.Diet;
import com.example.community_cr.diet.entity.MealType;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
	@Query("SELECT DISTINCT d.date FROM Diet d WHERE d.date BETWEEN ?1 AND ?2 ORDER BY d.date DESC")
	List<LocalDate> findDietDatesBetween(LocalDate startDate, LocalDate endDate);

	List<Diet> findAllByDateAndUserId(LocalDate date, long userId);

	Optional<Diet> findByUserIdAndDateAndType(long userId, LocalDate date, MealType mealType);

	List<Diet> findAllByUserIdAndDateBetween(long userId, LocalDate startDate, LocalDate endDate);
}

