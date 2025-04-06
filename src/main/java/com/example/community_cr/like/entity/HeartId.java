package com.example.community_cr.like.entity;

import com.example.community_cr.community.entity.Post;
import com.example.community_cr.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HeartId {
	private long user;
	private long post;

	public static HeartId of(Post post, User user) {
		return HeartId.builder()
			.post(post.getId())
			.user(user.getId())
			.build();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof HeartId heartId)) {
			return false;
		}
		if (heartId.getUser() != this.user) {
			return false;
		}
		return heartId.getPost() == this.post;
	}
}
