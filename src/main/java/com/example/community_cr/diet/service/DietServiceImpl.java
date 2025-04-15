package com.example.community_cr.diet.service;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
import com.example.community_cr.diet.controller.dto.response.DietResponse;
import com.example.community_cr.diet.entity.*;
import com.example.community_cr.diet.repository.DietRepository;
import com.example.community_cr.diet.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietServiceImpl implements DietService {

    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;

    @Override
    public void saveDiet(DietRequest dto) {
        Diet diet = new Diet();
        diet.setDate(dto.getDate());

        List<Meal> meals = dto.getMeals().stream().map(mealReq -> {
            Meal meal = new Meal();
            meal.setType(mealReq.getType());
            meal.setDiet(diet);

            List<MealFood> foods = mealReq.getFoods().stream().map(foodReq -> {
                //foodCode 기반 DB 조회
                Food food = foodRepository.findByFoodCode(foodReq.getFoodCode())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음식 코드: " + foodReq.getFoodCode()));

                //MealFood 생성 및 세팅
                MealFood mealFood = new MealFood();
                mealFood.setFoodCode(food.getFoodCode());
                mealFood.setFoodName(food.getFoodName());
                mealFood.setKcal(food.getKcalPer100g());
                mealFood.setWeightInGrams(100);           // 일단 기본값 100
                mealFood.setMeal(meal);

                return mealFood;
            }).toList();

            meal.setFoods(foods);
            return meal;
        }).toList();

        diet.setMeals(meals);
        dietRepository.save(diet);
    }

    public DietResponse toResponseDto(Diet diet) {
        List<DietResponse.MealDto> mealDto = diet.getMeals().stream().map(meal -> {
            List<DietResponse.FoodDto> foodDto = meal.getFoods().stream().map(food ->
                    new DietResponse.FoodDto(
                            food.getFoodName(),
                            food.getFoodCode()
                    )
            ).toList();

            return new DietResponse.MealDto(meal.getType(), foodDto);
        }).toList();

        DietResponse response = new DietResponse();
        response.setId(diet.getId());
        response.setDate(diet.getDate());
        response.setMeals(mealDto);
        return response;
    }
}
