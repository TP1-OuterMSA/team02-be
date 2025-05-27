package com.example.community_cr.mealMatch.notification.component;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.community_cr.mealMatch.match.entity.MatchOfferNotification;
import com.example.community_cr.mealMatch.match.repository.MatchOfferNotificationRepository;
import com.example.community_cr.mealMatch.notification.controller.dto.response.MatchOfferNotificationResponse;
import com.example.community_cr.mealMatch.notification.repository.EmitterRepository;
import com.example.community_cr.mealMatch.notification.service.NotificationService;
import com.example.kafka_schemas.MatchNotificationEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationConsumer {
	private final EmitterRepository emitterRepository;
	private final MatchOfferNotificationRepository matchOfferNotificationRepository;

	private final NotificationService notificationService;

	@KafkaListener(topics = "match.notification.created", groupId = "notification-#{T(java.util.UUID).randomUUID().toString()}")
	public void consume(MatchNotificationEvent matchNotificationEvent) {
		log.info("Kafka 이벤트 수신 : {}", matchNotificationEvent.getNotificationId());

		Optional<MatchOfferNotification> optionalMatchOfferNotification =
			matchOfferNotificationRepository.findWithOfferById(matchNotificationEvent.getNotificationId());
		if (optionalMatchOfferNotification.isEmpty()) {
			log.info("해당하는 SSE Emitter 존재하지 않음");
			return;
		}
		MatchOfferNotification matchOfferNotification = optionalMatchOfferNotification.get();

		long receiverId = matchOfferNotification.getReceiver().getId();
		if (!emitterRepository.existsByIdStartWith(receiverId)) {
			return;
		}

		MatchOfferNotificationResponse matchOfferNotificationResponse = MatchOfferNotificationResponse.from(
			matchOfferNotification);

		notificationService.send(receiverId, matchOfferNotificationResponse);
	}
}
