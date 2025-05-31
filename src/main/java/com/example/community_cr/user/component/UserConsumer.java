package com.example.community_cr.user.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;
import com.example.kafka_schemas.UserEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class UserConsumer {
	private final UserRepository userRepository;

	@KafkaListener(topics = "user.event", groupId = "meal-nutrition")
	public void consumeSignup(UserEvent event) {
		log.info("Kafka User Event 수신 ID : {}, User Name : {}, Email : {}", event.getId(), event.getUsername(),
			event.getEmail());
		userRepository.save(User.from(event));
	}
}
