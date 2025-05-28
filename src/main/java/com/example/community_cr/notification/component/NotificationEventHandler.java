package com.example.community_cr.notification.component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.community_cr.mealMatch.entity.MatchOfferNotification;
import com.example.community_cr.message.entity.MessageNotification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
	private final NotificationProducer notificationProducer;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleMessageNotification(MessageNotification messageNotification) {
		notificationProducer.sendNotification(messageNotification);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleMatchOfferNotification(MatchOfferNotification matchOfferNotification) {
		notificationProducer.sendNotification(matchOfferNotification);
	}
}

