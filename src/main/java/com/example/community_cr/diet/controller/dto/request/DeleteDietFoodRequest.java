package com.example.community_cr.diet.controller.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DeleteDietFoodRequest {
	@NotEmpty(message = "최소 1개 이상의 음식 아이디가 필요합니다.")
	private List<Long> foodIds;
}
