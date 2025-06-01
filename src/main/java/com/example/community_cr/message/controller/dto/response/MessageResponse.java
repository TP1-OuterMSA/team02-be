package com.example.community_cr.message.controller.dto.response;

import java.time.LocalDateTime;

import com.example.community_cr.message.entity.Message;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
	private Long id;
	private Long senderId;
	private Long receiverId;
	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sentAt;

	public static MessageResponse from(Message message) {
		return MessageResponse.builder()
			.id(message.getId())
			.senderId(message.getSender().getId())
			.receiverId(message.getReceiver().getId())
			.content(message.getContent())
			.sentAt(message.getSentAt())
			.build();
	}
}
