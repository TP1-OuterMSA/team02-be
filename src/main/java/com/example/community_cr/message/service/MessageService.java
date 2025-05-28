package com.example.community_cr.message.service;

import java.util.List;

import com.example.community_cr.message.controller.dto.request.MessageRequest;
import com.example.community_cr.message.controller.dto.response.MessageResponse;

public interface MessageService {
	MessageResponse createMessage(MessageRequest request, long sender);

	void createMessage(String content, long senderId, long receiverId);

	List<MessageResponse> getMessages(Long me, Long other, Long cursor, int count);

	void deleteMessage(long userId, long messageId);
}

