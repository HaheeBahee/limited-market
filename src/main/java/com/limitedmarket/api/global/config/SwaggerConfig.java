package com.limitedmarket.api.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // JWT 토큰 받기
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)      // HTTP 방식
                .scheme("bearer")                    // Bearer 토큰 방식
                .bearerFormat("JWT")                 // 토큰 형식은 JWT
                .in(SecurityScheme.In.HEADER)        // Header에 담아서 보냄
                .name("Authorization");              // 헤더 이름은 Authorization

        // 모든 API 에서 해당 인증 방식 적용
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        // Swagger UI
        return new OpenAPI()
                .info(new Info()
                        .title("limited-market API")
                        .description("한정 상품 선착순 주문 API")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme));
    }
}