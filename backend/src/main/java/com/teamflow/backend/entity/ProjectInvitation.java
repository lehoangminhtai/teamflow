package com.teamflow.backend.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_invitations")
public class ProjectInvitation extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;
	
	@Column(nullable = false, length = 255)
	private String email;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProjectRole role;
	
	@Column(nullable = false, length = 64)
	private String token;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false,length = 20)
	private InvitationStatus status = InvitationStatus.PENDING;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "invited_by", nullable = false)
	private User invitedBy;
	
	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;
	
	protected ProjectInvitation() {
		
	}

	public ProjectInvitation(Project project, String email, ProjectRole role, String token, User invitedBy,
			Instant expiresAt) {
		this.project = project;
		this.email = email;
		this.role = role;
		this.token = token;
		this.invitedBy = invitedBy;
		this.expiresAt = expiresAt;
	}

	public Project getProject() {
		return project;
	}

	public String getEmail() {
		return email;
	}

	public ProjectRole getRole() {
		return role;
	}

	public String getToken() {
		return token;
	}

	public InvitationStatus getStatus() {
		return status;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}
	
	public boolean isPending() {
		return status == InvitationStatus.PENDING && expiresAt.isAfter(Instant.now());
	}
	
	public void accept() {
		this.status = InvitationStatus.ACCEPTED;
	}
	
	public void revoke() {
		this.status = InvitationStatus.REVOKED;
	}
}
