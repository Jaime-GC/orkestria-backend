package com.tfg.app.resource.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Avoids infinite recursion in JSON serialization
public class ResourceGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Avoids infinite recursion in JSON serialization
    private ResourceGroup parent;
    
    @Column(name = "is_reservable")
    private Boolean isReservable = false; 
    
    
    // Constructor for tests
    public ResourceGroup(Long id) {
        this.id = id;
    }
}
