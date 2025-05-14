package com.example.community_cr.mate.matching.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.mate.matching.controller.dto.request.MealPostRequest;
import com.example.community_cr.mate.matching.controller.dto.response.MealPostResponse;
import com.example.community_cr.mate.matching.entity.MealPost;
import com.example.community_cr.mate.matching.entity.Place;
import com.example.community_cr.mate.matching.repository.MatchRepository;
import com.example.community_cr.mate.matching.repository.MealPostRepository;
import com.example.community_cr.mate.matching.repository.PlaceRepository;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchServiceImpl implements MatchService {
	private final MatchRepository matchRepository;
	private final MealPostRepository mealPostRepository;
	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;

	@Override
	public MealPostResponse saveMealPost(long userId, MealPostRequest mealPostRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 존재하지 않습니다."));

		Place place = placeRepository.findByLongitudeAndLatitudeOrAddressOrName(mealPostRequest.getLatitude(),
				mealPostRequest.getLongitude(), mealPostRequest.getAddress(), mealPostRequest.getName())
			.orElse(
				savePlace(mealPostRequest));

		MealPost mealPost = MealPost.of(mealPostRequest, place, user, LocalDateTime.now());
		mealPost = mealPostRepository.save(mealPost);
		return MealPostResponse.from(mealPost);
	}

	private Place savePlace(MealPostRequest mealPostRequest) {
		Place place = Place.builder()
			.latitude(mealPostRequest.getLatitude())
			.longitude(mealPostRequest.getLongitude())
			.address(mealPostRequest.getAddress())
			.name(mealPostRequest.getName())
			.build();
		return placeRepository.save(place);
	}
}
