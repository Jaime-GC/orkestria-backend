package com.tfg.app.resource.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private ResourceGroup parent;
}
