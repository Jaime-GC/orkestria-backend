package com.tfg.app.user.repository;

import com.tfg.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<User,Long> { }
