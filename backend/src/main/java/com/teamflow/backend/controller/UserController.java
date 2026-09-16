package com.teamflow.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.dto.user.ChangePasswordRequest;
import com.teamflow.backend.dto.user.UpdateProfileRequest;
import com.teamflow.backend.dto.user.UserResponse;
import com.teamflow.backend.dto.user.UserSummary;
import com.teamflow.backend.security.AuthUser;
import com.teamflow.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User Profile")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@Operation(summary = "Information about currently logged-in user")
	@GetMapping("/me")
	public UserResponse me(@AuthenticationPrincipal AuthUser authUser) {
		return userService.getById(authUser.id());
		
	}
	
	@Operation(summary = "Get Information User by ID")
	@GetMapping("/{id}")
	public UserSummary findOne(@PathVariable Long id) {
		return userService.getSummary(id);
	}
	
	@Operation(summary = "Update profile")
	@PatchMapping("/me")
	public UserResponse updateMe(@AuthenticationPrincipal AuthUser authUser,
							@Valid @RequestBody UpdateProfileRequest request
			) {
		return userService.updateProfile(authUser.id(), request);
	}
	
	
	@Operation(summary = "Change Password")
	@PutMapping("/me/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@AuthenticationPrincipal AuthUser authUser,
			@Valid @RequestBody ChangePasswordRequest request) {
		userService.changePassword(authUser.id(), request);
	}
}
