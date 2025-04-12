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
import com.example.community_cr.community.controller.dto.request.UpdatePostRequest;
import com.example.community_cr.community.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.controller.dto.response.PostResponse;
import com.example.community_cr.community.entity.Post;
import com.example.community_cr.image.service.ImageService;
import com.example.community_cr.like.entity.HeartId;
import com.example.community_cr.like.repository.LikeRepository;
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
	private final LikeRepository likeRepository;

	@Override
	public Optional<PostDetailResponse> createCommunityPost(long userId, PostRequest postRequest, MultipartFile image) {
		User user = userRepository.findById(userId)
			.orElseThrow(IllegalArgumentException::new);

		String imageFileName = imageService.upload(image);

		Post post = postRequest.toEntity(LocalDateTime.now(), imageFileName, user);
		communityRepository.save(post);

		return toOptionalDetailResponse(post, false);
	}

	@Override
	public Optional<PostDetailResponse> updateCommunityPost(long userId, long postId,
		UpdatePostRequest updatePostRequest) {
		Post post = getPostForUserId(postId, userId);

		Post updatedPost = updatePostRequest.toEntity(LocalDateTime.now(), post);
		communityRepository.save(updatedPost);

		return toOptionalDetailResponse(updatedPost,
			likeRepository.existsById(HeartId.of(postId, userId)));
	}

	@Override
	public Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, MultipartFile image) {
		Post post = getPostForUserId(postId, userId);

		String imageFileName = imageService.upload(image);
		imageService.delete(post.getImageFileName());

		post.updateImageFileName(imageFileName);
		communityRepository.save(post);

		return toOptionalDetailResponse(post,
			likeRepository.existsById(HeartId.of(postId, userId)));
	}

	@Override
	public Optional<PostDetailResponse> updateCommunityPost(long userId, long postId,
		UpdatePostRequest updatePostRequest, MultipartFile image) {
		Post post = getPostForUserId(postId, userId);

		String imageFileName = imageService.upload(image);
		imageService.delete(post.getImageFileName());

		Post updatedPost = updatePostRequest.toEntity(LocalDateTime.now(), post, imageFileName);
		communityRepository.save(updatedPost);

		return toOptionalDetailResponse(updatedPost,
			likeRepository.existsById(HeartId.of(postId, userId)));
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
	public Optional<PostDetailResponse> findCommunityPostById(long userId, long postId) {
		Post post = communityRepository.findById(postId)
			.orElseThrow(IllegalArgumentException::new);
		boolean likeStatus = likeRepository.existsById(HeartId.of(postId, userId));
		return toOptionalDetailResponse(post, likeStatus);
	}

	@Override
	public void deletePost(long userId, long postId) {
		Post post = getPostForUserId(postId, userId);
		String imageFileName = post.getImageFileName();

		communityRepository.delete(post);
		imageService.delete(imageFileName);
	}

	private Post getPostForUserId(long postId, long userId) {
		Post post = communityRepository.findById(postId)
			.orElseThrow(IllegalArgumentException::new);
		if (post.getUser().getId() != userId) {
			throw new IllegalArgumentException();
		}
		return post;
	}

	private Optional<PostDetailResponse> toOptionalDetailResponse(Post post, boolean likeStatus) {
		PostDetailResponse response = PostDetailResponse.from(post, likeStatus);
		response.setImage(
			imageService.generatePresignedUrl(response.getImage()));
		return Optional.of(response);
	}
}
