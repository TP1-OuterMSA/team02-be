package com.example.community_cr.store.component;

import org.springframework.stereotype.Component;

import com.example.community_cr.mealMatch.repository.PlaceRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StoreConsumer {
	private final PlaceRepository placeRepository;

	// @KafkaListener(topics = "store.events", groupId = "meal-nutrition")
	// public void consume(StoreEvent storeEvent) {
	// 	Place place = Place.from(storeEvent);
	// 	log.info("Kafka Saved Event : date: {}, title: {}", schoolEvent.getDayInfo(), schoolEvent.getTitle());
	// 	schoolEventRepository.save(schoolEvent);
	// }
}
