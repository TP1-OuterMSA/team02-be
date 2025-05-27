package com.example.community_cr.school_event.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.community_cr.school_event.entity.SchoolEvent;
import com.example.community_cr.school_event.repository.SchoolEventRepository;
import com.example.kafka_schemas.EventMenu;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SchoolEventConsumer {
	private final SchoolEventRepository schoolEventRepository;

	@KafkaListener(topics = "event.web.crawler.updated", groupId = "meal-nutrition")
	public void consume(EventMenu event) {
		log.info("Kafka Saved Event : date: {}, title: {}", event.getDate(), event.getEventTitle());
		if (schoolEventRepository.existsByDayInfoAndTitle(event.getDate(), event.getEventTitle())) {
			return;
		}
		SchoolEvent schoolEvent = SchoolEvent.from(event);
		schoolEventRepository.save(schoolEvent);
	}

}
