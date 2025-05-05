package com.example.community_cr.community.post_block.service;

import java.util.Optional;

import com.example.community_cr.community.post.controller.dto.response.PostResponse;

public interface BlockService {
	void blockPost(long userId, long postId);

	Optional<PostResponse> unblockPost(long userId, long postId);
}
