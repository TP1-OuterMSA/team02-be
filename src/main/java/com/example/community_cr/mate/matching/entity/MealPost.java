package com.example.community_cr.mate.matching.entity;

import java.time.LocalDateTime;

import com.example.community_cr.mate.matching.controller.dto.request.MealPostRequest;
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

	@Builder.Default
	@Column(updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder.Default
	private LocalDateTime updatedAt = LocalDateTime.now();

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
}
