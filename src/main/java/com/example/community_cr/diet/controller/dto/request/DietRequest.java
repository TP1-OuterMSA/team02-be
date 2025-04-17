package com.example.community_cr.diet.controller.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.example.community_cr.diet.entity.MealType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DietRequest {
	@NotNull(message = "날짜를 입력해주세요")
	private LocalDate date;

	@NotNull
	private MealType mealType;

	@NotEmpty(message = "최소 1개 이상의 음식 코드가 필요합니다.")
	private List<String> foods;
}
