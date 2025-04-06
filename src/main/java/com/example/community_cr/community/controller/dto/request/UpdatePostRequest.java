package com.example.community_cr.community.controller.dto.request;

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
}
