package com.example.community_cr.community.comment_like.entity;

import com.example.community_cr.community.comment.entity.Comment;
import com.example.community_cr.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(CommentHeartId.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CommentHeart {
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", insertable = false, updatable = false)
	private Comment comment;

	public static CommentHeart of(User user, Comment comment) {
		return CommentHeart.builder()
			.user(user)
			.comment(comment)
			.build();
	}
}
