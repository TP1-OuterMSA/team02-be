package com.example.community_cr.mealMatch.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
	SseEmitter subscribe(long userId);

	void send(long receiverId, Object notification);

	void sendPastEvents(long userId, String lastEventId);
}
