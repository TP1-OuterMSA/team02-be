package com.example.community_cr.community.comment_like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.community.comment_like.entity.CommentHeart;
import com.example.community_cr.community.comment_like.entity.CommentHeartId;

public interface CommentLikeRepository extends JpaRepository<CommentHeart, CommentHeartId> {
	boolean existsByUserIdAndCommentId(Long userId, Long commentId);
}
