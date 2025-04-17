package com.example.community_cr.diet.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FoodRequest {
	@NotBlank(message = "음식 코드를 입력해주세요.")
	private String foodCode;

	@Min(value = 0, message = "최소 0 이상의 칼로리를 섭취하셔야됩니다.")
	private double intakeKcal;
}
