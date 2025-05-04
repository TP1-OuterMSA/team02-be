package com.example.community_cr.school_meal.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.diet.entity.MealType;
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
@Service
@AllArgsConstructor
public class ConsumerService {
	private final MealRepository mealRepository;
	private final MenuRepository menuRepository;
	private final MealMenuRepository mealMenuRepository;

	@KafkaListener(topics = "meal.web.crawler.updated", groupId = "meal-nutrition")
	@Transactional
	public void consume(MealEvent event) {
		MealType mealType = MealType.from(event.getMealType());
		String date = event.getDate();
		String[] menuNames = event.getMealContents().split(" ");

		Meal meal = Meal.builder()
			.dayInfo(date)
			.mealType(mealType)
			.build();
		mealRepository.save(meal);

		for (String menuName : menuNames) {
			Menu menu = menuRepository.findByName(menuName);
			if (menu == null) {
				try {
					menu = menuRepository.save(Menu.builder().name(menuName).build());
				} catch (DataIntegrityViolationException e) {
					menu = menuRepository.findByName(menuName);
					if (menu == null) {
						throw new IllegalStateException("Menu 저장 중 충돌 발생");
					}
				}
			}

			MealMenu mealMenu = MealMenu.builder()
				.meal(meal)
				.menu(menu)
				.build();
			mealMenuRepository.save(mealMenu);
		}
	}

}
