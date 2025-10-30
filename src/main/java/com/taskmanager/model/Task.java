package com.taskmanager.model;
import jakarta.persistence.*;
import lombok.Data; // Incluye @Getter, @Setter, @RequiredArgsConstructor, etc.
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    // 1. Identificador de la Entidad (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ¿Qué estrategia de generación de ID usamos para PostgreSQL?
    private Long id;

    @Column(nullable = false)
    private String title;

    // 3. Descripción
    private String description;

    // 4. Estado (Usando nuestro Enum)
    @Enumerated(EnumType.STRING) // Importante: guarda el nombre del Enum, no el índice.
    private TaskStatus status;

    // 5. Fecha de Creación
    private LocalDateTime createdAt;
}
