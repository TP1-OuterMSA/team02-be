package com.example.community_cr.school_meal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/schoolMeal")
public class SchoolMealController {
	@GetMapping("/test")
	public ResponseEntity<Void> test() {
		return ResponseEntity.ok().build();
	}
}
