package com.example.community_cr.community.Repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.community_cr.community.entity.Post;

public interface CommunityRepository extends JpaRepository<Post, Long> {
	Slice<Post> findTopByOrderByCreatedAtDesc(PageRequest pageRequest);

	@Query("SELECT p FROM Post p WHERE p.id <= ?1 ORDER BY p.createdAt DESC")
	Slice<Post> findNextPagePosts(long cursor, PageRequest pageRequest);
}
