package com.example.community_cr.diet.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.example.community_cr.diet.controller.dto.request.DietRequest;
import com.example.community_cr.diet.controller.dto.response.DietResponse;

public interface DietService {
	Optional<DietResponse> saveDiet(long userId, DietRequest dto);

	Optional<DietResponse> getDiet(long userId, long dietId);

	List<DietResponse> getDiets(long userId, LocalDate date);

	List<LocalDate> getDietDates(long userId, YearMonth month);
}
