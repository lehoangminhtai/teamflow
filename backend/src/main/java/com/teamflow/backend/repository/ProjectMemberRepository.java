package com.teamflow.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamflow.backend.entity.ProjectMember;
import com.teamflow.backend.entity.ProjectRole;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
	List<ProjectMember> findByUserId(Long userId);
	Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
	boolean existsByProjectIdAndUserId(Long projectId, Long userId);
	
	List<ProjectMember> findByProjectIdOrderByCreatedAtAsc(Long projectId);
	long countByProjectIdAndRole(Long projectId, ProjectRole role);
}
