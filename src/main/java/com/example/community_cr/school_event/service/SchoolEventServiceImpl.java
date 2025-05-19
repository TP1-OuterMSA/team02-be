package com.example.community_cr.school_event.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.school_event.controller.dto.response.SchoolEventResponse;
import com.example.community_cr.school_event.entity.SchoolEvent;
import com.example.community_cr.school_event.repository.SchoolEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolEventServiceImpl implements SchoolEventService {
	private final SchoolEventRepository schoolEventRepository;

	@Override
	public List<SchoolEventResponse> getEvents(long cursor, int count) {
		PageRequest pageRequest = PageRequest.of(0, count);
		Slice<SchoolEvent> schoolEvents;
		if (cursor == 0) {
			schoolEvents = schoolEventRepository.findAllOrderByDayInfoDesc(pageRequest);
		} else {
			schoolEvents = schoolEventRepository.findAllByCursorOrderByDayInfoDesc(cursor, pageRequest);
		}
		return schoolEvents.stream()
			.map(SchoolEventResponse::from)
			.toList();
	}
}
