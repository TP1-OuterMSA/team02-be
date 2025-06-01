package com.example.community_cr.community.post_block.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.community.post.controller.dto.response.PostResponse;
import com.example.community_cr.community.post_block.service.BlockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/team2/community")
@RequiredArgsConstructor
public class BlockController {
	private final BlockService blockService;

	@PatchMapping("/block/{postId}")
	public ResponseEntity<Void> blockPost(
		@RequestHeader("userId") long userId,
		@PathVariable long postId) {
		blockService.blockPost(userId, postId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/unblock/{postId}")
	public ResponseEntity<PostResponse> unblockPost(
		@RequestHeader("userId") long userId,
		@PathVariable long postId) {
		PostResponse postResponse = blockService.unblockPost(userId, postId)
			.orElseThrow(IllegalArgumentException::new);
		return ResponseEntity.ok(postResponse);
	}
}
