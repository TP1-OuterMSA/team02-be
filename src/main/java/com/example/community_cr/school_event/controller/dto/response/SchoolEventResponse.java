package com.example.community_cr.school_event.controller.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.community_cr.school_event.entity.SchoolEvent;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchoolEventResponse {
	private long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate date;

	private String title;

	public static SchoolEventResponse from(SchoolEvent schoolEvent) {
		return SchoolEventResponse.builder()
			.id(schoolEvent.getId())
			.title(schoolEvent.getTitle())
			.date(LocalDate.parse(
				schoolEvent.getDayInfo(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.build();
	}
}
