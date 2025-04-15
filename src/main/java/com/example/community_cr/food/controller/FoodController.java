package com.example.community_cr.food.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.food.controller.dto.FoodResponse;
import com.example.community_cr.food.service.FoodService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/diet")
@RequiredArgsConstructor
public class FoodController {
	private final FoodService foodService;

	@GetMapping("/getFoods")
	public ResponseEntity<List<FoodResponse>> getFoods(
		@RequestParam("pageNo") final int pageNo,
		@RequestParam("pageSize") final int pageSize,
		@RequestParam(name = "foodName", required = false) Optional<String> foodName) {
		Optional<List<FoodResponse>> foodResponses;
		if (foodName.isPresent()) {
			foodResponses = foodService.getFoods(pageNo, pageSize, foodName.get());
		} else {
			foodResponses = foodService.getFoods(pageNo, pageSize);
		}
		return ResponseEntity.ok(
			foodResponses.orElseThrow(IllegalArgumentException::new));
	}
}
