package com.example.community_cr.mealMatch.match.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.match.entity.MealPost;

public interface MealPostRepository extends JpaRepository<MealPost, Long> {
	@Query("""
			SELECT p FROM Post p
			ORDER BY p.createdAt DESC
		""")
	Slice<MealPost> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);

	@Query("""
			SELECT p FROM Post p
			WHERE p.id < :cursor
			ORDER BY p.createdAt DESC
		""")
	Slice<MealPost> findNextPagePosts(@Param("cursor") long cursor, PageRequest pageRequest);
}
