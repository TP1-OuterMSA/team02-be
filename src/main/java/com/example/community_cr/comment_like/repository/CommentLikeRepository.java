package com.example.community_cr.comment_like.repository;

import com.example.community_cr.comment_like.entity.CommentHeart;
import com.example.community_cr.comment_like.entity.CommentHeartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentLikeRepository extends JpaRepository<CommentHeart, CommentHeartId> {

    long countByCommentId(Long commentId);
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

}
