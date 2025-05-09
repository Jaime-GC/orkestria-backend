package com.tfg.app.resource.model;

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

    @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading for performance

    @JoinColumn(name = "resource_item_id", nullable = false)
    private ResourceItem resourceItem;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String reservedBy; // Username or ID of the person who made the reservation
}
