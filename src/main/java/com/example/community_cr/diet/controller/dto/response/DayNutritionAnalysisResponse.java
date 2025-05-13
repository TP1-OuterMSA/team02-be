package com.example.community_cr.diet.controller.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayNutritionAnalysisResponse {
	private List<NutritionAnalysisResponse> nutrition;

	public static DayNutritionAnalysisResponse from(List<NutritionAnalysisResponse> nutrition) {
		return DayNutritionAnalysisResponse.builder()
			.nutrition(nutrition)
			.build();
	}
}
