package com.example.community_cr.mealMatch.match.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.mealMatch.match.entity.MealPost;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealPostResponse {
	private long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime schedule;

	private String content;
	private PlaceResponse place;
	private long userId;

	public static MealPostResponse from(MealPost mealPost) {
		return MealPostResponse.builder()
			.id(mealPost.getId())
			.createdAt(mealPost.getCreatedAt())
			.updatedAt(mealPost.getUpdatedAt())
			.schedule(mealPost.getSchedule())
			.content(mealPost.getContent())
			.place(PlaceResponse.from(mealPost.getPlace()))
			.userId(mealPost.getUser().getId())
			.build();
	}
}
