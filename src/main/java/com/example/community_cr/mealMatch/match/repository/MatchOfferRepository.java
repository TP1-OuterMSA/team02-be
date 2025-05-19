package com.example.community_cr.mealMatch.match.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.match.entity.MatchOffer;

public interface MatchOfferRepository extends JpaRepository<MatchOffer, Long> {
	boolean existsByUserIdAndMealPostId(long userId, long mealPostId);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.mealPost.user.id = :userId
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") long userId, PageRequest pageRequest);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.mealPost.user.id = :userId
		    AND m.id < :cursor
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByUserIdNextPagePosts(@Param("userId") long userId, @Param("cursor") long cursor,
		PageRequest pageRequest);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.mealPost.id = :mealPostId
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByMealPostIdOrderByCreatedAtDesc(@Param("mealPostId") long mealPostId,
		PageRequest pageRequest);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.mealPost.id = :mealPostId
		    AND m.id < :cursor
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByMealPostIdNextPagePosts(@Param("mealPostId") long mealPostId,
		@Param("cursor") long cursor, PageRequest pageRequest);
}
