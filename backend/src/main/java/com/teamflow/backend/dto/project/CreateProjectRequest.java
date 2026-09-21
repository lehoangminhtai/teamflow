package com.teamflow.backend.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
		@NotBlank(message = "Project name can not be empty")
		@Size(max = 150, message = "Project name max 150 characters")
		String name,
		
		@Size(max = 5000, message = "Description project max 5000 characters")
		String description) {}
