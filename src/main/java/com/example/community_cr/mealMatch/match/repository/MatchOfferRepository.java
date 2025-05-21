package com.example.community_cr.mealMatch.match.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.match.entity.MatchOffer;

public interface MatchOfferRepository extends JpaRepository<MatchOffer, Long> {
	boolean existsByUserIdAndMatchPostId(long userId, long mealPostId);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.matchPost.user.id = :userId
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") long userId, PageRequest pageRequest);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.matchPost.user.id = :userId
		    AND m.id < :cursor
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByUserIdNextPagePosts(@Param("userId") long userId, @Param("cursor") long cursor,
		PageRequest pageRequest);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.matchPost.id = :matchPostId
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByMatchPostIdOrderByCreatedAtDesc(@Param("matchPostId") long matchPostId,
		PageRequest pageRequest);

	@Query("""
		    SELECT m
		    FROM MatchOffer m
		    WHERE m.matchPost.id = :matchPostId
		    AND m.id < :cursor
		    ORDER BY m.createdAt DESC
		""")
	Slice<MatchOffer> findAllByMatchPostIdNextPagePosts(@Param("matchPostId") long matchPostId,
		@Param("cursor") long cursor, PageRequest pageRequest);
}
