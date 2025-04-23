package com.example.community_cr.community.block.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.community_cr.community.block.entity.Block;
import com.example.community_cr.community.block.entity.BlockId;
import com.example.community_cr.community.block.repository.BlockRepository;
import com.example.community_cr.community.post.Repository.CommunityRepository;
import com.example.community_cr.community.post.controller.dto.response.PostResponse;
import com.example.community_cr.community.post.entity.Post;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockServiceImpl implements BlockService {
	private final BlockRepository blockRepository;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;

	@Override
	public void blockPost(long userId, long postId) {
		if (blockRepository.existsById(BlockId.of(postId, userId))) {
			throw new IllegalArgumentException("이미 차단한 게시글입니다.");
		}
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));
		Post post = communityRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("해당하는 게시글을 찾을 수 없습니다."));
		if (post.getUser().getId() == userId) {
			throw new IllegalArgumentException("자신의 게시글은 차단할 수 없습니다.");
		}
		blockRepository.save(Block.of(user, post));
	}

	@Override
	public Optional<PostResponse> unblockPost(long userId, long postId) {
		Block block = blockRepository.findById(BlockId.of(postId, userId))
			.orElseThrow(() -> new IllegalArgumentException("이미 차단하지 않은 게시글입니다."));
		blockRepository.delete(block);
		return Optional.of(
			PostResponse.from(block.getPost()));
	}
}
