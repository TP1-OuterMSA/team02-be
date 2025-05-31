package com.example.community_cr.mealMatch.controller.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MatchPostRequest {
	private double latitude;

	private double longitude;

	@NotBlank(message = "주소를 입력해주세요.")
	private String address;

	@NotBlank(message = "위치 이름을 입력해주세요.")
	private String name;

	@FutureOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime schedule;

	@NotBlank(message = "내용을 입력해주세요.")
	private String content;

}
