package com.example.community_cr.message.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.message.controller.dto.request.MessageRequest;
import com.example.community_cr.message.controller.dto.response.MessageResponse;
import com.example.community_cr.message.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/message")
public class MessageController {
	private final MessageService service;

	@PostMapping("/createMessage")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<MessageResponse> createMessage(
		@RequestHeader("userId") long sender,
		@RequestBody @Valid MessageRequest req
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(service.createMessage(req, sender));
	}

	@GetMapping("/getMessages/{otherId}")
	public ResponseEntity<List<MessageResponse>> listMessages(
		@RequestHeader("userId") long userId,
		@PathVariable long otherId,
		@RequestParam(name = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(name = "count", required = false, defaultValue = "3") int count
	) {
		return ResponseEntity.ok(
			service.getMessages(userId, otherId, cursor, count));
	}

	@GetMapping("/getMessages")
	public ResponseEntity<List<MessageResponse>> listMessages(
		@RequestHeader("userId") long userId,
		@RequestParam(name = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(name = "count", required = false, defaultValue = "3") int count
	) {
		return ResponseEntity.ok(
			service.getMessages(userId, cursor, count));
	}

	@DeleteMapping("/deleteMessage/{messageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMessage(
		@RequestHeader("userId") long userId,
		@PathVariable long messageId) {
		service.deleteMessage(userId, messageId);
	}
}

