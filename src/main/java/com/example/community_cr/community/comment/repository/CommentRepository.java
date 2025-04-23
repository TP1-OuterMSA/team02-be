package com.example.community_cr.community.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.community.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
