package com.tfg.app.project.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = true)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ProjectStatus status;

    public enum ProjectStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETE
    }
}