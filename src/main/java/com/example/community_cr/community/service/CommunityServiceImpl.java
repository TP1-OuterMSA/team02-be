package com.example.community_cr.community.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.community_cr.community.Repository.CommunityRepository;
import com.example.community_cr.community.controller.dto.request.PostRequest;
import com.example.community_cr.community.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.controller.dto.response.PostResponse;
import com.example.community_cr.community.entity.Post;
import com.example.community_cr.image.service.ImageService;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {
	private final ImageService imageService;
	private final CommunityRepository communityRepository;
	private final UserRepository userRepository;

	@Override
	public Optional<PostDetailResponse> createCommunityPost(long userId, PostRequest postRequest, MultipartFile image) {
		User user = userRepository.findById(userId)
			.orElseThrow(IllegalArgumentException::new);

		String imageFileName = imageService.upload(image);

		Post post = postRequest.toEntity(LocalDateTime.now(), imageFileName, user);
		communityRepository.save(post);

		return toOptionalDetailResponse(post);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PostResponse> findAllCommunityPosts(long cursor, int count) {
		PageRequest pageRequest = PageRequest.of(0, count);
		Slice<Post> postList;
		if (cursor == 0) {
			postList = communityRepository.findTopByOrderByCreatedAtDesc(pageRequest);
		} else {
			postList = communityRepository.findNextPagePosts(cursor, pageRequest);
		}
		return postList.stream()
			.map(PostResponse::from)
			.peek(postResponse -> postResponse.setImageLink(
				imageService.generatePresignedUrl(postResponse.getImageLink())))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PostDetailResponse> findCommunityPostById(long id) {
		Post post = communityRepository.findById(id)
			.orElseThrow(IllegalArgumentException::new);

		return toOptionalDetailResponse(post);
	}

	private Optional<PostDetailResponse> toOptionalDetailResponse(Post post) {
		PostDetailResponse response = PostDetailResponse.from(post);
		response.setImage(
			imageService.generatePresignedUrl(response.getImage()));
		return Optional.of(response);
	}
}
