package com.example.community_cr.comment_like.service;

public interface CommentLikeService {
    void likeComment(long userId, long commentId);
    void unlikeComment(long userId, long commentId);
}

