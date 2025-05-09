package com.tfg.app.resource.repository;

import com.tfg.app.resource.model.ResourceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceItemRepository extends JpaRepository<ResourceItem,Long> { }
