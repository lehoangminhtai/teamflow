package com.teamflow.backend.dto.auth;

import com.teamflow.backend.dto.user.UserResponse;

public record LoginResponse(
		String accessToken,
		long expiresIn,
		UserResponse user
		) {

}
