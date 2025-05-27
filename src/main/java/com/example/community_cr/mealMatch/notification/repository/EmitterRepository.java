package com.example.community_cr.mealMatch.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void save(String id, SseEmitter emitter) {
		emitters.put(id, emitter);
	}

	public void deleteById(String id) {
		emitters.remove(id);
	}

	public Map<String, SseEmitter> findAllStartWithById(long userId) {
		return emitters.entrySet().stream()
			.filter(entry -> entry.getKey().startsWith(userId + "_"))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public boolean existsByIdStartWith(long userId) {
		return emitters.entrySet().stream()
			.anyMatch(entry -> entry.getKey().startsWith(userId + "_"));
	}
}
