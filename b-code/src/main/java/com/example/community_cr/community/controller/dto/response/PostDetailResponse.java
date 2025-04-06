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
	public long id;
	
	@Setter
	public String image;

	public String title;

	public String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime createdAt;
	public int likeCount;
	public int commentCount;
	public List<CommentResponse> commentResponseList;
	private Long userId;

	public static PostDetailResponse from(Post post) {
		return PostDetailResponse.builder()
			.id(post.getId())
			.image(post.getImageFileName())
			.title(post.getTitle())
			.content(post.getContent())
			.createdAt(post.getCreatedAt())
			.likeCount(post.getHeartList().size())
			.commentCount(post.getCommentList().size())
			.commentResponseList(
				post.getCommentList().stream()
					.map(CommentResponse::from)
					.toList()
			)
			.userId(post.getUser().getId())
			.build();
	}
}
