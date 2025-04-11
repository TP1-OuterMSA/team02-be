package com.example.community_cr.comment.service;

import java.util.Optional;

import com.example.community_cr.comment.controller.dto.request.CommentRequest;
import com.example.community_cr.comment.controller.dto.request.UpdateCommentRequest;
import com.example.community_cr.comment.controller.dto.response.CommentResponse;

public interface CommentService {
	Optional<CommentResponse> saveComment(long userId, long postId, CommentRequest commentRequest);

	Optional<CommentResponse> updateComment(long userId, long commentId, UpdateCommentRequest request);

	void deleteComment(long userId, long commentId);
}
