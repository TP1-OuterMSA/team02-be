package com.example.community_cr.diet.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WeeklyNutritionDto {
    private String startDate; // 예: "04-28"
    private String endDate;   // 예: "05-04"

    // 단위: 비율 (%)
    private double carb;
    private double protein;
    private double fat;

    // 총 섭취 칼로리 (단위: kcal)
    private double kcal;
}
