package com.example.community_cr.diet.controller.dto.response.api;

import com.example.community_cr.diet.entity.Food;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NutritionInfo {
	private int id;
	private String name;
	private double weight;
	private double kcal;
	private double carb;
	private double protein;
	private double fat;

	public Food toEntity() {
		return Food.builder()
			.foodWeight(weight)
			.carb(carb)
			.foodName(name)
			.protein(protein)
			.fat(fat)
			.kcal(kcal)
			.build();
	}
}

