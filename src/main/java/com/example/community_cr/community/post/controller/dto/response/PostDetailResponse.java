package com.example.community_cr.community.post.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.community_cr.community.comment.controller.dto.response.CommentResponse;
import com.example.community_cr.community.post.entity.Post;
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
	private LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
	private long likeCount;
	private long commentCount;
	private List<CommentResponse> commentResponseList;
	private boolean likeStatus;

	public static PostDetailResponse of(Post post, boolean likeStatus, List<CommentResponse> commentResponses,
		String imageUrl) {
		return PostDetailResponse.builder()
			.id(post.getId())
			.userId(post.getUser().getId())
			.image(imageUrl)
			.title(post.getTitle())
			.content(post.getContent())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.likeCount(post.getHeartCount())
			.commentCount(post.getCommentCount())
			.commentResponseList(commentResponses)
			.likeStatus(likeStatus)
			.build();
	}
}
