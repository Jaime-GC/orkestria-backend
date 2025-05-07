package com.tfg.app.user.repository;

import com.tfg.app.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> { }
