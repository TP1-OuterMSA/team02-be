package com.example.community_cr.community.comment_like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.community.comment_like.service.CommentLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/community")
public class CommentLikeController {

	private final CommentLikeService commentLikeService;

	@PostMapping("/comment/like/{commentId}")
	public ResponseEntity<Void> likeComment(
		@RequestHeader("userId") long userId,
		@PathVariable(name = "commentId") long commentId) {
		commentLikeService.likeComment(userId, commentId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/comment/unlike/{commentId}")
	public ResponseEntity<Void> unlikeComment(
		@RequestHeader("userId") long userId,
		@PathVariable(name = "commentId") long commentId) {
		commentLikeService.unlikeComment(userId, commentId);
		return ResponseEntity.ok().build();
	}
}
