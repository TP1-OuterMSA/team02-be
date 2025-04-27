package com.example.community_cr.comment_like.service;

import com.example.community_cr.comment.entity.Comment;
import com.example.community_cr.comment.repository.CommentRepository;
import com.example.community_cr.comment_like.entity.CommentHeart;
import com.example.community_cr.comment_like.entity.CommentHeartId;
import com.example.community_cr.comment_like.repository.CommentLikeRepository;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public void likeComment(long userId, long commentId) {
        if (commentLikeRepository.existsById(CommentHeartId.of(commentId, userId))) {
            throw new IllegalArgumentException("이미 좋아요한 댓글입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        commentLikeRepository.save(CommentHeart.of(user, comment));
    }

    @Override
    public void unlikeComment(long userId, long commentId) {
        CommentHeartId heartId = CommentHeartId.of(commentId, userId);
        if (!commentLikeRepository.existsById(heartId)) {
            throw new IllegalArgumentException("좋아요 기록이 없습니다.");
        }
        commentLikeRepository.deleteById(heartId);
    }

    public long countLikesOnComment(Long commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }
}
