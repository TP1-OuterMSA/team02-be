package com.example.community_cr.diet.controller.dto.response.api;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApiErrorResponse {
	private ErrorDetail error;
	private String user_id;

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class ErrorDetail {
		private String message;
		private int code;
		private Metadata metadata;
	}

	@Getter
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class Metadata {
		private Map<String, String> headers;
		private String provider_name;
	}
}
