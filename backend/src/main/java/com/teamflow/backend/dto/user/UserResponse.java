package com.teamflow.backend.dto.user;

import java.time.Instant;

public record UserResponse(
		Long id,
		String email,
		String fullName,
		String avatarUrl,
		Instant createdAt
		) {
}
