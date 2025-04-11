package com.example.community_cr.community.controller.dto.request;

import java.time.LocalDateTime;

import com.example.community_cr.community.entity.Post;
import com.example.community_cr.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PostRequest {
	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 50)
	private String title;

	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 100)
	private String content;

	public Post toEntity(LocalDateTime createdAt, String imageFileName, User user) {
		return Post.builder()
			.imageFileName(imageFileName)
			.title(title)
			.content(content)
			.createdAt(createdAt)
			.user(user)
			.build();
	}
}
