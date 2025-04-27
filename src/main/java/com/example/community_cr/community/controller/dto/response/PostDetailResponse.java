package com.example.community_cr.community.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.community_cr.comment.controller.dto.response.CommentResponse;
import com.example.community_cr.community.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PostDetailResponse {
	private long id;
	private long userId;
	@Setter
	private String image;
	private String title;
	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
	private int likeCount;
	private int commentCount;
	private List<CommentResponse> commentResponseList;
	private boolean likeStatus;

	public static PostDetailResponse from(Post post, boolean likeStatus) {
		return PostDetailResponse.builder()
				.id(post.getId())
				.userId(post.getUser().getId())
				.image(post.getImageFileName())
				.title(post.getTitle())
				.content(post.getContent())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.likeCount(post.getHeartList().size())
				.commentCount(post.getCommentList().size())
				.commentResponseList(
						post.getCommentList().stream()
								.map(CommentResponse::from)
								.toList()
				)
				.likeStatus(likeStatus)
				.build();
	}

	public static PostDetailResponse from(Post post, boolean likeStatus, List<CommentResponse> commentResponses) {
		return PostDetailResponse.builder()
				.id(post.getId())
				.userId(post.getUser().getId())
				.image(post.getImageFileName())
				.title(post.getTitle())
				.content(post.getContent())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.likeCount(post.getHeartList().size())
				.commentCount(post.getCommentList().size())
				.commentResponseList(commentResponses)
				.likeStatus(likeStatus)
				.build();
	}
}
