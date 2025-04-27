package com.example.community_cr.community.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.community_cr.community.comment.entity.Comment;
import com.example.community_cr.community.like.entity.Heart;
import com.example.community_cr.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String imageFileName;

	private String title;

	private String content;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder.Default
	private LocalDateTime updatedAt = LocalDateTime.now(); // ★ 추가

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Builder.Default
	private List<Heart> heartList = new ArrayList<>();
	private long heartCount;

	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Builder.Default
	private List<Comment> commentList = new ArrayList<>();
	private long commentCount;

	public void addHeart(Heart heart) {
		heartList.add(heart);
		heartCount = heartList.size();
	}

	public void removeHeart(Heart heart) {
		heartList.remove(heart);
		heartCount = heartList.size();
	}

	public void addComment(Comment comment) {
		commentList.add(comment);
		commentCount = commentList.size();
	}

	public void removeComment(Comment comment) {
		commentList.remove(comment);
		commentCount = commentList.size();
	}

	public void updateImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public void updateUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
