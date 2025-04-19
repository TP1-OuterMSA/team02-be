package com.example.community_cr.diet.controller.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.example.community_cr.diet.entity.MealType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DietRequest {
	@NotNull(message = "날짜를 입력해주세요")
	private LocalDate date;

	@NotNull(message = "식사 타입을 입력해주세요")
	private MealType mealType;

	@NotEmpty(message = "최소 1개 이상의 음식 코드가 필요합니다.")
	private List<FoodRequest> foods;
}
