package com.example.community_cr.school_event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community_cr.school_event.entity.SchoolEvent;

public interface SchoolEventRepository extends JpaRepository<SchoolEvent, Long> {
	List<SchoolEvent> findAllByDayInfo(String date);

	List<SchoolEvent> findAllByDayInfoIn(List<String> string);
}
