package com.example.community_cr.mealMatch.match.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.community_cr.mealMatch.match.controller.dto.request.MealPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.request.UpdateMealPostRequest;
import com.example.community_cr.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class MatchPost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private LocalDateTime schedule;

	@Column(nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "matchPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MatchOffer> matchOfferList;

	public static MatchPost of(MealPostRequest mealPostRequest, Place place, User user, LocalDateTime createdAt) {
		return com.example.community_cr.mealMatch.match.entity.MatchPost.builder()
			.createdAt(createdAt)
			.updatedAt(createdAt)
			.schedule(mealPostRequest.getSchedule())
			.content(mealPostRequest.getContent())
			.place(place)
			.user(user)
			.build();
	}

	public Long getUserId() {
		return user != null ? user.getId() : null;
	}

	public void update(UpdateMealPostRequest request) {
		this.content = request.getContent() != null ? request.getContent() : this.content;
		this.schedule = request.getSchedule() != null ? request.getSchedule() : this.schedule;
		this.updatedAt = LocalDateTime.now();
	}
}
