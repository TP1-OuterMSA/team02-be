package com.example.community_cr.community.post.Repository;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.community.post.entity.Post;

public interface CommunityRepository extends JpaRepository<Post, Long> {
	// All 가져오기
	@Query("""
			SELECT p FROM Post p
			WHERE NOT EXISTS (
				SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
			)
			ORDER BY p.createdAt DESC
		""")
	Slice<Post> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);

	@Query("""
			SELECT p FROM Post p
			WHERE p.id < :cursor
			AND NOT EXISTS (
				SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
			)
			ORDER BY p.createdAt DESC
		""")
	Slice<Post> findNextPagePosts(@Param("cursor") long cursor, PageRequest pageRequest);

	// User Id로 가져오기 (나의 게시글 가져오기에 사용 -> Block 필터링이 없음)
	@Query("""
			SELECT p FROM Post p
			WHERE p.user.id = :userId
			ORDER BY p.createdAt DESC
		""")
	Slice<Post> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") long userId, Pageable pageable);

	@Query("""
			SELECT p FROM Post p
			WHERE p.user.id = :userId AND p.id < :cursor
			ORDER BY p.createdAt DESC
		""")
	Slice<Post> findNextPagePostsByUserId(@Param("userId") long userId, @Param("cursor") long cursor,
		Pageable pageable);

	// 좋아요 갯수 순으로 가져오기

	@Query("""
			SELECT COUNT(h) FROM Post p
			LEFT JOIN p.heartList h
			WHERE p.id = :postId
				AND NOT EXISTS (
					SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
				)
			GROUP BY p
			ORDER BY COUNT(h) DESC, p.id DESC
		""")
	Optional<Long> getLikeCountById(@Param("postId") long postId);

	@Query("""
			SELECT p FROM Post p
			LEFT JOIN p.heartList h
			WHERE NOT EXISTS (
				SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
			)
			GROUP BY p
			ORDER BY COUNT(h) DESC, p.id DESC
		""")
	Slice<Post> findAllOrderByHeartCountDesc(Pageable pageable);

	@Query("""
		    SELECT p FROM Post p
		    LEFT JOIN p.heartList h
			WHERE NOT EXISTS (
				SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
			)
		    GROUP BY p.id
		    HAVING (COUNT(h) < :heartCount
		        OR (COUNT(h) = :heartCount AND p.id < :cursor))
		    ORDER BY COUNT(h) DESC, p.id DESC
		""")
	Slice<Post> findNextPageOrderByHeartCountDesc(@Param("heartCount") long heartCount,
		@Param("cursor") long cursor, Pageable pageable);

	// 댓글 갯수 순으로 가져오기

	@Query("""
			SELECT COUNT(c) FROM Post p
			LEFT JOIN p.commentList c
			WHERE p.id = :postId
			AND NOT EXISTS (
				SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
			)
			GROUP BY p
			ORDER BY COUNT(c) DESC, p.id DESC
		""")
	Optional<Long> getCommentCountById(@Param("postId") long postId);

	@Query("""
			SELECT p FROM Post p
			LEFT JOIN p.commentList c
			WHERE NOT EXISTS (
					SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
				)
			GROUP BY p
			ORDER BY COUNT(c) DESC, p.id DESC
		""")
	Slice<Post> findAllOrderByCommentCountDesc(Pageable pageable);

	@Query("""
		    SELECT p FROM Post p
		    LEFT JOIN p.commentList c
			WHERE NOT EXISTS (
				SELECT 1 FROM Block b WHERE b.user.id = :userId AND b.post.id = p.id
			)
		    GROUP BY p.id
		    HAVING (COUNT(c) < :commentCount
		        OR (COUNT(c) = :commentCount AND p.id < :cursor))
		    ORDER BY COUNT(c) DESC, p.id DESC
		""")
	Slice<Post> findNextPageOrderByCommentCountDesc(@Param("commentCount") long commentCount,
		@Param("cursor") long cursor, Pageable pageable);
}
