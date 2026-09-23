package com.teamflow.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teamflow.backend.entity.Task;
import com.teamflow.backend.entity.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {
	List<Task> findByProjectId(Long projectId);
	List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
	long countByProjectIdAndStatus(Long projectId, TaskStatus status);
	
	@Modifying
	@Query(""" 
			update Task t set t.assignee = null
			where t.project.id = :projectId
			and t.assignee.id = :userId
			""")
	int unassignAllInProject(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
