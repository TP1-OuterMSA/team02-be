package com.example.community_cr.community.post.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.community.post.entity.Post;

public interface CommunityRepository extends JpaRepository<Post, Long> {
	// Slice<Post> findTopByOrderByCreatedAtDesc(PageRequest pageRequest);
	//
	// @Query("SELECT p FROM Post p WHERE p.id <= ?1 ORDER BY p.createdAt DESC")
	// Slice<Post> findNextPagePosts(long cursor, PageRequest pageRequest);
	@Query("""
		    SELECT p FROM Post p
		    WHERE (:cursor = 0 OR p.id <= :cursor)
		    AND NOT EXISTS (
		        SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
		    )
		    ORDER BY p.createdAt DESC
		""")
	Slice<Post> findCommunityPostsExcludeBlocked(@Param("userId") long userId, @Param("cursor") long cursor,
		Pageable pageable);
}
