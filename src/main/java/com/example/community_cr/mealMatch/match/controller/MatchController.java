package com.example.community_cr.mealMatch.match.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.community_cr.mealMatch.match.controller.dto.request.MealPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.response.MatchOfferResponse;
import com.example.community_cr.mealMatch.match.controller.dto.response.MealPostResponse;
import com.example.community_cr.mealMatch.match.service.MatchService;

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

	@PostMapping("/offer")
	public ResponseEntity<Void> offerMealMate(
		@RequestHeader("user-id") long userId,
		@RequestParam("mealPostId") long mealPostId,
		@RequestParam("startDateTime") LocalDateTime startDateTime,
		@RequestParam("endDateTime") LocalDateTime endDateTime
	) {
		matchService.offerMealMate(userId, mealPostId, startDateTime, endDateTime);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/getOffers")
	public ResponseEntity<List<MatchOfferResponse>> getMatchOffer(
		@RequestHeader("user-id") long userId,
		@RequestParam(value = "mealPostId", required = false) Optional<Long> mealPostId,
		@RequestParam(value = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(value = "count", required = false, defaultValue = "3") int count
	) {
		List<MatchOfferResponse> matchOfferResponses;
		if (mealPostId.isEmpty()) {
			matchOfferResponses = matchService.getMatchOffer(userId, cursor, count);
		} else {
			matchOfferResponses = matchService.getMatchOffer(userId, mealPostId.get(), cursor, count);
		}
		return ResponseEntity.ok(matchOfferResponses);
	}

	@PatchMapping("/reply")
	public ResponseEntity<Void> replyMealMateOffer(
		@RequestHeader("user-id") long userId,
		@RequestParam("matchOfferId") long matchOfferId,
		@RequestParam("matchState") boolean matchState
	) {
		matchService.replyMealMateOffer(userId, matchOfferId, matchState);
		return ResponseEntity.ok().build();
	}
	// [1] 전체 글 목록 조회
	@GetMapping
	public List<MealPostResponse> getAllPosts() {
		return matchService.getAllPosts();
	}

	// [2] 글 수정
	@PutMapping("/{postId}")
	public ResponseEntity<?> updatePost(
			@PathVariable Long postId,
			@RequestBody MealPostRequest request,
			@RequestHeader("user-id") Long userId) {
		matchService.updatePost(postId, userId, request);
		return ResponseEntity.ok().build();
	}

	// [3] 글 삭제
	@DeleteMapping("/{postId}")
	public ResponseEntity<?> deletePost(
			@PathVariable Long postId,
			@RequestHeader("user-id") Long userId) {
		matchService.deletePost(postId, userId);
		return ResponseEntity.noContent().build();
	}
}
