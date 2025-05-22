package com.example.community_cr.message.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MessageRequest {
    @NotNull
    private Long receiverId;

    @NotNull @Size(min = 1, max = 1000)
    private String content;

    // getters/setters
    public Long getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
