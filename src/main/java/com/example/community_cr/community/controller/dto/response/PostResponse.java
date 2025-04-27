package com.example.community_cr.community.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.community.entity.Post;
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
	public LocalDateTime updatedAt;

	public int likeCount;

	public int commentCount;

	public static PostResponse from(Post post) {
		return PostResponse.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.imageLink(post.getImageFileName())
			.likeCount(post.getHeartList().size())
			.commentCount(post.getCommentList().size())
			.build();
	}
}
