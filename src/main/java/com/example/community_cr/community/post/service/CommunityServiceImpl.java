package com.example.community_cr.community.post.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.community_cr.community.comment.controller.dto.response.CommentResponse;
import com.example.community_cr.community.comment_like.repository.CommentLikeRepository;
import com.example.community_cr.community.post.Repository.CommunityRepository;
import com.example.community_cr.community.post.controller.dto.request.PostRequest;
import com.example.community_cr.community.post.controller.dto.request.UpdatePostRequest;
import com.example.community_cr.community.post.controller.dto.response.PostDetailResponse;
import com.example.community_cr.community.post.controller.dto.response.PostResponse;
import com.example.community_cr.community.post.entity.Post;
import com.example.community_cr.community.post.entity.PostFilterType;
import com.example.community_cr.community.post_like.entity.HeartId;
import com.example.community_cr.community.post_like.repository.LikeRepository;
import com.example.community_cr.image.service.ImageService;
import com.example.community_cr.user.entity.User;
import com.example.community_cr.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {
	private final ImageService imageService;
	private final CommunityRepository communityRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final CommentLikeRepository commentLikeRepository;

	@Override
	public Optional<PostDetailResponse> createCommunityPost(long userId, PostRequest postRequest, MultipartFile image) {
		User user = userRepository.findById(userId)
			.orElseThrow(IllegalArgumentException::new);

		String imageFileName = imageService.upload(image);

		Post post = postRequest.toEntity(LocalDateTime.now(), imageFileName, user);
		communityRepository.save(post);

		return toOptionalDetailResponse(post, false, userId);
	}

	@Override
	public Optional<PostDetailResponse> updateCommunityPost(long userId, long postId,
		UpdatePostRequest updatePostRequest) {
		Post post = getPostForUserId(postId, userId);

		Post updatedPost = updatePostRequest.toEntity(LocalDateTime.now(), post);
		communityRepository.save(updatedPost);

		return toOptionalDetailResponse(updatedPost,
			likeRepository.existsById(HeartId.of(postId, userId)), userId);
	}

	@Override
	public Optional<PostDetailResponse> updateCommunityPost(long userId, long postId, MultipartFile image) {
		Post post = getPostForUserId(postId, userId);

		String imageFileName = imageService.upload(image);
		imageService.delete(post.getImageFileName());

		post.updateImageFileName(imageFileName, LocalDateTime.now());
		communityRepository.save(post);

		return toOptionalDetailResponse(post,
			likeRepository.existsById(HeartId.of(postId, userId)), userId);
	}

	@Override
	public Optional<PostDetailResponse> updateCommunityPost(long userId, long postId,
		UpdatePostRequest updatePostRequest, MultipartFile image) {
		Post post = getPostForUserId(postId, userId);

		String imageFileName = imageService.upload(image);
		imageService.delete(post.getImageFileName());

		Post updatedPost = updatePostRequest.toEntity(LocalDateTime.now(), post, imageFileName);

		communityRepository.save(updatedPost);

		return toOptionalDetailResponse(updatedPost,
			likeRepository.existsById(HeartId.of(postId, userId)), userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PostResponse> findAllCommunityPosts(long userId, long cursor, int count,
		PostFilterType postFilterType) {

		PageRequest pageRequest = PageRequest.of(0, count);
		Slice<Post> postList;
		postList = switch (postFilterType) {
			case ALL -> getCommunityPosts(userId, cursor, pageRequest);
			case LIKE -> getCommunityPostsByLikeCount(userId, cursor, pageRequest);
			case COMMENT -> getCommunityPostsByCommentCount(userId, cursor, pageRequest);
			case MY -> getCommunityPostsByUserId(userId, cursor, pageRequest);
		};

		assert postList != null;
		return postList.stream()
			.map(PostResponse::from)
			.peek(postResponse -> postResponse.setImageLink(
				imageService.generatePresignedUrl(postResponse.getImageLink())))
			.toList();
	}

	private Slice<Post> getCommunityPostsByUserId(long userId, long cursor, PageRequest pageRequest) {
		if (cursor == 0) {
			return communityRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageRequest);
		} else {
			return communityRepository.findNextPagePostsByUserId(userId, cursor, pageRequest);
		}
	}

	private Slice<Post> getCommunityPostsByCommentCount(long userId, long cursor, PageRequest pageRequest) {
		if (cursor == 0) {
			return communityRepository.findAllOrderByCommentCountDesc(userId, pageRequest);
		} else {
			long commentCount = communityRepository.getCommentCountById(cursor)
				.orElseThrow(() -> new IllegalArgumentException("해당하는 게시글이 존재하지 않습니다. 잘못된 커서가 전달되었습니다."));
			return communityRepository.findNextPageOrderByCommentCountDesc(userId, commentCount, cursor, pageRequest);
		}
	}

	private Slice<Post> getCommunityPostsByLikeCount(long userId, long cursor, PageRequest pageRequest) {
		if (cursor == 0) {
			return communityRepository.findAllOrderByHeartCountDesc(userId, pageRequest);
		} else {
			long heartCount = communityRepository.getLikeCountById(cursor)
				.orElseThrow(() -> new IllegalArgumentException("해당하는 게시글이 존재하지 않습니다. 잘못된 커서가 전달되었습니다."));
			return communityRepository.findNextPageOrderByHeartCountDesc(userId, heartCount, cursor, pageRequest);
		}
	}

	private Slice<Post> getCommunityPosts(long userId, long cursor, PageRequest pageRequest) {
		if (cursor == 0) {
			return communityRepository.findAllByOrderByCreatedAtDesc(userId, pageRequest);
		} else {
			return communityRepository.findNextPagePosts(userId, cursor, pageRequest);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PostDetailResponse> findCommunityPostById(long userId, long postId) {
		Post post = communityRepository.findById(postId)
			.orElseThrow(IllegalArgumentException::new);

		boolean postLikeStatus = likeRepository.existsById(HeartId.of(postId, userId));

		List<CommentResponse> commentResponses = post.getCommentList().stream()
			.map(comment -> {
				boolean liked = commentLikeRepository.existsByUserIdAndCommentId(userId, comment.getId());
				return CommentResponse.of(comment, liked);
			})
			.toList();

		String image = imageService.generatePresignedUrl(post.getImageFileName());
		PostDetailResponse response = PostDetailResponse.of(post, postLikeStatus, commentResponses, image);

		return Optional.of(response);
	}

	@Override
	public void deletePost(long userId, long postId) {
		Post post = getPostForUserId(postId, userId);
		String imageFileName = post.getImageFileName();

		communityRepository.delete(post);
		imageService.delete(imageFileName);
	}

	private Post getPostForUserId(long postId, long userId) {
		Post post = communityRepository.findById(postId)
			.orElseThrow(IllegalArgumentException::new);
		if (post.getUser().getId() != userId) {
			throw new IllegalArgumentException();
		}
		return post;
	}

	private Optional<PostDetailResponse> toOptionalDetailResponse(Post post, boolean likeStatus, long userId) {
		String imageUrl = imageService.generatePresignedUrl(post.getImageFileName());
		List<CommentResponse> commentResponses = post.getCommentList().stream()
			.map(comment -> {
				boolean liked = commentLikeRepository.existsByUserIdAndCommentId(userId, comment.getId());
				return CommentResponse.of(comment, liked);
			})
			.toList();
		PostDetailResponse response = PostDetailResponse.of(post, likeStatus, commentResponses, imageUrl);
		return Optional.of(response);
	}
}
