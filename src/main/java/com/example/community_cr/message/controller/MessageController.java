package com.example.community_cr.message.controller;

import com.example.community_cr.message.controller.dto.MessageRequest;
import com.example.community_cr.message.controller.dto.MessageResponse;
import com.example.community_cr.message.entity.Message;
import com.example.community_cr.message.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(
            @RequestHeader(name = "user-id") Long me,
            @RequestBody @Valid MessageRequest req) {

        Message msg = new Message();
        msg.setSenderId(me);
        msg.setReceiverId(req.getReceiverId());
        msg.setContent(req.getContent());

        Message saved = service.createMessage(msg);
        return toResponse(saved);
    }

    @GetMapping("/{otherId}")
    public List<MessageResponse> listMessages(
            @RequestHeader(name = "user-id") Long me,
            @PathVariable Long otherId,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "count", defaultValue = "20") int count) {

        List<Message> msgs = service.fetchMessages(me, otherId, cursor, count);
        return msgs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(
            @RequestHeader(name = "user-id") Long me,
            @PathVariable Long messageId) {
        service.deleteMessage(messageId);
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getSentAt()
        );
    }
}

