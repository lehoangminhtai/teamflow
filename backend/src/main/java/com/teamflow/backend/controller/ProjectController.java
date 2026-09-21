package com.teamflow.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.dto.project.CreateProjectRequest;
import com.teamflow.backend.dto.project.ProjectResponse;
import com.teamflow.backend.dto.project.ProjectSummary;
import com.teamflow.backend.security.AuthUser;
import com.teamflow.backend.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project", description = "Manage Project")
public class ProjectController {
	
	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@Operation(summary = "Create new project")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProjectResponse create(@AuthenticationPrincipal AuthUser authUser,
			@Valid @RequestBody CreateProjectRequest request) {
		return projectService.create(authUser.id(), request);
	}
	
	@Operation(summary = "List of projects I have participated in")
	@GetMapping
	public List<ProjectSummary> listMine(@AuthenticationPrincipal AuthUser authUser,
			@RequestParam(defaultValue = "false") boolean archived){
		return projectService.listMine(authUser.id(), archived);
	}
}
