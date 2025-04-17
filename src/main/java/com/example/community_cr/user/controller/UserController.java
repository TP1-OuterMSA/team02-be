package com.example.community_cr.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

	@PatchMapping("/updateKcal")
	public ResponseEntity<Void> updateKcal(
		@RequestHeader("user-id") long userId,
		@RequestParam("kcal") double kcal) {
		userService.updateKcal(userId, kcal);
		return ResponseEntity.ok().build();
	}
}
