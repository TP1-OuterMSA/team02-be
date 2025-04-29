package com.example.community_cr.community.comment_like.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentHeartId implements Serializable {

	private long user;
	private long comment;

	public static CommentHeartId of(long commentId, long userId) {
		return CommentHeartId.builder()
			.comment(commentId)
			.user(userId)
			.build();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CommentHeartId that))
			return false;
		return user == that.user && comment == that.comment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, comment);
	}
}
