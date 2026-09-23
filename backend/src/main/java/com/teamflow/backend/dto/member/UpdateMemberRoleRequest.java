package com.teamflow.backend.dto.member;

import com.teamflow.backend.entity.ProjectRole;

import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
		@NotNull ProjectRole role
		) {

}
