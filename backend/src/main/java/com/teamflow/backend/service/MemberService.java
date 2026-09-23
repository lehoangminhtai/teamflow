package com.teamflow.backend.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamflow.backend.dto.member.InvitationResponse;
import com.teamflow.backend.dto.member.InviteMemberRequest;
import com.teamflow.backend.dto.member.MemberResponse;
import com.teamflow.backend.dto.project.ProjectSummary;
import com.teamflow.backend.entity.InvitationStatus;
import com.teamflow.backend.entity.ProjectInvitation;
import com.teamflow.backend.entity.ProjectMember;
import com.teamflow.backend.entity.ProjectRole;
import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.ConflictException;
import com.teamflow.backend.exception.ForbiddenException;
import com.teamflow.backend.exception.NotFoundException;
import com.teamflow.backend.mapper.ProjectMapper;
import com.teamflow.backend.mapper.UserMapper;
import com.teamflow.backend.repository.ProjectInvitationRepository;
import com.teamflow.backend.repository.ProjectMemberRepository;
import com.teamflow.backend.repository.TaskRepository;
import com.teamflow.backend.repository.UserRepository;

@Service
public class MemberService {
	private static final Logger log = LoggerFactory.getLogger(MemberService.class);
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int INVITATION_DAYS = 7;
	
	private final ProjectMemberRepository memberRepository;
	private final ProjectInvitationRepository invitationRepository;
	private final UserRepository userRepository;
	private final ProjectAccessService accessService;
	private final UserMapper userMapper;
	private final ProjectMapper projectMapper;
	private final TaskRepository taskRepository;
	
	public MemberService(ProjectMemberRepository memberRepository, ProjectInvitationRepository invitationRepository,
			UserRepository userRepository, ProjectAccessService accessService, UserMapper userMapper,
			ProjectMapper projectMapper, TaskRepository taskRepository) {
		this.memberRepository = memberRepository;
		this.invitationRepository = invitationRepository;
		this.userRepository = userRepository;
		this.accessService = accessService;
		this.userMapper = userMapper;
		this.projectMapper = projectMapper;
		this.taskRepository = taskRepository;
	}
	
	@Transactional(readOnly = true)
	public List<MemberResponse> list(Long currentUserId, Long projectId){
		accessService.requireMember(projectId, currentUserId);
		
		return memberRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
				.stream()
				.map(m ->new MemberResponse(
						userMapper.toSummary(m.getUser()),
						m.getRole(),
						m.getCreatedAt()
						))
				.toList();
	}
	
	@Transactional
	public InvitationResponse invite(Long currentUserId, Long projectId, InviteMemberRequest request) {
		ProjectMember inviterMembership = accessService.requireRole(projectId, currentUserId, 
				ProjectRole.OWNER, ProjectRole.MANAGER);
		String email = request.email().toLowerCase();
		
		userRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
			if(memberRepository.existsByProjectIdAndUserId(projectId, existing.getId())) {
				throw new ConflictException("This user joined the project");
			}
		});
		if (invitationRepository.existsByProjectIdAndEmailIgnoreCaseAndStatus(projectId, email, InvitationStatus.PENDING)) {
			throw new ConflictException("There is a pending invitation for this email");
		}
		
		byte[] bytes = new byte[24];
		RANDOM.nextBytes(bytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		
		ProjectInvitation invitation = new ProjectInvitation(inviterMembership.getProject(), 
				email, request.role(), token, inviterMembership.getUser(), 
				Instant.now().plus(INVITATION_DAYS, ChronoUnit.DAYS));
		
		invitationRepository.save(invitation);
		log.info("Invitation created: projectId = {} byUserId = {}", projectId, currentUserId);
		
		return new InvitationResponse(invitation.getId(), email, invitation.getRole(),
				token, invitation.getExpiresAt(), invitation.getStatus());
		
	}
	
	@Transactional
	public ProjectSummary accept(Long currentUserId, String token) {
		ProjectInvitation invitation = invitationRepository.findByToken(token)
				.orElseThrow(() -> new NotFoundException("Not found the invitation"));
		
		if(!invitation.isPending()) {
			throw new ConflictException("The invitation expired or has been used");
		}
		
		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new NotFoundException("Not found user"));
		
		if (!user.getEmail().equalsIgnoreCase(invitation.getEmail())) {
			throw new ConflictException("The invitation is used for another email");
		}
		
		Long projectId = invitation.getProject().getId();
		
		if(memberRepository.existsByProjectIdAndUserId(projectId, currentUserId)) {
			throw new ConflictException("You joined this invitation");
		}
		
		memberRepository.save(new ProjectMember(invitation.getProject(), user, invitation.getRole()));
		 
		invitation.accept();
		invitationRepository.save(invitation);
		log.info("Invitation accepted: projectId={} userId={}", projectId, currentUserId);
		
		return projectMapper.toSummary(invitation.getProject(), invitation.getRole(),
				memberRepository.countByProjectId(projectId));
	}
	
	@Transactional(readOnly = true)
	public List<InvitationResponse> listPending(Long currentUserId, Long projectId){
		accessService.requireRole(projectId, currentUserId, ProjectRole.OWNER, ProjectRole.MANAGER);
		
		return invitationRepository
				.findByProjectIdAndStatus(projectId, InvitationStatus.PENDING)
				.stream()
				.map(i -> new InvitationResponse(i.getId(), i.getEmail(), i.getRole()
						, i.getToken(), i.getExpiresAt(), i.getStatus()))
				.toList();
				
	}
	
	@Transactional
	public void revoke(Long currentUserId, Long invitationId) {
		ProjectInvitation invitation = invitationRepository.findById(invitationId)
				.orElseThrow(() -> new NotFoundException("Not found invitation"));
		accessService.requireRole(invitation.getProject().getId(), currentUserId, ProjectRole.OWNER, ProjectRole.MANAGER);
		
		if (invitation.getStatus() != InvitationStatus.PENDING) {
			throw new ConflictException("Just revoke invitaion pending");
		}
		
		invitation.revoke();
		invitationRepository.save(invitation);
	}
	
	@Transactional
	public MemberResponse updateRole(
			Long currentUserId, Long projectId, 
			Long targetUserId, ProjectRole newRole
			) {
		accessService.requireRole(projectId, currentUserId, ProjectRole.OWNER);
		
		if (newRole == ProjectRole.OWNER) {
			throw new ConflictException("Cannot update new role is OWNER. Please use the ownership transfer function.");
		}
		
		ProjectMember target = memberRepository.findByProjectIdAndUserId(projectId, targetUserId)
				.orElseThrow(() -> new NotFoundException("Not found membership"));
		if (target.getRole() == ProjectRole.OWNER) {
			throw new ConflictException("Cannot demote the project owner. Project must have owner");
		}
		
		target.setRole(newRole);
		memberRepository.save(target);
		
		log.info("Member role changed: projectId={} targetUserId={} newRole={}",
				projectId, targetUserId, newRole);
		
		return new MemberResponse(userMapper.toSummary(target.getUser()),target.getRole(), target.getCreatedAt());
	}
	
	@Transactional
	public void remove(Long currentUserId, Long projectId, Long targetUserId) {
		ProjectMember actor = accessService.requireRole(projectId, currentUserId, ProjectRole.OWNER, ProjectRole.MANAGER);
		
		ProjectMember target = memberRepository
				.findByProjectIdAndUserId(projectId, targetUserId)
				.orElseThrow(() -> new NotFoundException("Not found this member in project"));
		if (target.getRole() == ProjectRole.OWNER) {
			throw new ConflictException("Cannot remove owner");
		}
		
		if (actor.getRole() == ProjectRole.MANAGER && target.getRole() == ProjectRole.MANAGER) {
			throw new ForbiddenException("Only owner can remove other manage");
		}
		
		int unassigned = taskRepository.unassignAllInProject(projectId, targetUserId);
		
		memberRepository.delete(target);
		log.info("Member removed: projectId={} targetUserId={} unassignedTasks={} byUserId={}",
				projectId, targetUserId, currentUserId, unassigned);
	}
}
