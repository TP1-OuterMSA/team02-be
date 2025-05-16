package com.example.community_cr.mealMatch.match.entity;

import java.time.LocalDateTime;

import com.example.community_cr.mealMatch.match.controller.dto.request.MealPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.response.MealPostResponse;
import com.example.community_cr.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class MealPost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	private Object matchDate;
	private Object mealTime;
	private Long placeId;


	public Long getUserId() {
		return user != null ? user.getId() : null;
	}

	public static MealPost of(MealPostRequest mealPostRequest, Place place, User user, LocalDateTime createdAt) {
		return MealPost.builder()
			.createdAt(createdAt)
			.updatedAt(createdAt)
			.title(mealPostRequest.getTitle())
			.content(mealPostRequest.getContent())
			.place(place)
			.user(user)
			.build();
	}

	public void update(MealPostRequest request) {
		this.title = request.getTitle();
		this.content = request.getContent();
//		this.matchDate = request.getMatchDate();
//		this.mealTime = request.getMealTime();
	}
}
