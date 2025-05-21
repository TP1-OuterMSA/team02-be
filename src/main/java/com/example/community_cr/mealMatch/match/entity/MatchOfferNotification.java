package com.example.community_cr.mealMatch.match.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchOfferNotification {
	@Id
	private String id;

	private long receiverId;

	@Enumerated(EnumType.STRING)
	private MatchState matchState;

	private String message;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_offer_id")
	private MatchOffer matchOffer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_post_id")
	private MatchPost matchPost;

	public static MatchOfferNotification of(String id, long receiverId, String message, LocalDateTime createdAt,
		MatchOffer matchOffer) {
		return MatchOfferNotification.builder()
			.id(id)
			.receiverId(receiverId)
			.matchState(matchOffer.getMatchState())
			.message(message)
			.createdAt(createdAt)
			.matchOffer(matchOffer)
			.matchPost(matchOffer.getMatchPost())
			.build();
	}
}
