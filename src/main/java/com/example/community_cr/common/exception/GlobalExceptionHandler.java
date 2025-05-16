package com.example.community_cr.common.exception;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	// Validation 에러
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<String>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException exception) {
		List<String> messages = exception.getBindingResult().getAllErrors().stream()
			.map(ObjectError::getDefaultMessage)
			.toList();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(messages);
	}

	// DB 무결성 제약 위반
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleDataIntegrityViolationException(
		DataIntegrityViolationException exception) {
		if (exception.getCause() instanceof ConstraintViolationException violationException) {
			String sqlMessage = violationException.getSQLException().getMessage();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(sqlMessage);
		} else {
			Throwable rootCause = ExceptionUtils.getRootCause(exception);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(rootCause.getMessage());
		}
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ApiErrorException.class)
	public ResponseEntity<String> handleApiErrorException(ApiErrorException exception) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body("Code : [" + exception.getCode() + "], Message : " + exception.getMessage());
	}
}
