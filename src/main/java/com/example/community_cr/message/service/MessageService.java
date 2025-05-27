package com.example.community_cr.message.service;

import java.util.List;

import com.example.community_cr.message.controller.dto.MessageRequest;
import com.example.community_cr.message.controller.dto.MessageResponse;

public interface MessageService {
	MessageResponse createMessage(MessageRequest request, long sender);

	List<MessageResponse> getMessages(Long me, Long other, Long cursor, int count);

	void deleteMessage(long userId, long messageId);
}

