package com.teamflow.backend.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamflow.backend.entity.Project;
import com.teamflow.backend.entity.ProjectMember;
import com.teamflow.backend.entity.ProjectRole;
import com.teamflow.backend.exception.ForbiddenException;
import com.teamflow.backend.exception.NotFoundException;
import com.teamflow.backend.repository.ProjectMemberRepository;

@Service
public class ProjectAccessService {
	private final ProjectMemberRepository memberRepository;

	public ProjectAccessService(ProjectMemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	@Transactional(readOnly = true)
	public ProjectMember requireMember(Long projectId, Long userId) {
		return memberRepository.findByProjectIdAndUserId(projectId, userId)
				.orElseThrow(()-> new NotFoundException("Not found project"));
	}
	
	@Transactional(readOnly = true)
	public ProjectMember requireRole(Long projectId, Long userId, ProjectRole... allowed) {
		ProjectMember membership = requireMember(projectId, userId);
		
		if(!Set.of(allowed).contains(membership.getRole())) {
			throw new ForbiddenException("You do not authorize to do this action");
		}
		
		return membership;
	}
	
	@Transactional(readOnly = true)
	public Project requireProjectAsMember(Long projectId, Long userId) {
		return requireMember(projectId, userId).getProject();
	}
}
