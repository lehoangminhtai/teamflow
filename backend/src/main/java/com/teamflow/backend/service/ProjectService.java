package com.teamflow.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamflow.backend.dto.project.CreateProjectRequest;
import com.teamflow.backend.dto.project.ProjectResponse;
import com.teamflow.backend.dto.project.ProjectSummary;
import com.teamflow.backend.dto.project.UpdateProjectRequest;
import com.teamflow.backend.entity.Project;
import com.teamflow.backend.entity.ProjectMember;
import com.teamflow.backend.entity.ProjectRole;
import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.ConflictException;
import com.teamflow.backend.exception.NotFoundException;
import com.teamflow.backend.mapper.ProjectMapper;
import com.teamflow.backend.repository.ProjectMemberRepository;
import com.teamflow.backend.repository.ProjectRepository;
import com.teamflow.backend.repository.UserRepository;

@Service
public class ProjectService {
	private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
	
	private final ProjectRepository projectRepository;
	private final ProjectMemberRepository memberRepository;
	private final UserRepository userRepository;
	private final ProjectMapper projectMapper;
	private final ProjectAccessService accessService;

	public ProjectService(ProjectRepository projectRepository, ProjectMemberRepository memberRepository,
			UserRepository userRepository, ProjectMapper projectMapper, ProjectAccessService accessSerivce) {
		this.projectRepository = projectRepository;
		this.memberRepository = memberRepository;
		this.userRepository = userRepository;
		this.projectMapper = projectMapper;
		this.accessService = accessSerivce;
	}
	
	@Transactional
	public ProjectResponse create(Long currentUserId, CreateProjectRequest request) {
		User owner = userRepository.findById(currentUserId)
				.orElseThrow(() -> new NotFoundException("Not found user"));
		
		Project project = projectRepository.save(
				new Project(request.name(), request.description(), owner));
		
		memberRepository.save(new ProjectMember(project, owner, ProjectRole.OWNER));
		
		log.info("Project created: if={}", project.getId(), currentUserId);
		
		return projectMapper.toResponse(project, ProjectRole.OWNER, 1);
	}
	
	@Transactional(readOnly = true)
	public List<ProjectSummary> listMine(Long currentUserId, boolean archived){
		List<ProjectMember> memberships = 
				memberRepository.findMembershipsWithProject(currentUserId, archived);
		
		return memberships.stream()
				.map(m -> projectMapper.toSummary(
						m.getProject(), m.getRole(), memberRepository.countByProjectId(m.getProject().getId())))
				.toList();
	}
	
	@Transactional(readOnly = true)
	public ProjectResponse getById(Long currentUserId, Long projectId) {
		ProjectMember membership = accessService.requireMember(projectId, currentUserId);
		Project project = membership.getProject();
		
		return projectMapper.toResponse(project, membership.getRole(), memberRepository.countByProjectId(projectId));
	}
	
	@Transactional
	public ProjectResponse update (Long currentUserId, Long projectId, UpdateProjectRequest request) {
		ProjectMember membership = accessService.requireRole(projectId, currentUserId, ProjectRole.OWNER, ProjectRole.MANAGER);
		
		Project project = membership.getProject();
		if(project.isArchived()) {
			throw new ConflictException("Project archived, please Remove from archive before editing");
		}
		
		project.setName(request.name().trim());
		project.setDescription(request.description());
		
		projectRepository.save(project);
		
		log.info("Project updated: id = {}, byUserId = {}", projectId,currentUserId);
		
		return projectMapper.toResponse(project, membership.getRole(), memberRepository.countByProjectId(projectId));
	}
	
	@Transactional
	public void delete(Long currentUserId, Long projectId) {
		accessService.requireRole(projectId, currentUserId, ProjectRole.OWNER);
		
		projectRepository.deleteById(projectId);
		log.info("Project deleted: id = {}, byUserId = {}", projectId, currentUserId);
	}
	
	@Transactional
	public ProjectResponse setArchived(Long currentUserId, Long projectId, boolean archived) {
		ProjectMember membership = accessService.requireRole(projectId, currentUserId, ProjectRole.OWNER, ProjectRole.MANAGER);
		
		Project project = membership.getProject();
		project.setArchived(archived);
		projectRepository.save(project);
		
		return projectMapper.toResponse(project, membership.getRole(), 
				memberRepository.countByProjectId(projectId));
	}
	
}
