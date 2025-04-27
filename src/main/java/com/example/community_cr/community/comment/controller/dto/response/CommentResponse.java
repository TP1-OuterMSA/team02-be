package com.example.community_cr.community.comment.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.community.comment.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
	public final long commentId;
	public final long userId;
	public final long postId;
	public final String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public final LocalDateTime createdAt;
	private boolean liked;

	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder()
			.commentId(comment.getId())
			.content(comment.getContent())
			.userId(comment.getUser().getId())
			.postId(comment.getPost().getId())
			.liked(false)
			.createdAt(comment.getCreatedAt())
			.build();
	}

	public static CommentResponse from(Comment comment, boolean liked) {
		return CommentResponse.builder()
			.commentId(comment.getId())
			.userId(comment.getUser().getId())
			.postId(comment.getPost().getId())
			.content(comment.getContent())
			.liked(liked)
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
