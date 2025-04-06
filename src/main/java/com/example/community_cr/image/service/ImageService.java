package com.example.community_cr.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
	String upload(MultipartFile file);

	String generatePresignedUrl(String fileName);
}
