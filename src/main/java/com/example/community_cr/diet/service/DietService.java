package com.example.community_cr.diet.service;

import java.util.List;
import java.util.Optional;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
import com.example.community_cr.diet.controller.dto.response.DietResponse;

public interface DietService {
	Optional<DietResponse> saveDiet(long userId, DietRequest dto);

	Optional<DietResponse> getDiet(long dietId);

	List<DietResponse> getDiets(long userId, long cursor, int count);
}
