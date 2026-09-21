package com.teamflow.backend.dto.project;

import java.time.Instant;

import com.teamflow.backend.dto.user.UserSummary;
import com.teamflow.backend.entity.ProjectRole;

public record ProjectResponse(
		Long id,
		String name,
		String description,
		UserSummary owner,
		ProjectRole myRole, 
		long memberCount,
		Instant createdAt,
		Instant updatedAt
		) {

}
