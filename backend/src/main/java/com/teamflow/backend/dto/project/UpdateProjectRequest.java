package com.teamflow.backend.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
		@NotBlank(message = "Project name cannot be empty")
		@Size(max = 150, message = "Project name: maximum 150 characters")
		String name,
		
		@Size(max = 5000, message = "Description: maximum 5000 characters")
		String description
		) {

}
