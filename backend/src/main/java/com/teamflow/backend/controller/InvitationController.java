package com.teamflow.backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.dto.project.ProjectSummary;
import com.teamflow.backend.security.AuthUser;
import com.teamflow.backend.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/invitations")
@Tag(name = "Invitation", description = "Invitation Project")
public class InvitationController {
	private final MemberService memberService;

	public InvitationController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@PostMapping("/{token}/accept")
	public ProjectSummary accept(
			@AuthenticationPrincipal AuthUser authUser,
			@PathVariable String token
			) {
		return memberService.accept(authUser.id(), token);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "revoke invitation")
	public void revoke(@AuthenticationPrincipal AuthUser authUser,
			@PathVariable Long id) {
		memberService.revoke(authUser.id(), id);
	}
}
