package com.example.community_cr.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MJ Cafeteria Feedback API")
                        .description("명지대학교 학생 식당 피드백 API 명세서")
                        .version("1.0.0")
                );
    }
}
