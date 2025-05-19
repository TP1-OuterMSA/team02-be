package com.example.community_cr.school_event.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.community_cr.school_event.controller.dto.response.SchoolEventResponse;
import com.example.community_cr.school_event.service.SchoolEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team2/schoolEvent")
public class SchoolEventController {
	private final SchoolEventService schoolEventService;

	@GetMapping("/getEvents")
	public ResponseEntity<List<SchoolEventResponse>> getEvents(
		@RequestParam(value = "cursor", required = false, defaultValue = "0") long cursor,
		@RequestParam(value = "count", required = false, defaultValue = "3") int count
	) {
		return ResponseEntity.ok(schoolEventService.getEvents(cursor, count));
	}
}
