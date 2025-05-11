package com.example.community_cr.diet.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.example.community_cr.diet.entity.Diet;
import com.example.community_cr.diet.entity.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DietResponse {
	private Long id;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate date;
	private MealType type;
	private List<FoodDto> foods;
	private double recommendKcal;

	public static DietResponse from(Diet diet, double recommendKcal) {
		return DietResponse.builder()
			.id(diet.getId())
			.date(diet.getDate())
			.type(diet.getType())
			.foods(diet.getFoods().stream()
				.map(food -> new FoodDto(food.getId(), food.getFood().getFoodName(),
					food.getIntakeWeight(), food.getIntakeKcal()))
				.toList())
			.recommendKcal(recommendKcal)
			.build();
	}

	@Getter
	@AllArgsConstructor
	public static class FoodDto {
		private long foodId;
		private String foodName;
		private double intakeWeight;
		private double intakeKcal;
	}
}
