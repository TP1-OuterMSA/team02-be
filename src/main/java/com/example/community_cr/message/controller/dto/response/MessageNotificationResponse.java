package com.example.community_cr.message.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.message.entity.MessageNotification;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageNotificationResponse {
	private String message;
	private long messageId;
	private long senderId;
	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sentAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	public static MessageNotificationResponse from(MessageNotification messageNotification) {
		return MessageNotificationResponse.builder()
			.message(messageNotification.getNotificationMessage())
			.messageId(messageNotification.getMessage().getId())
			.senderId(messageNotification.getSender().getId())
			.content(messageNotification.getMessage().getContent())
			.sentAt(messageNotification.getMessage().getSentAt())
			.createdAt(messageNotification.getCreatedAt())
			.build();
	}
}
