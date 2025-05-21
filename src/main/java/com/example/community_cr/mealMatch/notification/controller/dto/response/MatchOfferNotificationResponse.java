package com.example.community_cr.mealMatch.notification.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.mealMatch.match.entity.MatchOffer;
import com.example.community_cr.mealMatch.match.entity.MatchOfferNotification;
import com.example.community_cr.mealMatch.match.entity.MatchState;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchOfferNotificationResponse {
	private long matchOfferId;
	private long matchPostId;
	private MatchState matchState;
	private String message;

	private LocalDateTime createdAt;

	public static MatchOfferNotificationResponse of(MatchOffer matchOffer, String message, LocalDateTime createdAt) {
		return MatchOfferNotificationResponse.builder()
			.matchOfferId(matchOffer.getId())
			.matchState(matchOffer.getMatchState())
			.message(message)
			.createdAt(createdAt)
			.build();
	}

	public static Object from(MatchOfferNotification matchOfferNotification) {
		return MatchOfferNotificationResponse.builder()
			.matchOfferId(matchOfferNotification.getMatchOffer().getId())
			.matchState(matchOfferNotification.getMatchOffer().getMatchState())
			.message(matchOfferNotification.getMessage())
			.createdAt(matchOfferNotification.getCreatedAt())
			.build();
	}
}
