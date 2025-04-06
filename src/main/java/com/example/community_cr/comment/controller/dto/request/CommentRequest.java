package com.example.community_cr.comment.controller.dto.request;

import java.time.LocalDateTime;

import com.example.community_cr.comment.entity.Comment;
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
public class CommentRequest {
	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 50)
	private String content;

	public Comment toEntity(LocalDateTime createdAt, Post post, User user) {
		return Comment.builder()
			.content(content)
			.createdAt(createdAt)
			.user(user)
			.post(post)
			.build();
	}
}
