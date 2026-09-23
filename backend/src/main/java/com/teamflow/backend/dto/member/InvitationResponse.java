package com.teamflow.backend.dto.member;

import java.time.Instant;

import com.teamflow.backend.entity.InvitationStatus;
import com.teamflow.backend.entity.ProjectRole;

public record InvitationResponse(
		Long id,
		String email,
		ProjectRole role,
		String token,
		Instant expiresAt,
		InvitationStatus status
		) {

}
