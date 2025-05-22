package com.example.community_cr.mealMatch.notification.component;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.community_cr.mealMatch.match.entity.MatchOfferNotification;
import com.example.kafka_schemas.MatchNotificationEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationProducer {
	private final KafkaTemplate<String, MatchNotificationEvent> kafkaTemplate;

	public void sendNotification(MatchOfferNotification matchOfferNotification) {
		try {
			MatchNotificationEvent matchNotificationEvent = createMatchNotification(matchOfferNotification);
			ProducerRecord<String, MatchNotificationEvent> producerRecord = new ProducerRecord<>(
				"match.notification.created", "notification.event", matchNotificationEvent);
			kafkaTemplate.send(producerRecord);
			log.info("Kafka 전송 성공 - matchNotificationEvent: {}", matchNotificationEvent);
		} catch (Exception e) {
			log.error("Kafka 전송 실패", e);
		}
	}

	private MatchNotificationEvent createMatchNotification(MatchOfferNotification matchOfferNotification) {
		return new MatchNotificationEvent(matchOfferNotification.getId());
	}
}
