package com.example.community_cr.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/user")
public class UserController {
	private final UserService userService;

	@GetMapping("/getRecommendKcal")
	public ResponseEntity<Double> getRecommendKcal(
		@RequestHeader("userId") long userId
	) {
		double recommendKcal = userService.getRecommendKcal(userId)
			.orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(recommendKcal);
	}

	@PatchMapping("/updateKcal")
	public ResponseEntity<Void> updateKcal(
		@RequestHeader("userId") long userId,
		@RequestParam("kcal") double kcal
	) {
		userService.updateKcal(userId, kcal);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/getMyId")
	public ResponseEntity<Long> getMyId(
		@RequestHeader("userId") long userId
	) {
		return ResponseEntity.ok(userId);
	}
}
