package com.example.community_cr.message.repository;

import com.example.community_cr.message.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE ((m.senderId = :me AND m.receiverId = :other) " +
            "OR (m.senderId = :other AND m.receiverId = :me)) " +
            "AND m.id < :cursor ORDER BY m.id DESC")
    List<Message> findChatHistory(@Param("me") Long me,
                                  @Param("other") Long other,
                                  @Param("cursor") Long cursor,
                                  Pageable pageable);
}
