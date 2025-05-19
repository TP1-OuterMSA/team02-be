package com.example.community_cr.school_event.service;

import java.util.List;

import com.example.community_cr.school_event.controller.dto.response.SchoolEventResponse;

public interface SchoolEventService {
	List<SchoolEventResponse> getEvents(long cursor, int count);
}
