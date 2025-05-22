package com.example.community_cr.message.controller.dto;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

public class MessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;

    public MessageResponse(Long id, Long senderId, Long receiverId, String content, LocalDateTime sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
}
