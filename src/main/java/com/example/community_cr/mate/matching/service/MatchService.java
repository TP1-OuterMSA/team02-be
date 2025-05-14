package com.example.community_cr.mate.matching.service;

import com.example.community_cr.mate.matching.controller.dto.request.MealPostRequest;
import com.example.community_cr.mate.matching.controller.dto.response.MealPostResponse;

public interface MatchService {
	MealPostResponse saveMealPost(long userId, MealPostRequest mealPostRequest);
}
