package com.example.community_cr.mealMatch.match.controller.dto.response;

import com.example.community_cr.mealMatch.match.entity.Place;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {
	private long id;
	private String address_name;
	private String place_name;
	private double x;
	private double y;
	private long matchPostCount;

	public static PlaceResponse from(Place place) {
		return PlaceResponse.builder()
			.place_name(place.getName())
			.id(place.getId())
			.y(place.getLatitude())
			.x(place.getLongitude())
			.address_name(place.getAddress())
			.matchPostCount(place.getMatchPostList().size())
			.build();
	}
}
