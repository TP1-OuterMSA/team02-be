package com.example.community_cr.community.comment.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.community.comment.controller.dto.request.CommentRequest;
import com.example.community_cr.community.comment.controller.dto.request.UpdateCommentRequest;
import com.example.community_cr.community.comment.controller.dto.response.CommentResponse;
import com.example.community_cr.community.comment.entity.Comment;
import com.example.community_cr.community.comment.repository.CommentRepository;
import com.example.community_cr.community.post.Repository.CommunityRepository;
import com.example.community_cr.community.post.entity.Post;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final CommunityRepository communityRepository;
	private final UserRepository userRepository;

	@Override
	public Optional<CommentResponse> saveComment(long userId, long postId, CommentRequest commentRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(IllegalArgumentException::new);
		Post post = communityRepository.findById(postId)
			.orElseThrow(IllegalArgumentException::new);
		Comment comment = commentRequest.toEntity(LocalDateTime.now(), post, user);
		comment = commentRepository.save(comment);
		post.addComment(comment);
		communityRepository.save(post);
		return Optional.of(CommentResponse.from(comment));
	}

	@Override
	public Optional<CommentResponse> updateComment(long userId, long commentId, UpdateCommentRequest request) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		if (comment.getUser().getId() != userId) {
			throw new IllegalArgumentException("자신의 댓글만 수정할 수 있습니다.");
		}

		comment.updateContent(request.getContent());
		commentRepository.save(comment);

		return Optional.of(CommentResponse.from(comment));
	}

	@Override
	public void deleteComment(long userId, long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		if (comment.getUser().getId() != userId) {
			throw new IllegalArgumentException("자신의 댓글만 삭제할 수 있습니다.");
		}
		Post post = comment.getPost();
		
		commentRepository.delete(comment);

		post.removeComment(comment);
		communityRepository.save(post);
	}
}
