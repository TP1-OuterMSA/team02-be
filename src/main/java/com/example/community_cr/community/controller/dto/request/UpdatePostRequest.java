package com.example.community_cr.community.controller.dto.request;

import java.time.LocalDateTime;

import com.example.community_cr.community.entity.Post;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdatePostRequest {
	@Pattern(regexp = "^(?!\\s*$).+", message = "빈 문자열은 허용되지 않습니다.")
	@Size(max = 50)
	private String title;

	@Pattern(regexp = "^(?!\\s*$).+", message = "빈 문자열은 허용되지 않습니다.")
	@Size(max = 100)
	private String content;

	public Post toEntity(LocalDateTime createdAt, Post post) {
		return Post.builder()
			.id(post.getId())
			.imageFileName(post.getImageFileName())
			.title(title == null ? post.getTitle() : title)
			.content(content == null ? post.getContent() : content)
			.createdAt(createdAt)
			.user(post.getUser())
			.build();
	}

	public Post toEntity(LocalDateTime createdAt, Post post, String imageFileName) {
		return Post.builder()
			.id(post.getId())
			.imageFileName(imageFileName)
			.title(title == null ? post.getTitle() : title)
			.content(content == null ? post.getContent() : content)
			.createdAt(createdAt)
			.user(post.getUser())
			.build();
	}
}
