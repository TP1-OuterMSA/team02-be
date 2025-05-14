package com.example.community_cr.school_meal.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.school_meal.component.NutritionProducer;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/schoolMeal")
public class SchoolMealController {
	private final NutritionProducer nutritionProducer;

	@GetMapping("/test")
	public ResponseEntity<Void> test(
		@RequestParam("startDate") LocalDate startDate,
		@RequestParam("endDate") LocalDate endDate
	) {
		nutritionProducer.sendMealItem(startDate, endDate);
		return ResponseEntity.ok().build();
	}
}
