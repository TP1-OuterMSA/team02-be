package com.example.community_cr.mealMatch.match.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.mealMatch.match.entity.MatchPost;
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

	public static MealPostResponse from(MatchPost matchPost) {
		return MealPostResponse.builder()
			.id(matchPost.getId())
			.createdAt(matchPost.getCreatedAt())
			.updatedAt(matchPost.getUpdatedAt())
			.schedule(matchPost.getSchedule())
			.content(matchPost.getContent())
			.place(PlaceResponse.from(matchPost.getPlace()))
			.userId(matchPost.getUser().getId())
			.build();
	}
}
