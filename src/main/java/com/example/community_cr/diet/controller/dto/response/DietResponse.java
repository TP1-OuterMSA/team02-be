package com.example.community_cr.diet.controller.dto.response;

import com.example.community_cr.diet.entity.MealType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DietResponse {
    private Long id;
    private LocalDate date;
    private List<MealDto> meals;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MealDto {
        private MealType type;
        private List<FoodDto> foods;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FoodDto {
        private String foodName;
        private String foodCode;

    }
}
