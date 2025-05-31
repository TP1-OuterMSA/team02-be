package com.example.community_cr.mealMatch.match.controller.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceListResponse {
	List<PlaceResponse> places;
}
