package com.example.community_cr.diet.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.example.community_cr.diet.controller.dto.response.NutritionAnalysisResponse;
import com.example.community_cr.diet.entity.MealType;
import com.example.community_cr.diet.service.DietService;
import com.example.community_cr.diet.service.FoodService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/diet")
public class DietController {
	private final DietService dietService;
	private final FoodService foodService;

	@PostMapping("/saveDiet")
	public ResponseEntity<DietResponse> saveDiet(
		@RequestHeader("user-id") long userId,
		@RequestBody @Valid DietRequest dto
	) {
		foodService.saveNutrition(dto.getFoods());
		Optional<DietResponse> response = dietService.saveDiet(userId, dto);
		return ResponseEntity.ok(
			response.orElseThrow(IllegalArgumentException::new));
	}

	@GetMapping("/getDiet")
	public ResponseEntity<DietResponse> getDiet(
		@RequestHeader("user-id") long userId,
		@RequestParam("dietId") long dietId
	) {
		Optional<DietResponse> response = dietService.getDiet(userId, dietId);
		return ResponseEntity.ok(
			response.orElseThrow(IllegalArgumentException::new));
	}

	@GetMapping("/getDiets")
	public ResponseEntity<List<DietResponse>> getDiets(
		@RequestHeader("user-id") long userId,
		@RequestParam(name = "date") LocalDate date
	) {
		return ResponseEntity.ok(
			dietService.getDiets(userId, date));
	}

	@GetMapping("/getFoods")
	public ResponseEntity<List<FoodResponse>> getFoods(
		@RequestParam(value = "count", required = false, defaultValue = "3") final int count,
		@RequestParam(name = "foodName") String foodName
	) {
		List<FoodResponse> foodResponses = foodService.getFoods(count, foodName);

		return ResponseEntity.ok(foodResponses);
	}

	@GetMapping("/getDietDates")
	public ResponseEntity<List<String>> getDietDates(
		@RequestHeader("user-id") long userId,
		@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
	) {
		List<LocalDate> dates = dietService.getDietDates(userId, month);

		return ResponseEntity.ok(dates.stream()
			.map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)) // yyyy-MM-dd
			.collect(Collectors.toList()));
	}

	@GetMapping("/getSchoolMeal")
	public ResponseEntity<List<FoodResponse>> getSchoolMeal(
		@RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
		@RequestParam(name = "mealType") MealType mealType
	) {
		List<FoodResponse> foods = foodService.getSchoolMeal(date, mealType);
		return ResponseEntity.ok(foods);
	}

	@DeleteMapping("/deleteDiet")
	public ResponseEntity<Void> deleteDiet(
		@RequestHeader("user-id") long userId,
		@RequestParam("dietId") long dietId
	) {
		dietService.deleteDiet(userId, dietId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/analyze")
	public ResponseEntity<NutritionAnalysisResponse> analyzeNutrition(
		@RequestHeader("user-id") long userId,
		@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		return ResponseEntity.ok(dietService.analyzeNutrition(userId, date));
	}
}
