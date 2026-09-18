package com.teamflow.backend.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.dto.auth.LoginRequest;
import com.teamflow.backend.dto.auth.LoginResponse;
import com.teamflow.backend.dto.auth.RegisterRequest;
import com.teamflow.backend.dto.user.UserResponse;
import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.UnauthorizedException;
import com.teamflow.backend.security.JwtService;
import com.teamflow.backend.security.RefreshTokenService;
import com.teamflow.backend.service.AuthService;
import com.teamflow.backend.service.AuthService.RefreshResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Register, Login, Manage Session")
public class AuthController {
	private final AuthService authService;
	private  final JwtService jwtService;
	private static final String COOKIE_NAME = "refresh_token";
	private final RefreshTokenService refreshTokenService;
	private final boolean cookieSecure;

	public AuthController(AuthService authService, JwtService jwtService, RefreshTokenService refreshTokenService,
			@Value("${app.cookie.secure}") boolean cookieSecure
			) {
		this.authService = authService;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.cookieSecure = cookieSecure;
	}

	@Operation(summary = "Create new user")
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}
	
	@Operation(summary = "Login and receive access token")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse body = authService.login(request);
		User user = authService.requireUserByEmail(request.email());
		
		String refreshToken = refreshTokenService.issue(user);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, buildCookie(refreshToken,
						Duration.ofDays(refreshTokenService.getRefreshDays())).toString())
				.body(body);
	}
	
	@Operation(summary = "Obtain a new access token using the refresh token")
	@PostMapping("/refresh")
	public RefreshResponse refresh(
			@CookieValue(name = COOKIE_NAME, required = false) String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new UnauthorizedException("TOKEN_EXPIRED", "Session login expired, login again please");
		}
		
		User user = refreshTokenService.verify(refreshToken);
		
		String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getFullName());
		return new RefreshResponse(accessToken, jwtService.getAccessTokenMinutes()*60);
	}
	
	@Operation(summary = "Logout and revoke sesstion")
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
		@CookieValue(name = COOKIE_NAME, required = false) String refreshToken){
		if (refreshToken!= null && !refreshToken.isBlank()) {
			refreshTokenService.revoke(refreshToken);
		}
		
		return ResponseEntity.noContent()
				.header(HttpHeaders.SET_COOKIE, buildCookie("", Duration.ZERO).toString())
				.build();
	}

	private ResponseCookie buildCookie(String value, Duration maxAge) {
		return ResponseCookie.from(COOKIE_NAME,value)
				.httpOnly(true)
				.secure(cookieSecure)
				.sameSite("Lax")
				.path("/api/auth")
				.maxAge(maxAge)
				.build();
	}
	
	
}
