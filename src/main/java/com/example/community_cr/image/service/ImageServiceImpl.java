package com.example.community_cr.image.service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final AmazonS3Client s3Client;

	@Value("${s3.bucket}")
	private String bucket;

	@Override
	public String upload(MultipartFile file) {
		try {
			String contentType = file.getContentType();
			if (contentType == null || !contentType.startsWith("image/")) {
				throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
			}
			String fileName = createFileName();
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			metadata.setContentLength(file.getSize());
			s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
			return fileName;
		} catch (IOException e) {
			throw new IllegalStateException("[Error] AWS S3 서비스 접근에 실패했습니다.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public String generatePresignedUrl(String fileName) {
		Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10); // 10분 유효
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName);
		request.setExpiration(expiration);
		URL url = s3Client.generatePresignedUrl(request);
		return url.toString();
	}

	private String createFileName() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		return UUID.randomUUID() + "_" + LocalDateTime.now().format(formatter);
	}
}
