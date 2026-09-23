package com.teamflow.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.dto.common.PageResponse;
import com.teamflow.backend.dto.member.InvitationResponse;
import com.teamflow.backend.dto.member.InviteMemberRequest;
import com.teamflow.backend.dto.member.MemberResponse;
import com.teamflow.backend.dto.member.UpdateMemberRoleRequest;
import com.teamflow.backend.dto.project.CreateProjectRequest;
import com.teamflow.backend.dto.project.ProjectResponse;
import com.teamflow.backend.dto.project.ProjectSummary;
import com.teamflow.backend.dto.project.UpdateProjectRequest;
import com.teamflow.backend.entity.ProjectInvitation;
import com.teamflow.backend.security.AuthUser;
import com.teamflow.backend.service.MemberService;
import com.teamflow.backend.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project", description = "Manage Project")
public class ProjectController {
	
	private final ProjectService projectService;
	private final MemberService memberService;

	
	
	public ProjectController(ProjectService projectService, MemberService memberService) {
		this.projectService = projectService;
		this.memberService = memberService;
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
	public PageResponse<ProjectSummary> listMine(
			@AuthenticationPrincipal AuthUser authUser,
			@RequestParam(defaultValue = "false") boolean archived,
			@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) String sort
			){
		return projectService.listMine(authUser.id(), archived, q, page, size, sort);
	}
	
	@Operation(summary = "Project detail")
	@GetMapping("/{id}")
	public ProjectResponse getOne(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id) {
		return projectService.getById(authUser.id(), id);
	}
	
	@Operation(summary = "Update Project")
	@PutMapping("/{id}")
	public ProjectResponse update(@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id,
			@Valid @RequestBody UpdateProjectRequest request) {
		return projectService.update(authUser.id(), id, request);
	}
	
	@Operation(summary = "Delete project")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long id) {
		projectService.delete(authUser.id(), id);
	}
	
	
	@PostMapping("/{id}/archive")
	public ProjectResponse archive(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long id) {
		return projectService.setArchived(authUser.id(), id, true);
	}
	
	@PostMapping("/{id}/unarchive")
	public ProjectResponse unarchive(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long id) {
		return projectService.setArchived(authUser.id(), id, false);
	}
	
	@PostMapping("/{id}/members")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Invite to join project")
	public InvitationResponse invite(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id,
			@Valid @RequestBody InviteMemberRequest request
			) {
		return memberService.invite(authUser.id(), id, request);
	}
	
	@GetMapping("/{id}/members")
	@Operation(summary = "List member in project")
	public List<MemberResponse> listMembers(@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id){
		return memberService.list(authUser.id(), id);
	}
	
	@GetMapping("/{id}/invitations")
	@Operation(summary = "Get list invitation pending of me")
	public List<InvitationResponse> listPending(@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id){
		return memberService.listPending(authUser.id(), id);
	}
	
	@PatchMapping("/{id}/members/{userId}")
	@Operation(summary = "Update role member in project")
	public MemberResponse updateRole(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id,
			@PathVariable Long userId,
			@Valid @RequestBody UpdateMemberRoleRequest request
			) {
		return memberService.updateRole(authUser.id(), id, userId, request.role());
	}
	
	@DeleteMapping("/{id}/members/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeMember(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id,
			@PathVariable Long userId
			) {
		memberService.remove(authUser.id(), id, userId);
	}
}
