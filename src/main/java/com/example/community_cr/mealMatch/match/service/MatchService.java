package com.example.community_cr.mealMatch.match.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.community_cr.mealMatch.match.controller.dto.request.MatchPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.request.UpdateMatchPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.response.MatchOfferResponse;
import com.example.community_cr.mealMatch.match.controller.dto.response.MatchPostResponse;
import com.example.community_cr.mealMatch.match.controller.dto.response.PlaceResponse;

public interface MatchService {
	MatchPostResponse saveMatchPost(long userId, MatchPostRequest matchPostRequest);

	void offerMealMate(long userId, long matchPostId, LocalDateTime startSchedule, LocalDateTime endSchedule);

	void replyMealMateOffer(long userId, long matchOfferId, boolean matchState);

	List<MatchOfferResponse> getMatchOffer(long userId, long cursor, int count);

	List<MatchOfferResponse> getMatchOffer(long userId, long matchPostId, long cursor, int count);

	List<MatchPostResponse> getAllPosts(String address, long cursor, int count);

	MatchPostResponse updatePost(Long postId, Long userId, UpdateMatchPostRequest request);

	void deletePost(Long postId, Long userId);

	List<PlaceResponse> getPlaces(double nwLongitude, double nwLatitude, double seLongitude, double seLatitude);
}
