package com.teamflow.backend.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity{
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable =  false)
	private User user;
	
	@Column(name = "token_hash", nullable = false, length = 64)
	private String tokenHash;
	
	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;
	
	@Column(name = "revoked_at")
	private Instant revokedAt;
	
	protected RefreshToken() {
		
	}

	public RefreshToken(User user, String tokenHash, Instant expiresAt) {
		this.user = user;
		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
	}

	public User getUser() {
		return user;
	}


	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getRevokedAt() {
		return revokedAt;
	}
	
	public void revoke() {
		this.revokedAt = Instant.now();
	}
	
	public boolean isUsable() {
		return revokedAt == null && expiresAt.isAfter(Instant.now());
	}
	
}
