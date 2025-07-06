package com.tfg.app.task.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.tfg.app.project.model.Project;
import com.tfg.app.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = true) 
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Avoids infinite recursion in JSON serialization
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User assignedUser;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;


    
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Type {
        URGENT, RECURRING, OTHER
    }

    public enum Status {
        TODO, DOING, BLOCKED, DONE
    }
}