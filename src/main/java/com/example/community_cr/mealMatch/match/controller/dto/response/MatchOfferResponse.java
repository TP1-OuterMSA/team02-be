package com.example.community_cr.mealMatch.match.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.community_cr.mealMatch.match.entity.MatchOffer;
import com.example.community_cr.mealMatch.match.entity.MatchPost;
import com.example.community_cr.mealMatch.match.entity.MatchState;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchOfferResponse {
	private long id;
	private String name;
	private String address;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime schedule;

	private List<MatchOfferDto> matchList;

	public static MatchOfferResponse of(List<MatchOffer> matchOffers, MatchPost matchPost) {
		return MatchOfferResponse.builder()
			.id(matchPost.getPlace().getId())
			.name(matchPost.getPlace().getAddress())
			.schedule(matchPost.getSchedule())
			.matchList(matchOffers.stream()
				.map(MatchOfferDto::from)
				.toList())
			.build();
	}

	@Getter
	@Builder
	private static class MatchOfferDto {
		private long id;
		private String content;
		private MatchState matchState;

		public static MatchOfferDto from(MatchOffer matchOffer) {
			return MatchOfferDto.builder()
				.id(matchOffer.getId())
				.content(matchOffer.getContent())
				.matchState(matchOffer.getMatchState())
				.build();
		}
	}
}
