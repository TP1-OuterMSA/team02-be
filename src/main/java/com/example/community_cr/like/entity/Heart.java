package com.example.community_cr.like.entity;

import com.example.community_cr.community.entity.Post;
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
@IdClass(HeartId.class)
public class Heart {
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Heart Heart)) {
			return false;
		}
		if (Heart.getPost().getId() != this.post.getId()) {
			return false;
		}
		return Heart.getUser().getId() == this.user.getId();
	}
}
