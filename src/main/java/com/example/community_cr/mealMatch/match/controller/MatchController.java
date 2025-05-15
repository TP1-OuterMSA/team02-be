package com.example.community_cr.mealMatch.match.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
