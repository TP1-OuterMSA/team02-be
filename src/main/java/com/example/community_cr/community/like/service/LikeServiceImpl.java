package com.example.community_cr.community.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.community.like.entity.Heart;
import com.example.community_cr.community.post.Repository.CommunityRepository;
import com.example.community_cr.community.post.entity.Post;
import com.example.community_cr.community.like.entity.HeartId;
import com.example.community_cr.community.like.repository.LikeRepository;
import com.example.community_cr.community.like.repository.LikeService;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {
	private final LikeRepository likeRepository;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;

	@Override
	public void likePost(long senderId, long postId) {
		if (likeRepository.existsById(HeartId.of(postId, senderId))) {
			throw new IllegalArgumentException();
		}
		User user = userRepository.findById(senderId)
			.orElseThrow(IllegalArgumentException::new);
		Post post = communityRepository.findById(postId)
			.orElseThrow(IllegalArgumentException::new);
		likeRepository.save(Heart.of(user, post));
	}

	@Override
	public void unlikePost(long senderId, long postId) {
		HeartId heartId = HeartId.of(postId, senderId);
		if (!likeRepository.existsById(heartId)) {
			throw new IllegalArgumentException();
		}
		likeRepository.deleteById(heartId);
	}
}
