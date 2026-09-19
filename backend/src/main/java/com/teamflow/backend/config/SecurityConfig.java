package com.teamflow.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.teamflow.backend.security.JwtAuthenticationFilter;
import com.teamflow.backend.security.SecurityErrorWriter;

@Configuration
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtFilter;
	private final SecurityErrorWriter errorWriter;
	private final CorsConfigurationSource corsConfigurationSource;
	
	public SecurityConfig(JwtAuthenticationFilter jwtFilter, SecurityErrorWriter errorWriter,
			CorsConfigurationSource corsConfigurationSource) {
		this.jwtFilter = jwtFilter;
		this.errorWriter = errorWriter;
		this.corsConfigurationSource = corsConfigurationSource;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
		.cors(cors -> cors.configurationSource(corsConfigurationSource))
		.csrf(csrf -> csrf.disable())
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/api/health", "/api/info").permitAll()
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
				.anyRequest().authenticated())
		.exceptionHandling(ex -> ex
				.authenticationEntryPoint(errorWriter)
				.accessDeniedHandler(errorWriter))
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
