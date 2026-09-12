package com.teamflow.backend.entity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {
	
	
	@Column(nullable = false, length = 255)
	private String email;
	
	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;
	
	@Column(name = "full_name", nullable = false, length = 120)
	private String fullName;
	
	@Column(name = "avatar_url", length = 500)
	private String avatarUrl;
	
	protected User() {
		
	}

	public User(String email, String passwordHash, String fullName) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.fullName = fullName;
	}
	
	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getFullName() {
		return fullName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	
}
