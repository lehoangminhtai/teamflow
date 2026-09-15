package com.teamflow.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Email required")
    @Email(message = "Email not valid")
    @Size(max = 255, message = "Email max 255 characters")
    String email,

    @NotBlank(message = "Password required")
    @Size(
        min = 8,
        max = 72,
        message = "Password must be from 8 to 72 characters"
    )
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
        message = "The password must contain at least 1 letter and 1 digit"
    )
    String password,

    @NotBlank(message = "Fullname required")
    @Size(
        min = 2,
        max = 120,
        message = "Fullname must be from 2 to 120 characters"
    )
    String fullname
) {
}
