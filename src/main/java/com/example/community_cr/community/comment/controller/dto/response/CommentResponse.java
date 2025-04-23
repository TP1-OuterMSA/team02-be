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

	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder()
			.commentId(comment.getId())
			.content(comment.getContent())
			.userId(comment.getUser().getId())
			.postId(comment.getPost().getId())
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
