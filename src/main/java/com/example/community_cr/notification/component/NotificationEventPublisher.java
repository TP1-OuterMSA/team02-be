package com.example.community_cr.notification.component;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.community_cr.mealMatch.entity.MatchOfferNotification;
import com.example.community_cr.message.entity.MessageNotification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

	private final ApplicationEventPublisher publisher;

	public void publish(MessageNotification messageNotification) {
		publisher.publishEvent(messageNotification);
	}

	public void publish(MatchOfferNotification matchOfferNotification) {
		publisher.publishEvent(matchOfferNotification);
	}
}
