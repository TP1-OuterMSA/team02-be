package com.example.community_cr.notification.component;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.community_cr.mealMatch.controller.dto.response.MatchOfferNotificationResponse;
import com.example.community_cr.mealMatch.entity.MatchOfferNotification;
import com.example.community_cr.mealMatch.repository.MatchOfferNotificationRepository;
import com.example.community_cr.message.controller.dto.response.MessageNotificationResponse;
import com.example.community_cr.message.entity.MessageNotification;
import com.example.community_cr.message.repository.MessageNotificationRepository;
import com.example.community_cr.notification.repository.EmitterRepository;
import com.example.community_cr.notification.service.NotificationService;
import com.example.kafka_schemas.MatchNotificationEvent;
import com.example.kafka_schemas.NotificationEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationConsumer {
	private final EmitterRepository emitterRepository;
	private final MatchOfferNotificationRepository matchOfferNotificationRepository;
	private final MessageNotificationRepository messageNotificationRepository;

	private final NotificationService notificationService;

	@KafkaListener(topics = "match.notification.created", groupId = "notification-#{T(java.util.UUID).randomUUID().toString()}")
	public void consumeMatchNotification(MatchNotificationEvent notificationEvent) {
		log.info("Kafka 이벤트 수신 : {}", notificationEvent.getNotificationId());

		Optional<MatchOfferNotification> optionalMatchOfferNotification =
			matchOfferNotificationRepository.findWithOfferById(notificationEvent.getNotificationId());
		if (optionalMatchOfferNotification.isEmpty()) {
			log.info("해당하는 알림이 존재하지 않음");
			return;
		}
		MatchOfferNotification matchOfferNotification = optionalMatchOfferNotification.get();

		long receiverId = matchOfferNotification.getReceiver().getId();
		if (!emitterRepository.existsByIdStartWith(receiverId)) {
			log.info("해당하는 SSE Emitter 존재하지 않음");
			return;
		}

		MatchOfferNotificationResponse matchOfferNotificationResponse = MatchOfferNotificationResponse.from(
			matchOfferNotification);

		notificationService.send(receiverId, matchOfferNotificationResponse);
	}

	@KafkaListener(topics = "message.notification.created", groupId = "notification-#{T(java.util.UUID).randomUUID().toString()}")
	public void consumeMessageNotification(NotificationEvent notificationEvent) {
		log.info("Kafka 이벤트 수신 : {}", notificationEvent.getNotificationId());

		Optional<MessageNotification> optionalMessageNotification =
			messageNotificationRepository.findWithReceiverById(notificationEvent.getNotificationId());
		if (optionalMessageNotification.isEmpty()) {
			log.info("해당하는 알림이 존재하지 않음");
			return;
		}
		MessageNotification messageNotification = optionalMessageNotification.get();

		long receiverId = messageNotification.getReceiver().getId();
		if (!emitterRepository.existsByIdStartWith(receiverId)) {
			log.info("해당하는 SSE Emitter 존재하지 않음");
			return;
		}

		MessageNotificationResponse messageOfferNotificationResponse = MessageNotificationResponse.from(
			messageNotification);

		notificationService.send(receiverId, messageOfferNotificationResponse);
	}
}
