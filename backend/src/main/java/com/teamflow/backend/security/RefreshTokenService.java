package com.teamflow.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamflow.backend.entity.RefreshToken;
import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.UnauthorizedException;
import com.teamflow.backend.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	private static final SecureRandom RANDOM = new SecureRandom();
	
	private final RefreshTokenRepository repository;
	private final long refreshDays;
	public RefreshTokenService(RefreshTokenRepository repository,
			@Value("${app.jwt.refresh-token-days}") long refreshDays) {
		this.repository = repository;
		this.refreshDays = refreshDays;
	}
	
	@Transactional
	public String issue(User user) {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		
		String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		
		Instant expiresAt = Instant.now().plus(refreshDays,ChronoUnit.DAYS);
		repository.save(new RefreshToken(user, sha256(raw), expiresAt));
		return raw;
	}
	
	@Transactional(readOnly = true)
	public User verify(String rawToken) {
		RefreshToken stored = repository.findByTokenHash(sha256(rawToken))
				.orElseThrow(() -> expired());
	
		if(!stored.isUsable()) {
			throw expired();
		}
		return stored.getUser();
	}
	
	@Transactional
	public void revoke(String rawToken) {
		repository.findByTokenHash(sha256(rawToken))
			.ifPresent(token -> {
				token.revoke();
				repository.save(token);
			});
	}
	
	@Transactional
	public void revokeAllForUser(Long userId) {
		List<RefreshToken> tokens = repository.findByUserIdAndRevokedAtIsNull(userId);
		tokens.forEach(RefreshToken::revoke);
		repository.saveAll(tokens);
	}
	
	public long getRefreshDays() {
		return refreshDays;
	}

	private UnauthorizedException expired() {
			return new UnauthorizedException("TOKEN_EXPIRED", 
					"Session login expired, login again please");
					
	}

	private String sha256(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashed);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 not available", ex);
		}
	}
}
