package com.example.community_cr.school_event.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.community_cr.school_event.repository.SchoolEventRepository;
import com.example.kafka_schemas.MealEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SchoolEventConsumer {
	private final SchoolEventRepository schoolEventRepository;

	@KafkaListener(topics = "event.web.crawler.updated", groupId = "meal-nutrition")
	public void consume(MealEvent event) {
		// List<String> menuNames = Arrays.asList(event.getMealContents().split(" "));
		//
		// SchoolEvent meal = SchoolEvent.from(event);
		// schoolEventRepository.save(meal);
		//
		// Map<String, Menu> menus = menuRepository.findAllByNameIn(menuNames).stream()
		// 	.collect(Collectors.toMap(
		// 		Menu::getName,
		// 		Function.identity()
		// 	));
		//
		// List<Menu> notFoundMenus = menuNames.stream()
		// 	.filter(menuName -> !menus.containsKey(menuName))
		// 	.map(Menu::from)
		// 	.toList();
		//
		// List<Menu> savedMenus = menuRepository.saveAll(notFoundMenus);
		// savedMenus.forEach(menu -> menus.put(menu.getName(), menu));
		//
		// List<MealMenu> mealMenus = new ArrayList<>();
		// menuNames.forEach(menuName -> {
		// 	Menu menu = menus.get(menuName);
		// 	MealMenu mealMenu = MealMenu.of(meal, menu);
		// 	mealMenus.add(mealMenu);
		// });
		// mealMenuRepository.saveAll(mealMenus);
	}

}
