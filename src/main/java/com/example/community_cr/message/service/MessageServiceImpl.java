package com.example.community_cr.message.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.message.controller.dto.request.MessageRequest;
import com.example.community_cr.message.controller.dto.response.MessageResponse;
import com.example.community_cr.message.entity.Message;
import com.example.community_cr.message.entity.MessageNotification;
import com.example.community_cr.message.repository.MessageNotificationRepository;
import com.example.community_cr.message.repository.MessageRepository;
import com.example.community_cr.notification.component.NotificationEventPublisher;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final MessageNotificationRepository messageNotificationRepository;

	private final NotificationEventPublisher eventPublisher;

	@Override
	public MessageResponse createMessage(MessageRequest messageRequest, long senderId) {
		if (messageRequest.getReceiverId() == senderId) {
			throw new IllegalArgumentException("전송자와 수신자의 ID가 같습니다.");
		}
		User sender = userRepository.findById(senderId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 없습니다."));
		User receiver = userRepository.findById(messageRequest.getReceiverId())
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 없습니다."));

		Message message = Message.of(sender, receiver, messageRequest.getContent(), LocalDateTime.now());
		message = messageRepository.save(message);

		String notificationMessage = "새로운 쪽지가 왔습니다.";
		String notificationId = message.getReceiver().getId() + "_" + System.currentTimeMillis();
		MessageNotification messageNotification = MessageNotification.builder()
			.id(notificationId)
			.sender(sender)
			.receiver(receiver)
			.message(message)
			.notificationMessage(notificationMessage)
			.createdAt(LocalDateTime.now())
			.build();
		messageNotification = messageNotificationRepository.save(messageNotification);

		eventPublisher.publish(messageNotification);

		return MessageResponse.from(message);
	}

	@Override
	public void createMessage(String content, long senderId, long receiverId) {
		if (receiverId == senderId) {
			return;
		}
		User sender = userRepository.findById(senderId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 없습니다."));
		User receiver = userRepository.findById(receiverId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 없습니다."));

		Message message = Message.of(sender, receiver, content, LocalDateTime.now());
		message = messageRepository.save(message);

		String notificationMessage = "새로운 쪽지가 왔습니다.";
		String notificationId = message.getReceiver().getId() + "_" + System.currentTimeMillis();
		MessageNotification messageNotification = MessageNotification.builder()
			.id(notificationId)
			.sender(sender)
			.receiver(receiver)
			.message(message)
			.notificationMessage(notificationMessage)
			.createdAt(LocalDateTime.now())
			.build();
		messageNotification = messageNotificationRepository.save(messageNotification);

		eventPublisher.publish(messageNotification);
	}

	@Override
	public List<MessageResponse> getMessages(Long me, Long other, Long cursor, int count) {
		Long effectiveCursor = (cursor == null || cursor <= 0) ? Long.MAX_VALUE : cursor;
		List<Message> messages = messageRepository.findChatHistory(
			me, other, effectiveCursor, PageRequest.of(0, count));
		return messages.stream()
			.map(MessageResponse::from)
			.toList();
	}

	@Override
	public List<MessageResponse> getMessages(Long me, Long cursor, int count) {
		Long effectiveCursor = (cursor == null || cursor <= 0) ? Long.MAX_VALUE : cursor;
		List<Message> messages = messageRepository.findChatHistory(
			me, effectiveCursor, PageRequest.of(0, count));
		return messages.stream()
			.map(MessageResponse::from)
			.toList();
	}

	@Override
	@Transactional
	public void deleteMessage(long userId, long messageId) {
		Message message = messageRepository.findById(messageId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 쪽지 정보가 존재하지 않습니다."));
		if (message.getSender().getId() != userId) {
			throw new IllegalArgumentException("쪽지를 보낸 사람만 쪽지를 삭제할 수 있습니다.");
		}
		messageRepository.delete(message);
	}
}


