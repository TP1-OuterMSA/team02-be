package com.example.community_cr.diet.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.example.community_cr.diet.entity.Diet;
import com.example.community_cr.diet.entity.DietFood;
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

	public static DietResponse from(Diet diet) {
		return DietResponse.builder()
			.id(diet.getId())
			.date(diet.getDate())
			.type(diet.getType())
			.foods(diet.getFoods().stream()
				.map(DietFood::getFood)
				.map(food -> new FoodDto(food.getFoodName(), food.getFoodCode()))
				.toList())
			.build();
	}

	@Getter
	@AllArgsConstructor
	public static class FoodDto {
		private String foodName;
		private String foodCode;
	}
}
