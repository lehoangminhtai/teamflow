package com.teamflow.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
		@NotBlank(message = "Current password can not be empty")
		String currentPassword,
		
		@NotBlank(message = "New password can not be empty")
		@Size(min = 8, max = 72, message = "New password must be from 8 to 72 characters")
		@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
				 message = "The password must contain at least 1 letter and 1 digit"
			    )
		String newPassword
		) {}
