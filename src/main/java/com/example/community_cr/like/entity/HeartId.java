package com.example.community_cr.like.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HeartId {
	private long user;
	private long post;

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof HeartId heartId)) {
			return false;
		}
		if (heartId.getUser() != this.user) {
			return false;
		}
		return heartId.getPost() == this.post;
	}
}
