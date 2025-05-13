package com.example.community_cr.diet.controller.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EvaluateNutritionRequest {
	@NotEmpty
	private List<EvaluateNutritionDto> evaluateNutritionList;

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class EvaluateNutritionDto {
		@Positive
		private double kcal;

		@Positive
		private double carb;

		@Positive
		private double protein;

		@Positive
		private double fat;

		@Override
		public String toString() {
			return String.format("%f-%f-%f-%f", kcal, carb, protein, fat);
		}
	}

}
