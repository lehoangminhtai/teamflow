package com.teamflow.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teamflow.backend.entity.Project;
import com.teamflow.backend.entity.ProjectMember;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	
}
