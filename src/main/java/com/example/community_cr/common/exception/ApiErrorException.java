package com.example.community_cr.common.exception;

import com.example.community_cr.diet.controller.dto.response.api.ApiErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorException extends RuntimeException {
	private Integer code;

	public ApiErrorException(Integer code, String message) {
		super(message);
		this.code = code;
	}

	public ApiErrorException(ApiErrorResponse apiErrorResponse) {
		super(apiErrorResponse.getError().getMessage());
		this.code = apiErrorResponse.getError().getCode();
	}
}
