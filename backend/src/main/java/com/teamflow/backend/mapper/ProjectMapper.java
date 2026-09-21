package com.teamflow.backend.mapper;

import org.springframework.stereotype.Component;

import com.teamflow.backend.dto.project.ProjectResponse;
import com.teamflow.backend.dto.project.ProjectSummary;
import com.teamflow.backend.entity.Project;
import com.teamflow.backend.entity.ProjectRole;

@Component
public class ProjectMapper {
	private final UserMapper userMapper;

	public ProjectMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
	
	public ProjectSummary toSummary(Project project, ProjectRole myRole, long memberCount) {
		return new ProjectSummary(project.getId(), project.getName(), project.getDescription(), myRole, 
				memberCount, project.getCreatedAt());
	}
	
	public ProjectResponse toResponse(Project project, ProjectRole myRole, long memberCount) {
		return new ProjectResponse(
		project.getId(),
		project.getName(),
		project.getDescription(),
		userMapper.toSummary(project.getOwner()),
		myRole,
		memberCount,
		project.getCreatedAt(),
		project.getUpdatedAt());
		}
}
