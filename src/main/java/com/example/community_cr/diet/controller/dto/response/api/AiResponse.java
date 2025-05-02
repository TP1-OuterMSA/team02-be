package com.example.community_cr.diet.controller.dto.response.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AiResponse {
	private String id;
	private String provider;
	private String model;
	private String object;
	private long created;
	private List<Choice> choices;
	private Usage usage;

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class Choice {
		private Object logprobs;
		private String finish_reason;
		private String native_finish_reason;
		private int index;
		private Message message;
	}

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class Message {
		private String role;
		private String content;
		private Object refusal;
		private Object reasoning;
	}

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class Usage {
		private int prompt_tokens;
		private int completion_tokens;
		private int total_tokens;
		private String prompt_tokens_details;
	}
}
