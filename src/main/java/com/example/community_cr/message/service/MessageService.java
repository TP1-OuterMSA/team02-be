package com.example.community_cr.message.service;

import com.example.community_cr.message.entity.Message;
import java.util.List;

public interface MessageService {
    Message createMessage(Message message);
    List<Message> fetchMessages(Long me, Long other, Long cursor, int count);
    void deleteMessage(Long id);
}

