package com.tfg.app.user.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name="roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(unique=true, nullable=false)
    private RoleName name;

    public enum RoleName {
        ADMIN, MANAGER, EMPLOYEE, CLIENT
    }
}
