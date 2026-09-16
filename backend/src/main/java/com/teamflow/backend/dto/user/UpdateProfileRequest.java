package com.teamflow.backend.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
		@Size(min = 2, max = 120, message = "Fullname must be from 2 to 120 characters")
		String fullName,

		@Size(max = 500, message = "Url max 500 characters") 
		String avatarUrl
		) {}
