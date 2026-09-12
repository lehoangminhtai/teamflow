package com.teamflow.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamflow.backend.entity.Task;
import com.teamflow.backend.entity.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {
	List<Task> findByProjectId(Long projectId);
	List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
	long countByProjectIdAndStatus(Long projectId, TaskStatus status);
}
