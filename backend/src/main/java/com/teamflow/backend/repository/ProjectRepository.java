package com.teamflow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamflow.backend.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
