package com.example.community_cr.school_event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community_cr.school_event.entity.SchoolEvent;

public interface SchoolEventRepository extends JpaRepository<SchoolEvent, Long> {
	@Query("""
		    SELECT se
		    FROM SchoolEvent se
		    ORDER BY se.dayInfo DESC
		""")
	Slice<SchoolEvent> findAllOrderByDayInfoDesc(PageRequest pageRequest);

	@Query("""
		    SELECT se
		    FROM SchoolEvent se
		    WHERE se.id < :cursor
		    ORDER BY se.dayInfo DESC
		""")
	Slice<SchoolEvent> findAllByCursorOrderByDayInfoDesc(@Param("cursor") long cursor, PageRequest pageRequest);
}
