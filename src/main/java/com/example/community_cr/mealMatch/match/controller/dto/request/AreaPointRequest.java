package com.example.community_cr.mealMatch.match.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AreaPointRequest {
	private double nwLongitude;
	private double nwLatitude;
	private double seLongitude;
	private double seLatitude;
}
