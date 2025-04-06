package com.example.community_cr.community.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.community_cr.community.controller.dto.request.PostRequest;
import com.example.community_cr.community.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.controller.dto.response.PostResponse;

public interface CommunityService {
	Optional<PostDetailResponse> createCommunityPost(long userId, PostRequest postRequest, MultipartFile image);

	List<PostResponse> findAllCommunityPosts(long cursor, int count);

	Optional<PostDetailResponse> findCommunityPostById(long id);
}
