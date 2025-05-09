package com.tfg.app.resource.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;
}
