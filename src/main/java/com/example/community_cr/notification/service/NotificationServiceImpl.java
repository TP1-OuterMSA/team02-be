package com.example.community_cr.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.community_cr.mealMatch.controller.dto.response.MatchOfferNotificationResponse;
import com.example.community_cr.mealMatch.entity.MatchOfferNotification;
import com.example.community_cr.mealMatch.repository.MatchOfferNotificationRepository;
import com.example.community_cr.notification.repository.EmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
	private final EmitterRepository emitterRepository;
	private final MatchOfferNotificationRepository matchOfferNotificationRepository;

	@Override
	public SseEmitter subscribe(long userId) {
		log.info("Subscribe ID : {}", userId);
		String id = userId + "_" + System.currentTimeMillis();

		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		emitter.onCompletion(() -> {
			log.info("Completion SSE. User : {}", userId);
			emitterRepository.deleteById(id);
		});
		emitter.onTimeout(() -> {
			log.info("Time out SSE. User : {}", userId);
			emitter.complete();
			emitterRepository.deleteById(id);
		});
		emitter.onError(e -> {
			log.info("Error SSE. User : {}, Message: {}", userId, e.getMessage());
			emitter.complete();
			emitterRepository.deleteById(id);
		});

		emitterRepository.save(id, emitter);

		sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

		return emitter;
	}

	@Override
	public void sendPastEvents(long userId, String lastEventId) {
		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(userId);

		List<MatchOfferNotification> notifications;
		if (lastEventId.isEmpty()) {
			notifications = matchOfferNotificationRepository.findAllEventCacheStartWithId(userId + "_");
		} else {
			notifications = matchOfferNotificationRepository.findAllEventCacheStartWithIdAndGreaterThanLastEventId(
				userId + "_", lastEventId);
		}

		sseEmitters.forEach(
			(key, emitter) -> notifications.forEach(
				event -> sendToClient(emitter, event.getId(), MatchOfferNotificationResponse.from(event)))
		);

	}

	@Override
	public void send(long receiverId, Object notification) {
		String id = receiverId + "_" + System.currentTimeMillis();

		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(receiverId);
		sseEmitters.forEach(
			(key, emitter) -> {
				// 데이터 전송
				sendToClient(emitter, id, notification);
			}
		);
	}

	private void sendToClient(SseEmitter emitter, String id, Object data) {
		try {
			emitter.send(SseEmitter.event()
				.id(id)
				.name("sse")
				.data(data));
		} catch (IOException exception) {
			log.info("SSE Emitter Exception ID: {}", id);
			emitterRepository.deleteById(id);
		}
	}
}
