package com.example.community_cr.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.like.repository.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/team2/community")
@RequiredArgsConstructor
public class LikeController {
	private final LikeService likeService;

	@PostMapping("/like/{postId}")
	public ResponseEntity<Void> like(
		@RequestHeader("user-id") long userId,
		@PathVariable(name = "postId") long postId) {
		likeService.likePost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/unlike/{postId}")
	public ResponseEntity<Void> unlike(
		@RequestHeader("user-id") long userId,
		@PathVariable(name = "postId") long postId) {
		likeService.unlikePost(userId, postId);
		return ResponseEntity.ok().build();
	}
}
