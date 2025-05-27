package com.example.community_cr.notification.component;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.community_cr.mealMatch.entity.MatchOfferNotification;
import com.example.community_cr.message.entity.MessageNotification;
import com.example.kafka_schemas.NotificationEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationProducer {
	private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

	public void sendNotification(MatchOfferNotification matchOfferNotification) {
		try {
			NotificationEvent matchNotificationEvent = createNotificationEvent(matchOfferNotification);
			ProducerRecord<String, NotificationEvent> producerRecord = new ProducerRecord<>(
				"match.notification.created", "notification.event", matchNotificationEvent);
			kafkaTemplate.send(producerRecord);
			log.info("Kafka 전송 성공 - matchNotificationEvent: {}", matchNotificationEvent);
		} catch (Exception e) {
			log.error("Kafka 전송 실패", e);
		}
	}

	public void sendNotification(MessageNotification messageNotification) {
		try {
			NotificationEvent notificationEvent = createNotificationEvent(messageNotification);
			ProducerRecord<String, NotificationEvent> producerRecord = new ProducerRecord<>(
				"message.notification.created", "notification.event", notificationEvent);
			kafkaTemplate.send(producerRecord);
			log.info("Kafka 전송 성공 - messageNotificationEvent: {}", notificationEvent);
		} catch (Exception e) {
			log.error("Kafka 전송 실패", e);
		}
	}

	private NotificationEvent createNotificationEvent(MatchOfferNotification matchOfferNotification) {
		return new NotificationEvent(matchOfferNotification.getId());
	}

	private NotificationEvent createNotificationEvent(MessageNotification messageNotification) {
		return new NotificationEvent(messageNotification.getId());
	}
}
