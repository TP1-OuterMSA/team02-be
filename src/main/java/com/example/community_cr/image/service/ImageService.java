package com.example.community_cr.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
	String upload(MultipartFile file);

	void delete(String fileName);

	String generatePresignedUrl(String fileName);
}
