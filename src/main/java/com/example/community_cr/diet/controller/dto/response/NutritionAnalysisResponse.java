package com.example.community_cr.diet.controller.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionAnalysisResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate date;

	private double totalKcal;
	private double carb;
	private double protein;
	private double fat;

	public static NutritionAnalysisResponse from(LocalDate date) {
		return NutritionAnalysisResponse.builder()
			.date(date)
			.totalKcal(0)
			.carb(0)
			.protein(0)
			.fat(0)
			.build();
	}
}
