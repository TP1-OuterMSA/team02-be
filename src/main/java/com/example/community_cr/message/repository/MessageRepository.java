package com.example.community_cr.message.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.message.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
	@Query("""
			SELECT m FROM Message m
			WHERE (
			(m.sender.id = :me AND m.receiver.id = :other)
			OR (m.sender.id = :other AND m.receiver.id = :me)
			)
			AND m.id < :cursor
			ORDER BY m.sentAt DESC
		""")
	List<Message> findChatHistory(@Param("me") Long me,
		@Param("other") Long other,
		@Param("cursor") Long cursor,
		Pageable pageable);

	@Query("""
			SELECT m FROM Message m
			WHERE m.receiver.id = :me
			AND m.id < :cursor
			ORDER BY m.sentAt DESC
		""")
	List<Message> findChatHistory(@Param("me") Long me,
		@Param("cursor") Long cursor,
		Pageable pageable);
}
