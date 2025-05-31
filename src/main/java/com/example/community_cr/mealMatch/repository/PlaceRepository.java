package com.example.community_cr.mealMatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.mealMatch.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	@Query("""
			SELECT p FROM Place p
			WHERE (p.latitude = :lat AND p.longitude = :lng)
			OR p.address = :address
		""")
	Optional<Place> findByLongitudeAndLatitudeOrAddress(@Param("lat") Double latitude,
		@Param("lng") Double longitude,
		@Param("address") String address);

	@Query("""
			SELECT p FROM Place p
			WHERE (:nwLon <= p.longitude AND p.longitude <= :seLon)
			AND (:seLat <= p.latitude AND p.latitude <= :nwLat)
		""")
	List<Place> findAllByPointIn(@Param("nwLon") double nwLongitude, @Param("nwLat") double nwLatitude,
		@Param("seLon") double seLongitude, @Param("seLat") double seLatitude);

	@Query("""
			SELECT p.id FROM Place p
			WHERE p.address = :address
		""")
	List<Long> findAllPlaceIdByAddress(@Param("address") String address);
}
