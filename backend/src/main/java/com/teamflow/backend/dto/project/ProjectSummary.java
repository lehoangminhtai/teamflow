package com.teamflow.backend.dto.project;

import java.time.Instant;

import com.teamflow.backend.entity.ProjectRole;

public record ProjectSummary(
		Long id,
		String name,
		String description,
		ProjectRole myRole,
		long memberCount,
		Instant createdAt) {}
