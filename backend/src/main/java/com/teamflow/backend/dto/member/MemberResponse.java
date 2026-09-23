package com.teamflow.backend.dto.member;

import java.time.Instant;

import com.teamflow.backend.dto.user.UserSummary;
import com.teamflow.backend.entity.ProjectRole;

public record MemberResponse(
		UserSummary user,
		ProjectRole role,
		Instant joinedAt
		) {

}
