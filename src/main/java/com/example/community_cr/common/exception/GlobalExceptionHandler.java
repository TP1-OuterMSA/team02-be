package com.example.community_cr.common.exception;

import java.sql.SQLIntegrityConstraintViolationException;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info("Method Argument Not Valid Exception: {}", messages);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(messages);
	}

	// DB 무결성 제약 위반
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleDataIntegrityViolationException(
		DataIntegrityViolationException exception) {
		log.info("Data Integrity Violation Exception messages: {}", exception.getMessage());
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

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<String> handleSQLIntegrityConstraintViolationException(
		SQLIntegrityConstraintViolationException exception) {
		log.info("SQL Integrity Constraint Violation Exception messages: {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(exception.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
		log.info("Illegal Argument Exception: {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {
		log.info("Illegal State Exception: {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ApiErrorException.class)
	public ResponseEntity<String> handleApiErrorException(ApiErrorException exception) {
		log.info("Api Error Exception: {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body("Code : [" + exception.getCode() + "], Message : " + exception.getMessage());
	}
}
