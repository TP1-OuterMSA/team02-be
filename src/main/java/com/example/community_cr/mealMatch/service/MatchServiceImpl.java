package com.example.community_cr.mealMatch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.mealMatch.controller.dto.request.MatchPostRequest;
import com.example.community_cr.mealMatch.controller.dto.request.UpdateMatchPostRequest;
import com.example.community_cr.mealMatch.controller.dto.response.MatchOfferResponse;
import com.example.community_cr.mealMatch.controller.dto.response.MatchPostResponse;
import com.example.community_cr.mealMatch.controller.dto.response.PlaceResponse;
import com.example.community_cr.mealMatch.entity.MatchOffer;
import com.example.community_cr.mealMatch.entity.MatchOfferNotification;
import com.example.community_cr.mealMatch.entity.MatchPost;
import com.example.community_cr.mealMatch.entity.MatchState;
import com.example.community_cr.mealMatch.entity.Place;
import com.example.community_cr.mealMatch.repository.MatchOfferNotificationRepository;
import com.example.community_cr.mealMatch.repository.MatchOfferRepository;
import com.example.community_cr.mealMatch.repository.MatchPostRepository;
import com.example.community_cr.mealMatch.repository.PlaceRepository;
import com.example.community_cr.message.service.MessageService;
import com.example.community_cr.notification.component.NotificationEventPublisher;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MatchServiceImpl implements MatchService {
	private final MatchOfferRepository matchOfferRepository;
	private final MatchPostRepository matchPostRepository;
	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;
	private final MatchOfferNotificationRepository matchOfferNotificationRepository;

	private final MessageService messageService;
	private final NotificationEventPublisher eventPublisher;

	@Override
	public MatchPostResponse saveMatchPost(long userId, MatchPostRequest matchPostRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 존재하지 않습니다."));

		Place place = placeRepository.findByLongitudeAndLatitudeOrAddress(matchPostRequest.getLatitude(),
				matchPostRequest.getLongitude(), matchPostRequest.getAddress())
			.orElseGet(() -> savePlace(matchPostRequest));

		MatchPost matchPost = MatchPost.of(matchPostRequest, place, user, LocalDateTime.now());
		matchPost = matchPostRepository.save(matchPost);
		return MatchPostResponse.from(matchPost);
	}

	@Override
	public void offerMealMate(long userId, long matchPostId, String content) {
		if (matchOfferRepository.existsByUserIdAndMatchPostId(userId, matchPostId)) {
			throw new IllegalArgumentException("이미 신청한 식사 매칭 글입니다.");
		}
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자 정보가 존재하지 않습니다."));

		MatchPost matchPost = matchPostRepository.findById(matchPostId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 식사 매칭 글 정보가 존재하지 않습니다."));

		if (matchPost.getUser().getId() == userId) {
			throw new IllegalArgumentException("자신의 식사 매칭 글은 신청할 수 없습니다.");
		}

		MatchOffer matchOffer = MatchOffer.builder()
			.matchState(MatchState.WAITING)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.user(user)
			.content(content)
			.matchPost(matchPost)
			.build();
		matchOffer = matchOfferRepository.save(matchOffer);
		String message = "새로운 신청이 들어왔습니다.";
		String notificationId = matchPost.getUser().getId() + "_" + System.currentTimeMillis();
		MatchOfferNotification matchOfferNotification = MatchOfferNotification.of(notificationId,
			matchOffer.getMatchPost().getUser(), message, LocalDateTime.now(), matchOffer);
		matchOfferNotification = matchOfferNotificationRepository.save(matchOfferNotification);

		eventPublisher.publish(matchOfferNotification);
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

		String message;
		if (matchState) {
			matchOffer.updateMatchState(MatchState.ACCEPTED, LocalDateTime.now());
			MatchOffer finalMatchOffer = matchOffer;
			matchOffer.getMatchPost().getMatchOfferList().stream()
				.filter(otherMatchOffer -> !otherMatchOffer.equals(finalMatchOffer))
				.forEach(otherMatchOffer -> otherMatchOffer.updateMatchState(MatchState.REJECTED, LocalDateTime.now()));
			message = "요청이 승인되었습니다.";
		} else {
			matchOffer.updateMatchState(MatchState.REJECTED, LocalDateTime.now());
			message = "요청이 거절되었습니다.";
		}
		matchOffer = matchOfferRepository.save(matchOffer);

		String notificationId = matchOffer.getUser().getId() + "_" + System.currentTimeMillis();
		MatchOfferNotification matchOfferNotification = MatchOfferNotification.of(notificationId,
			matchOffer.getUser(), message, LocalDateTime.now(), matchOffer);
		matchOfferNotification = matchOfferNotificationRepository.save(matchOfferNotification);

		eventPublisher.publish(matchOfferNotification);

		if (matchState) {
			long otherUserId = matchOffer.getUser().getId();
			String messageContent = "매칭이 성사되었습니다. 자유롭게 대화를 나눠주세요!";
			messageService.createMessage(messageContent, otherUserId, userId);
			messageService.createMessage(messageContent, userId, otherUserId);
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

		Map<MatchPost, List<MatchOffer>> matchOffersByPlaceId = matchOffers.stream()
			.filter(matchOffer -> !(matchOffer.getMatchState().equals(MatchState.REJECTED)))
			.collect(Collectors.groupingBy(MatchOffer::getMatchPost));

		return matchOffersByPlaceId.entrySet().stream()
			.map(entry -> MatchOfferResponse.of(entry.getValue(), entry.getKey()))
			.toList();
	}

	@Override
	public List<MatchOfferResponse> getMatchOffer(long userId, long matchPostId, long cursor, int count) {
		MatchPost matchPost = matchPostRepository.findById(matchPostId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 매칭 글 정보입니다."));

		if (matchPost.getUser().getId() != userId) {
			throw new IllegalArgumentException("자신이 작성한 매칭 글만 신청 정보를 볼 수 있습니다.");
		}

		PageRequest pageRequest = PageRequest.of(0, count);
		Slice<MatchOffer> matchOffers;
		if (cursor == 0) {
			matchOffers = matchOfferRepository.findAllByMatchPostIdOrderByCreatedAtDesc(matchPostId, pageRequest);
		} else {
			matchOffers = matchOfferRepository.findAllByMatchPostIdNextPagePosts(matchPostId, cursor, pageRequest);
		}

		Map<MatchPost, List<MatchOffer>> matchOffersByPlaceId = matchOffers.stream()
			.filter(matchOffer -> !(matchOffer.getMatchState().equals(MatchState.REJECTED)))
			.collect(Collectors.groupingBy(MatchOffer::getMatchPost));

		return matchOffersByPlaceId.entrySet().stream()
			.map(entry -> MatchOfferResponse.of(entry.getValue(), entry.getKey()))
			.toList();
	}

	private Place savePlace(MatchPostRequest matchPostRequest) {
		Place place = Place.builder()
			.latitude(matchPostRequest.getLatitude())
			.longitude(matchPostRequest.getLongitude())
			.address(matchPostRequest.getAddress())
			.name(matchPostRequest.getName())
			.build();
		return placeRepository.save(place);
	}

	@Override
	public List<MatchPostResponse> getAllPosts(String address, long cursor, int count) {
		PageRequest pageRequest = PageRequest.of(0, count);
		List<Long> placeIds = placeRepository.findAllPlaceIdByAddress(address);
		Slice<MatchPost> matchPosts;
		if (cursor == 0) {
			matchPosts = matchPostRepository.findAllByPlaceIdInOrderByCreatedAtDesc(placeIds, pageRequest);
		} else {
			matchPosts = matchPostRepository.findNextPagePosts(placeIds, cursor, pageRequest);
		}
		return matchPosts.stream()
			.map(MatchPostResponse::from)
			.collect(Collectors.toList());
	}

	@Override
	public MatchPostResponse updatePost(Long postId, Long userId, UpdateMatchPostRequest request) {
		MatchPost post = matchPostRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 매칭 글 정보입니다. "));

		if (!userId.equals(post.getUserId())) {
			throw new IllegalArgumentException("자신이 작성한 매칭 글만 신청 정보만 수정할 수 있습니다. ");
		}

		post.update(request);
		post = matchPostRepository.save(post);
		return MatchPostResponse.from(post);
	}

	@Override
	public void deletePost(Long postId, Long userId) {
		MatchPost post = matchPostRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식사 매칭 글 정보입니다."));
		if (!userId.equals(post.getUserId())) {
			throw new IllegalArgumentException("삭제 권한이 없습니다.");
		}
		matchPostRepository.delete(post);
	}

	@Override
	public List<PlaceResponse> getPlaces(double nwLongitude, double nwLatitude, double seLongitude, double seLatitude) {
		List<Place> places = placeRepository.findAllByPointIn(nwLongitude, nwLatitude, seLongitude, seLatitude);
		return places.stream()
			.map(PlaceResponse::from)
			.toList();
	}
}
