package com.example.community_cr.community.block.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BlockId {
	private long user;
	private long post;

	public static BlockId of(long postId, long userId) {
		return BlockId.builder()
			.post(postId)
			.user(userId)
			.build();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BlockId blockId)) {
			return false;
		}
		if (blockId.getUser() != this.user) {
			return false;
		}
		return blockId.getPost() == this.post;
	}
}
