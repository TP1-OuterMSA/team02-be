package com.example.community_cr.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
	@Bean
	public Executor taskExecutor() {
		return Executors.newFixedThreadPool(10); // 병렬 요청 수 조절
	}
}

