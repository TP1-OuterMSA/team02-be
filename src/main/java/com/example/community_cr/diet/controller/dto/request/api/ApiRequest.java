package com.example.community_cr.diet.controller.dto.request.api;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiRequest {
	private String model;
	private List<Message> messages;

	public ApiRequest(String model, String systemMessage, String userMessage) {
		this.model = model;
		Message system = new Message("system", systemMessage);
		Message user = new Message("user", userMessage);
		this.messages = new ArrayList<>();
		this.messages.add(system);
		this.messages.add(user);
	}

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class Message {
		private String role;
		private String content;
	}
}
