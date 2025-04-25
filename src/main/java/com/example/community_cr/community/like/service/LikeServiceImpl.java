package com.example.community_cr.community.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.community.like.entity.Heart;
import com.example.community_cr.community.like.entity.HeartId;
import com.example.community_cr.community.like.repository.LikeRepository;
import com.example.community_cr.community.post.Repository.CommunityRepository;
import com.example.community_cr.community.post.entity.Post;
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

		Heart heart = likeRepository.save(Heart.of(user, post));

		post.addHeart(heart);
		communityRepository.save(post);
	}

	@Override
	public void unlikePost(long senderId, long postId) {
		if (!userRepository.existsById(senderId)) {
			throw new IllegalArgumentException("해당하는 사용자가 존재하지 않습니다.");
		}
		Post post = communityRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 게시글이 존재하지 않습니다."));

		HeartId heartId = HeartId.of(postId, senderId);
		Heart heart = likeRepository.findById(heartId)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시글은 좋아요 누른 상태가 아닙니다."));

		likeRepository.deleteById(heartId);

		post.removeHeart(heart);
		communityRepository.save(post);
	}
}
