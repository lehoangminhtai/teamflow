package com.teamflow.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teamflow.backend.entity.ProjectMember;
import com.teamflow.backend.entity.ProjectRole;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
	List<ProjectMember> findByUserId(Long userId);

	Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

	boolean existsByProjectIdAndUserId(Long projectId, Long userId);

	List<ProjectMember> findByProjectIdOrderByCreatedAtAsc(Long projectId);

	long countByProjectIdAndRole(Long projectId, ProjectRole role);


	long countByProjectId(Long projectId);

	@Query("""
			select m from ProjectMember m join fetch m.project p
			where m.user.id = :userId and p.archived = :archived order by p.createdAt desc
			""")
	List<ProjectMember> findMembershipsWithProject(@Param("userId") Long userId,
			@Param("archived") boolean archived);
	
	@Query("""
			select m from ProjectMember m join fetch m.project p
			where m.user.id = :userId and p.archived = :archived
			and (cast(:q as string) is null or lower(p.name) like lower(concat('%', cast(:q as string), '%')))
			""")
	Page<ProjectMember> searchMemberships(
			@Param("userId") Long userId,
			@Param("archived") boolean archived,
			@Param("q") String q,
			Pageable pageable
			);
}
