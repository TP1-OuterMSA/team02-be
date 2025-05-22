package com.example.community_cr.message.service;

import com.example.community_cr.message.entity.Message;
import com.example.community_cr.message.repository.MessageRepository;
import com.example.community_cr.message.service.MessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repo;

    public MessageServiceImpl(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public Message createMessage(Message message) {
        message.setSentAt(LocalDateTime.now());
        return repo.save(message);
    }

    @Override
    public List<Message> fetchMessages(Long me, Long other, Long cursor, int count) {
        Long effectiveCursor = (cursor == null || cursor <= 0) ? Long.MAX_VALUE : cursor;
        return repo.findChatHistory(me, other, effectiveCursor, PageRequest.of(0, count));
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        repo.deleteById(id);
    }
}


