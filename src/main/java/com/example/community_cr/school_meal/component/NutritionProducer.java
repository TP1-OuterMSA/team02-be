package com.example.community_cr.school_meal.component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.community_cr.diet.entity.Food;
import com.example.community_cr.diet.repository.FoodRepository;
import com.example.community_cr.school_meal.entity.Meal;
import com.example.community_cr.school_meal.entity.MealMenu;
import com.example.community_cr.school_meal.entity.Menu;
import com.example.community_cr.school_meal.repository.MealRepository;
import com.example.kafka_schemas.NutritionEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class NutritionProducer {
	private final KafkaTemplate<String, NutritionEvent> kafkaTemplate;
	private final MealRepository mealRepository;
	private final FoodRepository foodRepository;

	public void sendMealItem(LocalDate startDate, LocalDate endDate) {
		List<String> dates = new ArrayList<>();
		for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
			dates.add(date.toString());
		}

		List<Meal> meals = mealRepository.findAllByDayInfoIn(dates);
		Set<Menu> menus = new HashSet<>();
		for (Meal meal : meals) {
			List<Menu> mealMenus = meal.getMealMenus().stream()
				.map(MealMenu::getMenu)
				.toList();
			menus.addAll(mealMenus);
		}
		List<String> foodNames = menus.stream()
			.map(Menu::getName)
			.toList();

		List<Food> foods = foodRepository.findAllByFoodNameIn(foodNames);
		sendAllMealItems(foods);
	}

	public void sendAllMealItems(List<Food> foods) {
		try {
			for (Food food : foods) {
				NutritionEvent nutritionEvent = createNutritionEventFromFood(food);
				ProducerRecord<String, NutritionEvent> producerRecord = new ProducerRecord<>(
					"meal.nutrition.updated", "nutrition.data", nutritionEvent);
				kafkaTemplate.send(producerRecord);
				log.info("Kafka 전송 성공 - nutritionEvent: {}", nutritionEvent);
			}
		} catch (Exception e) {
			log.error("Kafka 전송 실패", e);
		}
	}

	private NutritionEvent createNutritionEventFromFood(Food food) {
		return new NutritionEvent(food.getFoodName(), food.getFoodWeight(), food.getKcal(), food.getProtein(),
			food.getFat(), food.getCarb());
	}
}
