package com.teamflow.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource(
			@Value("${app.cors.allowed-origins}") String allowedOrigins) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
		config.setAllowedMethods(List.of(
				"GET", "POST","PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of(
				"Authorization", "Content-Type", "X-Request-Id"));
		config.setExposedHeaders(List.of("X-Request-Id"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", config);
		
		return source;
	}
}
