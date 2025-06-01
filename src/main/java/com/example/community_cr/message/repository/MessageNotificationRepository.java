package com.example.community_cr.message.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.message.entity.MessageNotification;

public interface MessageNotificationRepository extends JpaRepository<MessageNotification, String> {
	@Query("SELECT n FROM MessageNotification n JOIN FETCH n.receiver JOIN FETCH n.message WHERE n.id = :id")
	Optional<MessageNotification> findWithReceiverById(@Param("id") String id);
}
