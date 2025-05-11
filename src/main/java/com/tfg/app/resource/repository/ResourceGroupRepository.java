package com.tfg.app.resource.repository;

import com.tfg.app.resource.model.ResourceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceGroupRepository extends JpaRepository<ResourceGroup,Long> {
    List<ResourceGroup> findByParent(ResourceGroup parent);
    List<ResourceGroup> findByName(String name);
    List<ResourceGroup> findByIsReservableTrue();
}
