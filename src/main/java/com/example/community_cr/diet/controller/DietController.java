package com.example.community_cr.diet.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
import com.example.community_cr.diet.controller.dto.response.DietResponse;
import com.example.community_cr.diet.controller.dto.response.FoodResponse;
import com.example.community_cr.diet.service.DietService;
import com.example.community_cr.diet.service.FoodService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet")
public class DietController {
	private final DietService dietService;
	private final FoodService foodService;

	@PostMapping("/saveDiet")
	public ResponseEntity<DietResponse> createDiet(
		@RequestHeader("user-id") long userId,
		@RequestBody @Valid DietRequest dto) {
		foodService.saveNutrition(dto.getFoods());
		Optional<DietResponse> response = dietService.saveDiet(userId, dto);
		return ResponseEntity.ok(
			response.orElseThrow(IllegalArgumentException::new));
	}

	@GetMapping("/getDiet")
	public ResponseEntity<DietResponse> getDiet(
		@RequestHeader("user-id") long userId,
		@RequestParam("dietId") long dietId) {
		Optional<DietResponse> response = dietService.getDiet(userId, dietId);
		return ResponseEntity.ok(
			response.orElseThrow(IllegalArgumentException::new));
	}

	@GetMapping("/getDiets")
	public ResponseEntity<List<DietResponse>> getDiets(
		@RequestHeader("user-id") long userId,
		@RequestParam(name = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(name = "count", required = false, defaultValue = "4") int count) {
		return ResponseEntity.ok(
			dietService.getDiets(userId, cursor, count));
	}

	@GetMapping("/getFoods")
	public ResponseEntity<List<FoodResponse>> getFoods(
		@RequestParam("pageNo") final int pageNo,
		@RequestParam("pageSize") final int pageSize,
		@RequestParam(name = "foodName", required = false) Optional<String> foodName) {
		List<FoodResponse> foodResponses;
		if (foodName.isPresent()) {
			foodResponses = foodService.getFoods(pageNo, pageSize, foodName.get());
		} else {
			foodResponses = foodService.getFoods(pageNo, pageSize);
		}
		return ResponseEntity.ok(foodResponses);
	}

}
