package com.example.community_cr.mealMatch.match.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.mealMatch.match.controller.dto.request.MealPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.request.UpdateMealPostRequest;
import com.example.community_cr.mealMatch.match.controller.dto.response.MatchOfferResponse;
import com.example.community_cr.mealMatch.match.controller.dto.response.MealPostResponse;
import com.example.community_cr.mealMatch.match.controller.dto.response.PlaceResponse;
import com.example.community_cr.mealMatch.match.entity.MatchOffer;
import com.example.community_cr.mealMatch.match.entity.MatchPost;
import com.example.community_cr.mealMatch.match.entity.MatchState;
import com.example.community_cr.mealMatch.match.entity.Place;
import com.example.community_cr.mealMatch.match.repository.MatchOfferRepository;
import com.example.community_cr.mealMatch.match.repository.MealPostRepository;
import com.example.community_cr.mealMatch.match.repository.PlaceRepository;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchServiceImpl implements MatchService {
	private final MatchOfferRepository matchOfferRepository;
	private final MealPostRepository mealPostRepository;
	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;

	@Override
	public MealPostResponse saveMealPost(long userId, MealPostRequest mealPostRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 존재하지 않습니다."));

		Place place = placeRepository.findByLongitudeAndLatitudeOrAddress(mealPostRequest.getLatitude(),
				mealPostRequest.getLongitude(), mealPostRequest.getAddress())
			.orElse(
				savePlace(mealPostRequest));

		MatchPost matchPost = com.example.community_cr.mealMatch.match.entity.MatchPost.of(mealPostRequest, place, user,
			LocalDateTime.now());
		matchPost = mealPostRepository.save(matchPost);
		return MealPostResponse.from(matchPost);
	}

	@Override
	public void offerMealMate(long userId, long mealPostId, LocalDateTime startSchedule, LocalDateTime endSchedule) {
		if (matchOfferRepository.existsByUserIdAndMatchPostId(userId, mealPostId)) {
			throw new IllegalArgumentException("이미 신청한 식사 매칭 글입니다.");
		}
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 존재하지 않습니다."));

		MatchPost matchPost = mealPostRepository.findById(mealPostId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 식사 매칭 글 정보가 존재하지 않습니다."));

		if (matchPost.getUser().getId() == userId) {
			throw new IllegalArgumentException("자신의 식사 매칭 글은 신청할 수 없습니다.");
		}

		MatchOffer matchOffer = MatchOffer.builder()
			.matchState(MatchState.WAITING)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.user(user)
			.startSchedule(startSchedule)
			.endSchedule(endSchedule)
			.matchPost(matchPost)
			.build();
		matchOfferRepository.save(matchOffer);
	}

	@Override
	public void replyMealMateOffer(long userId, long matchOfferId, boolean matchState) {
		MatchOffer matchOffer = matchOfferRepository.findById(matchOfferId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 식사 메이트 신청 정보가 존재하지 않습니다."));

		if (matchOffer.getMatchPost().getUser().getId() != userId) {
			throw new IllegalArgumentException("자신의 식사 매칭 글만 응답 여부를 정할 수 있습니다.");
		}

		if (!matchOffer.getMatchState().equals(MatchState.WAITING)) {
			throw new IllegalArgumentException("이미 수락 또는 거절한 식사 요청입니다.");
		}

		if (matchState) {
			matchOffer.updateMatchState(MatchState.ACCEPTED, LocalDateTime.now());
		} else {
			matchOffer.updateMatchState(MatchState.REJECTED, LocalDateTime.now());
		}
	}

	@Override
	public List<MatchOfferResponse> getMatchOffer(long userId, long cursor, int count) {
		PageRequest pageRequest = PageRequest.of(0, count);
		Slice<MatchOffer> matchOffers;
		if (cursor == 0) {
			matchOffers = matchOfferRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageRequest);
		} else {
			matchOffers = matchOfferRepository.findAllByUserIdNextPagePosts(userId, cursor, pageRequest);
		}

		return matchOffers.stream()
			.map(matchOffer -> MatchOfferResponse.of(matchOffer, MealPostResponse.from(matchOffer.getMatchPost())))
			.toList();
	}

	@Override
	public List<MatchOfferResponse> getMatchOffer(long userId, long mealPostId, long cursor, int count) {
		MatchPost matchPost = mealPostRepository.findById(mealPostId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 매칭 글 정보입니다."));

		if (matchPost.getUser().getId() != userId) {
			throw new IllegalArgumentException("자신이 작성한 매칭 글만 신청 정보를 볼 수 있습니다.");
		}

		PageRequest pageRequest = PageRequest.of(0, count);
		Slice<MatchOffer> matchOffers;
		if (cursor == 0) {
			matchOffers = matchOfferRepository.findAllByMealPostIdOrderByCreatedAtDesc(mealPostId, pageRequest);
		} else {
			matchOffers = matchOfferRepository.findAllByMealPostIdNextPagePosts(mealPostId, cursor, pageRequest);
		}

		return matchOffers.stream()
			.map(matchOffer -> MatchOfferResponse.of(matchOffer, MealPostResponse.from(matchOffer.getMatchPost())))
			.toList();
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

	@Override
	public List<MealPostResponse> getAllPosts(String address, long cursor, int count) {
		PageRequest pageRequest = PageRequest.of(0, count);
		List<Long> placeIds = placeRepository.findAllPlaceIdByAddress(address);
		Slice<MatchPost> mealPosts;
		if (cursor == 0) {
			mealPosts = mealPostRepository.findAllByPlaceIdInOrderByCreatedAtDesc(placeIds, pageRequest);
		} else {
			mealPosts = mealPostRepository.findNextPagePosts(placeIds, cursor, pageRequest);
		}
		return mealPosts.stream()
			.map(MealPostResponse::from)
			.collect(Collectors.toList());
	}

	@Override
	public MealPostResponse updatePost(Long postId, Long userId, UpdateMealPostRequest request) {
		MatchPost post = mealPostRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 매칭 글 정보입니다. "));

		if (!userId.equals(post.getUserId())) {
			throw new IllegalArgumentException("자신이 작성한 매칭 글만 신청 정보만 수정할 수 있습니다. ");
		}

		post.update(request);
		post = mealPostRepository.save(post);
		return MealPostResponse.from(post);
	}

	@Override
	public void deletePost(Long postId, Long userId) {
		MatchPost post = mealPostRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 매칭 글 정보입니다."));
		if (!userId.equals(post.getUserId())) {
			throw new IllegalArgumentException("삭제 권한이 없습니다.");
		}
		mealPostRepository.delete(post);
	}

	@Override
	public List<PlaceResponse> getPlaces(double nwLongitude, double nwLatitude, double seLongitude, double seLatitude) {
		List<Place> places = placeRepository.findAllByPointIn(nwLongitude, nwLatitude, seLongitude, seLatitude);
		return places.stream()
			.map(PlaceResponse::from)
			.toList();
	}
}
