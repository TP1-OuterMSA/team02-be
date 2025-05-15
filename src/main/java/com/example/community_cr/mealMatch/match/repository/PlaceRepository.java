package com.example.community_cr.mealMatch.match.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.match.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	@Query("SELECT p FROM Place p WHERE (p.latitude = :lat AND p.longitude = :lng) OR p.address = :address OR p.name = :name")
	Optional<Place> findByLongitudeAndLatitudeOrAddressOrName(@Param("lat") Double latitude,
		@Param("lng") Double longitude,
		@Param("address") String address, @Param("name") String name);
}
