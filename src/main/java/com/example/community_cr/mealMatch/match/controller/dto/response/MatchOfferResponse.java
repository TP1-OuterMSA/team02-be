package com.example.community_cr.mealMatch.match.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.mealMatch.match.entity.MatchOffer;
import com.example.community_cr.mealMatch.match.entity.MatchState;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchOfferResponse {
	private long id;

	private MatchState matchState;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	private long senderId;

	private MealPostResponse mealPost;

	public static MatchOfferResponse of(MatchOffer matchOffer, MealPostResponse mealPostResponse) {
		return MatchOfferResponse.builder()
			.id(matchOffer.getId())
			.matchState(matchOffer.getMatchState())
			.createdAt(matchOffer.getCreatedAt())
			.updatedAt(matchOffer.getUpdatedAt())
			.senderId(matchOffer.getUser().getId())
			.mealPost(mealPostResponse)
			.build();
	}
}
