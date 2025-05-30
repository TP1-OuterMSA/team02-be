package com.example.community_cr.community.post.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.community_cr.community.post.controller.dto.request.PostRequest;
import com.example.community_cr.community.post.controller.dto.request.UpdatePostRequest;
import com.example.community_cr.community.post.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.post.controller.dto.response.PostResponse;
import com.example.community_cr.community.post.entity.PostFilterType;
import com.example.community_cr.community.post.service.CommunityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/team2/community")
@RequiredArgsConstructor
public class CommunityController {
	private final CommunityService communityService;

	@PostMapping("/save")
	public ResponseEntity<PostDetailResponse> savePost(
		@RequestHeader("userId") long userId,
		@RequestPart(name = "request") @Valid final PostRequest postRequest,
		@RequestPart(value = "image") final MultipartFile image) {
		Optional<PostDetailResponse> postDetailResponse = communityService.createCommunityPost(userId, postRequest,
			image);
		return ResponseEntity.ok(
			postDetailResponse.orElseThrow(IllegalArgumentException::new));
	}

	@PatchMapping("/update/{postId}")
	public ResponseEntity<PostDetailResponse> updatePost(
		@RequestHeader("userId") long userId,
		@PathVariable long postId,
		@RequestPart(name = "request", required = false) @Valid final Optional<UpdatePostRequest> updatePostRequest,
		@RequestPart(value = "image", required = false) final Optional<MultipartFile> image) {
		Optional<PostDetailResponse> postDetailResponse = Optional.empty();
		if (updatePostRequest.isPresent() && image.isPresent()) {
			postDetailResponse = communityService.updateCommunityPost(userId, postId, updatePostRequest.get(),
				image.get());
		} else if (updatePostRequest.isPresent()) {
			postDetailResponse = communityService.updateCommunityPost(userId, postId, updatePostRequest.get());
		} else if (image.isPresent()) {
			postDetailResponse = communityService.updateCommunityPost(userId, postId, image.get());
		}
		return ResponseEntity.ok(
			postDetailResponse.orElseThrow(IllegalArgumentException::new));
	}

	@DeleteMapping("/delete/{postId}")
	public ResponseEntity<Void> deletePost(
		@RequestHeader("userId") long userId,
		@PathVariable long postId) {
		communityService.deletePost(userId, postId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
			.build();
	}

	@GetMapping("/getPosts")
	public ResponseEntity<List<PostResponse>> getPosts(
		@RequestHeader("userId") long userId,
		@RequestParam(name = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(name = "count", required = false, defaultValue = "4") int count,
		@RequestParam(name = "filter", required = false, defaultValue = "ALL") PostFilterType postFilterType) {
		return ResponseEntity.ok(
			communityService.findAllCommunityPosts(userId, cursor, count, postFilterType));
	}

	@GetMapping("/getPost/{postId}")
	public ResponseEntity<PostDetailResponse> getPost(
		@RequestHeader("userId") long userId,
		@PathVariable long postId) {
		Optional<PostDetailResponse> postDetailResponse = communityService.findCommunityPostById(userId, postId);
		return ResponseEntity.ok(
			postDetailResponse.orElseThrow(IllegalArgumentException::new));
	}
}
