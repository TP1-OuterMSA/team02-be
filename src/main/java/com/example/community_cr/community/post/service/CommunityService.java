package com.example.community_cr.community.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.community_cr.community.post.controller.dto.request.PostRequest;
import com.example.community_cr.community.post.controller.dto.request.UpdatePostRequest;
import com.example.community_cr.community.post.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.post.controller.dto.response.PostResponse;
import com.example.community_cr.community.post.entity.PostFilterType;

public interface CommunityService {
	Optional<PostDetailResponse> createCommunityPost(long userId, PostRequest postRequest, MultipartFile image);

	Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, UpdatePostRequest updatePostRequest);

	Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, MultipartFile image);

	Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, UpdatePostRequest updatePostRequest,
		MultipartFile image);

	List<PostResponse> findAllCommunityPosts(long userId, long cursor, int count, PostFilterType postFilterType);

	Optional<PostDetailResponse> findCommunityPostById(long userId, long postId);

	void deletePost(long userId, long postId);
}
