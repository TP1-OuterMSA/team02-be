package com.example.community_cr.community.post_like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.community.post_like.entity.Heart;
import com.example.community_cr.community.post_like.entity.HeartId;

public interface LikeRepository extends JpaRepository<Heart, HeartId> {
}
