package com.example.community_cr.mealMatch.match.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MealPostRequest {
	private double latitude;

	private double longitude;

	@NotBlank(message = "주소를 입력해주세요.")
	private String address;

	@NotBlank(message = "위치 이름을 입력해주세요.")
	private String name;

	@NotBlank(message = "제목을 입력해주세요.")
	private String title;

	@NotBlank(message = "내용을 입력해주세요.")
	private String content;


}
