package com.example.community_cr.diet.controller.dto.request;

import com.example.community_cr.diet.entity.MealType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DietRequest {
    private LocalDate date;
    private List<MealRequest> meals;

    @Getter @Setter
    public static class MealRequest {
        private MealType type;
        private List<FoodRequest> foods;
    }

    @Getter @Setter
    public static class FoodRequest {
        private String foodCode;
    }
}
