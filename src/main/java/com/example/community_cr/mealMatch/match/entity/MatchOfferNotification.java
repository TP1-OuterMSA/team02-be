package com.example.community_cr.mealMatch.match.entity;

import java.time.LocalDateTime;

import com.example.community_cr.user.entity.User;

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

	@Enumerated(EnumType.STRING)
	private MatchState matchState;

	private String message;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder.Default
	private boolean isRead = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_offer_id")
	private MatchOffer matchOffer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_post_id")
	private MatchPost matchPost;

	public static MatchOfferNotification of(String id, User receiver, String message, LocalDateTime createdAt,
		MatchOffer matchOffer) {
		return MatchOfferNotification.builder()
			.id(id)
			.receiver(receiver)
			.matchState(matchOffer.getMatchState())
			.message(message)
			.createdAt(createdAt)
			.isRead(false)
			.matchOffer(matchOffer)
			.matchPost(matchOffer.getMatchPost())
			.build();
	}
}
