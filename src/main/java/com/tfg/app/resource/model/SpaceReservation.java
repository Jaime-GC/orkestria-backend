package com.tfg.app.resource.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "space_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; 

    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_group_id", nullable = false)
    private ResourceGroup resourceGroup;  // Changed from ResourceItem

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String reservedBy; // Username or ID of the person who made the reservation
}
