package com.example.community_cr.mealMatch.match.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.match.entity.MatchPost;

public interface MatchPostRepository extends JpaRepository<MatchPost, Long> {
	@Query("""
			SELECT mp FROM MatchPost mp
			WHERE mp.place.id IN :placeIds
			ORDER BY mp.createdAt DESC
		""")
	Slice<MatchPost> findAllByPlaceIdInOrderByCreatedAtDesc(@Param("placeIds") List<Long> placeIds,
		PageRequest pageRequest);

	@Query("""
			SELECT mp FROM MatchPost mp
			WHERE mp.id < :cursor
			AND mp.place.id IN :placeIds
			ORDER BY mp.createdAt DESC
		""")
	Slice<MatchPost> findNextPagePosts(@Param("placeIds") List<Long> placeIds,
		@Param("cursor") long cursor, PageRequest pageRequest);
}
