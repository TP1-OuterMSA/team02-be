package com.example.community_cr.like.repository;

public interface LikeService {
	void likePost(long senderId, long postId);

	void unlikePost(long senderId, long postId);
}
