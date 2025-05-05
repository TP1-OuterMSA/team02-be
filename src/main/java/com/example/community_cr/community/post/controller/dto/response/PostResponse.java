package com.example.community_cr.community.post.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.community.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PostResponse {
	public long id;

	@Setter
	public String imageLink;

	public String title;

	public String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime updatedAt;

	public long likeCount;

	public long commentCount;

	public static PostResponse from(Post post) {
		return PostResponse.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.imageLink(post.getImageFileName())
			.likeCount(post.getHeartCount())
			.commentCount(post.getCommentCount())
			.build();
	}
}
