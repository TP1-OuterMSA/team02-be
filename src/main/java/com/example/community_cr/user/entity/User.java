package com.example.community_cr.user.entity;

import com.example.kafka_schemas.UserEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class User {
	@Id
	private long id;

	@Column(columnDefinition = "double default 0")
	private double recommendKcal;

	private String username;

	private String nickname;

	private String email;

	public static User from(UserEvent event) {
		return User.builder()
			.id(event.getId())
			.username(event.getUsername())
			.nickname(event.getNickname())
			.email(event.getEmail())
			.recommendKcal(0)
			.build();
	}

	public void updateRecommendKcal(double recommendKcal) {
		this.recommendKcal = recommendKcal;
	}
}
