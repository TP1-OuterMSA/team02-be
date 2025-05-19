package com.example.community_cr.mealMatch.match.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.match.entity.MealPost;

public interface MealPostRepository extends JpaRepository<MealPost, Long> {
	@Query("""
			SELECT mp FROM MealPost mp
			WHERE mp.place.id IN :placeIds
			ORDER BY mp.createdAt DESC
		""")
	Slice<MealPost> findAllByPlaceIdInOrderByCreatedAtDesc(@Param("placeIds") List<Long> placeIds,
		PageRequest pageRequest);

	@Query("""
			SELECT mp FROM MealPost mp
			WHERE mp.id < :cursor
			AND mp.place.id IN :placeIds
			ORDER BY mp.createdAt DESC
		""")
	Slice<MealPost> findNextPagePosts(@Param("placeIds") List<Long> placeIds,
		@Param("cursor") long cursor, PageRequest pageRequest);
}
