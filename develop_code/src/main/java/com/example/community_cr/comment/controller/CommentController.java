package com.example.community_cr.comment.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.comment.controller.dto.request.CommentRequest;
import com.example.community_cr.comment.controller.dto.request.UpdateCommentRequest;
import com.example.community_cr.comment.controller.dto.response.CommentResponse;
import com.example.community_cr.comment.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/add/{postId}")
	public ResponseEntity<CommentResponse> addComment(
		@RequestHeader("user-id") final long userId,
		@PathVariable final long postId,
		@RequestBody @Valid CommentRequest commentRequest) {
		Optional<CommentResponse> commentResponse = commentService.saveComment(userId, postId, commentRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentResponse.orElseThrow(IllegalArgumentException::new));
	}

	@PatchMapping("/update/{commentId}")
	public ResponseEntity<CommentResponse> updateComment( //프론트에서 댓글의 정보 받고 싶으면
		@RequestHeader("user-id") long userId,
		@PathVariable long commentId, //아이디와
		@RequestBody @Valid UpdateCommentRequest request) { //수정할 내용 받아서
		Optional<CommentResponse> commentResponse = commentService.updateComment(userId, commentId, request);
		return ResponseEntity.ok(
			commentResponse.orElseThrow(IllegalArgumentException::new)
		);
	}

	@DeleteMapping("/delete/{commentId}")
	public ResponseEntity<CommentResponse> deleteComment(
		@RequestHeader("user-id") long userId,
		@PathVariable long commentId) {
		commentService.deleteComment(userId, commentId);
		return ResponseEntity.noContent().build();
	}
}
