package com.example.community_cr.community.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.community_cr.community.controller.dto.request.PostRequest;
import com.example.community_cr.community.controller.dto.request.UpdatePostRequest;
import com.example.community_cr.community.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.controller.dto.response.PostResponse;

public interface CommunityService {
	Optional<PostDetailResponse> createCommunityPost(long userId, PostRequest postRequest, MultipartFile image);

	Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, UpdatePostRequest updatePostRequest);

	Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, MultipartFile image);

	Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, UpdatePostRequest updatePostRequest,
		MultipartFile image);

	List<PostResponse> findAllCommunityPosts(long cursor, int count);

	Optional<PostDetailResponse> findCommunityPostById(long id);

	void deletePost(long userId, long postId);
}
