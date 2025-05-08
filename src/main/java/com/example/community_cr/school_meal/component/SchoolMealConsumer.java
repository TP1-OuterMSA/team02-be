package com.example.community_cr.school_meal.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.service.FoodService;
import com.example.community_cr.school_meal.entity.Meal;
import com.example.community_cr.school_meal.entity.MealMenu;
import com.example.community_cr.school_meal.entity.Menu;
import com.example.community_cr.school_meal.repository.MealMenuRepository;
import com.example.community_cr.school_meal.repository.MealRepository;
import com.example.community_cr.school_meal.repository.MenuRepository;
import com.example.kafka_schemas.MealEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SchoolMealConsumer {
	private final MealRepository mealRepository;
	private final MenuRepository menuRepository;
	private final MealMenuRepository mealMenuRepository;
	private final FoodService foodService;
	private final NutritionProducer nutritionProducer;

	@KafkaListener(topics = "meal.web.crawler.updated", groupId = "meal-nutrition")
	public void consume(MealEvent event) {
		List<String> menuNames = Arrays.asList(event.getMealContents().split(" "));

		Meal meal = Meal.from(event);
		mealRepository.save(meal);

		Map<String, Menu> menus = menuRepository.findAllByNameIn(menuNames).stream()
			.collect(Collectors.toMap(
				Menu::getName,
				Function.identity()
			));

		List<Menu> notFoundMenus = menuNames.stream()
			.filter(menuName -> !menus.containsKey(menuName))
			.map(Menu::from)
			.toList();

		List<Menu> savedMenus = menuRepository.saveAll(notFoundMenus);
		savedMenus.forEach(menu -> menus.put(menu.getName(), menu));

		List<MealMenu> mealMenus = new ArrayList<>();
		menuNames.forEach(menuName -> {
			Menu menu = menus.get(menuName);
			MealMenu mealMenu = MealMenu.of(meal, menu);
			mealMenus.add(mealMenu);
		});
		mealMenuRepository.saveAll(mealMenus);

		List<Food> foods = foodService.saveSchoolMealNutrition(menuNames);
		nutritionProducer.sendAllMealItems(foods);
	}

}
