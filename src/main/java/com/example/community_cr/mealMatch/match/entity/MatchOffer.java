package com.example.community_cr.mealMatch.match.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.community_cr.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class MatchOffer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MatchState matchState;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private LocalDateTime startSchedule;

	@Column(nullable = false)
	private LocalDateTime endSchedule;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_post_id")
	private MatchPost matchPost;

	@Builder.Default
	@OneToMany(mappedBy = "matchOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MatchOfferNotification> notifications = new ArrayList<>();

	public void updateMatchState(MatchState matchState, LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
		this.matchState = matchState;
	}
}
