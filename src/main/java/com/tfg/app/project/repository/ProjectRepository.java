package com.tfg.app.project.repository;

import com.tfg.app.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> { }
