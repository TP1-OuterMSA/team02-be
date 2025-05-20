package com.example.community_cr.mealMatch.match.controller.dto.response;

import com.example.community_cr.mealMatch.match.entity.Place;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {
	private String name;
	private Long id;
	private double latitude;
	private double longitude;
	private String address;
	private long matchPostCount;

	public static PlaceResponse from(Place place) {
		return PlaceResponse.builder()
			.name(place.getName())
			.id(place.getId())
			.latitude(place.getLatitude())
			.longitude(place.getLongitude())
			.address(place.getAddress())
			.matchPostCount(place.getMatchPostList().size())
			.build();
	}
}
