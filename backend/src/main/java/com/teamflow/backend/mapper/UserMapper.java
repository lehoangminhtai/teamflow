package com.teamflow.backend.mapper;

import org.springframework.stereotype.Component;

import com.teamflow.backend.dto.user.UserResponse;
import com.teamflow.backend.dto.user.UserSummary;
import com.teamflow.backend.entity.User;

@Component
public class UserMapper {
	public UserResponse toResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getEmail(),
				user.getFullName(),
				user.getAvatarUrl(),
				user.getCreatedAt()
				);
	}
	
	public UserSummary toSummary(User user) {
		return new UserSummary(user.getId(),user.getFullName(), user.getAvatarUrl());
	}
}
