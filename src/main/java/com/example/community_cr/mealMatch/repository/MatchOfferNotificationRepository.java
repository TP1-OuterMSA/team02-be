package com.example.community_cr.mealMatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.entity.MatchOfferNotification;

public interface MatchOfferNotificationRepository extends JpaRepository<MatchOfferNotification, String> {
	@Query("SELECT e FROM MatchOfferNotification e WHERE e.id LIKE CONCAT(:prefix, '%') AND e.id > :lastEventId")
	List<MatchOfferNotification> findAllEventCacheStartWithIdAndGreaterThanLastEventId(@Param("prefix") String prefix,
		@Param("lastEventId") String lastEventId);

	@Query("SELECT e FROM MatchOfferNotification e WHERE e.id LIKE CONCAT(:prefix, '%')")
	List<MatchOfferNotification> findAllEventCacheStartWithId(@Param("prefix") String prefix);

	@Query("SELECT n FROM MatchOfferNotification n JOIN FETCH n.matchOffer WHERE n.id = :id")
	Optional<MatchOfferNotification> findWithOfferById(String id);
}
