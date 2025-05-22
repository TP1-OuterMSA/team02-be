package com.example.community_cr.mealMatch.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
	SseEmitter subscribe(Long id, String lastEventId);

	void send(long receiverId, Object notification);
}
