package com.example.community_cr.community.post_like.service;

public interface LikeService {
	void likePost(long senderId, long postId);

	void unlikePost(long senderId, long postId);
}
