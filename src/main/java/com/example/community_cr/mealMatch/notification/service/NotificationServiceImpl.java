package com.example.community_cr.mealMatch.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.community_cr.mealMatch.match.entity.MatchOfferNotification;
import com.example.community_cr.mealMatch.match.repository.MatchOfferNotificationRepository;
import com.example.community_cr.mealMatch.notification.controller.dto.response.MatchOfferNotificationResponse;
import com.example.community_cr.mealMatch.notification.repository.EmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
	private final EmitterRepository emitterRepository;
	private final MatchOfferNotificationRepository matchOfferNotificationRepository;

	@Override
	public SseEmitter subscribe(Long userId, String lastEventId) {
		String id = userId + "_" + System.currentTimeMillis();

		SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

		emitter.onCompletion(() -> emitterRepository.deleteById(id));
		emitter.onTimeout(() -> emitterRepository.deleteById(id));

		sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

		List<MatchOfferNotification> notifications;
		if (lastEventId.isEmpty()) {
			notifications = matchOfferNotificationRepository.findAllEventCacheStartWithId(userId + "_");
		} else {
			notifications = matchOfferNotificationRepository.findAllEventCacheStartWithIdAndGreaterThanLastEventId(
				userId + "_", lastEventId);
		}
		notifications.forEach(
			event -> sendToClient(emitter, event.getId(), MatchOfferNotificationResponse.from(event)));

		return emitter;
	}

	@Override
	public String send(long receiverId, Object notification) {
		String id = receiverId + "_" + System.currentTimeMillis();

		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(receiverId + "_");
		sseEmitters.forEach(
			(key, emitter) -> {
				// 데이터 전송
				sendToClient(emitter, id, notification);
			}
		);
		return id;
	}

	private void sendToClient(SseEmitter emitter, String id, Object data) {
		try {
			emitter.send(SseEmitter.event()
				.id(id)
				.name("sse")
				.data(data));
		} catch (IOException exception) {
			log.info("emitter id: {}", id);
			emitterRepository.deleteById(id);
			throw new IllegalStateException("연결 오류!");
		}
	}
}
