package com.example.community_cr.mealMatch.match.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.community_cr.mealMatch.match.controller.dto.request.MealPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.response.MatchOfferResponse;
import com.example.community_cr.mealMatch.match.controller.dto.response.MealPostResponse;

public interface MatchService {
	MealPostResponse saveMealPost(long userId, MealPostRequest mealPostRequest);

	void offerMealMate(long userId, long mealPostId, LocalDateTime startDateTime, LocalDateTime endDateTime);

	void replyMealMateOffer(long userId, long matchOfferId, boolean matchState);

	List<MatchOfferResponse> getMatchOffer(long userId, long cursor, int count);

	List<MatchOfferResponse> getMatchOffer(long userId, long mealPostId, long cursor, int count);
}
