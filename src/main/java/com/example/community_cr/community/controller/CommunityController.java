package com.example.community_cr.community.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.community_cr.community.controller.dto.request.PostRequest;
import com.example.community_cr.community.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.controller.dto.response.PostResponse;
import com.example.community_cr.community.service.CommunityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
	private final CommunityService communityService;

	@PostMapping("/save")
	public ResponseEntity<PostDetailResponse> savePost(
		@RequestHeader("user-id") long userId,
		@RequestPart(name = "request") @Valid final PostRequest postRequest,
		@RequestPart(value = "image") final MultipartFile image) {
		Optional<PostDetailResponse> postDetailResponse = communityService.createCommunityPost(userId, postRequest,
			image);
		return ResponseEntity.ok(
			postDetailResponse.orElseThrow(IllegalArgumentException::new));
	}

	@GetMapping("/getPosts")
	public ResponseEntity<List<PostResponse>> getPosts(
		@RequestParam(name = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(name = "count", required = false, defaultValue = "4") int count) {
		return ResponseEntity.ok(
			communityService.findAllCommunityPosts(cursor, count));
	}

	@GetMapping("/getPost/{id}")
	public ResponseEntity<PostDetailResponse> getPost(@PathVariable long id) {
		Optional<PostDetailResponse> postDetailResponse = communityService.findCommunityPostById(id);
		return ResponseEntity.ok(
			postDetailResponse.orElseThrow(IllegalArgumentException::new));
	}
}
