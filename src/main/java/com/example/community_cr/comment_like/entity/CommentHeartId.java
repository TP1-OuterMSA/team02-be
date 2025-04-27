package com.example.community_cr.comment_like.entity;

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

	private long userId;
	private long commentId;

	public static CommentHeartId of(long commentId, long userId) {
		return CommentHeartId.builder()
			.commentId(commentId)
			.userId(userId)
			.build();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CommentHeartId that))
			return false;
		return userId == that.userId && commentId == that.commentId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, commentId);
	}
}
