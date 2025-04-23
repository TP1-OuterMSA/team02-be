package com.example.community_cr.community.comment.controller.dto.request;

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
public class UpdateCommentRequest {
	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 50)
	private String content;
}
