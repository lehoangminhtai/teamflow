package com.teamflow.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamflow.backend.entity.InvitationStatus;
import com.teamflow.backend.entity.ProjectInvitation;

public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, Long> {
	Optional<ProjectInvitation> findByToken(String token);
	
	boolean existsByProjectIdAndEmailIgnoreCaseAndStatus(
			Long projectId, String email, InvitationStatus status);
	
	List<ProjectInvitation> findByProjectIdAndStatus(Long projectId, InvitationStatus status);
}
