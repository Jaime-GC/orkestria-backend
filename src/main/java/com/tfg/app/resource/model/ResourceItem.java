package com.tfg.app.resource.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ResourceGroup group;
    
    // Add constructor with just ID for tests
    public ResourceItem(Long id) {
        this.id = id;
    }
}
