package com.example.community_cr.comment_like.controller;

import com.example.community_cr.comment_like.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/community")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/comment/like/{commentId}")
    public ResponseEntity<Void> likeComment(
            @RequestHeader("user-id") long userId,
            @PathVariable(name = "commentId") long commentId) {
        commentLikeService.likeComment(userId, commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/unlike/{commentId}")
    public ResponseEntity<Void> unlikeComment(
            @RequestHeader("user-id") long userId,
            @PathVariable(name = "commentId") long commentId) {
        commentLikeService.unlikeComment(userId, commentId);
        return ResponseEntity.ok().build();
    }
}
