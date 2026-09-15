package com.teamflow.backend.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final SecretKey key;
	private final long accessTokenMinutes;
	public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.access-token-minutes}") long accessTokenMinutes) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenMinutes = accessTokenMinutes;
	}
	public String generateAccessToken(Long userId, String email,String fullName) {
		Instant now = Instant.now();
		Instant expiry = now.plus(accessTokenMinutes,ChronoUnit.MINUTES);
		
		return Jwts.builder()
				.subject(String.valueOf(userId))
				.claim("email", email)
				.claim("fullNam", fullName)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiry))
				.signWith(key)
				.compact();
	}
	
	public Long extractUserId(String token) {
		Claims claims = parse(token);
		return Long.valueOf(claims.getSubject());
	}
	
	public String extractEmail(String token) {
		return parse(token).get("email", String.class);
	}
	
	public String extractFullName(String token) {
		return parse(token).get("fullName", String.class);
	}
	
	public boolean isValid(String token) {
		try {
			parse(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}
	
	public Long getAccessTokenMinutes() {
		return accessTokenMinutes;
	}
	
	private Claims parse(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
}
