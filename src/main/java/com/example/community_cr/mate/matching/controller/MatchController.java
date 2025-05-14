package com.example.community_cr.mate.matching.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.mate.matching.controller.dto.request.MealPostRequest;
import com.example.community_cr.mate.matching.controller.dto.response.MealPostResponse;
import com.example.community_cr.mate.matching.service.MatchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/team2/match")
@RequiredArgsConstructor
public class MatchController {
	private final MatchService matchService;

	@PostMapping("/save")
	public ResponseEntity<MealPostResponse> saveMealPost(
		@RequestHeader("user-id") long userId,
		@RequestBody @Valid MealPostRequest mealPostRequest) {
		return ResponseEntity.ok(matchService.saveMealPost(userId, mealPostRequest));
	}
}
