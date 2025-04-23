package com.example.community_cr.community.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.community.like.entity.Heart;
import com.example.community_cr.community.like.entity.HeartId;

public interface LikeRepository extends JpaRepository<Heart, HeartId> {
}
