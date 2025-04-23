package com.example.community_cr.community.block.entity;

import com.example.community_cr.community.post.entity.Post;
import com.example.community_cr.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(BlockId.class)
public class Block {
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	public static Block of(User user, Post post) {
		return Block.builder()
			.user(user)
			.post(post)
			.build();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Block block)) {
			return false;
		}
		if (block.getPost().getId() != this.post.getId()) {
			return false;
		}
		return block.getUser().getId() == this.user.getId();
	}
}
