package com.taskmanager.dto;

import com.taskmanager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Lombok: Genera getters, setters, toString
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    // El título no puede ser nulo ni solo espacios en blanco.
    @NotBlank(message = "Title is mandatory")
    private String title;

    // La descripción puede ser nula, por lo que no usamos @NotBlank
    private String description;

    // El estado es obligatorio.
    @NotNull(message = "Status is mandatory")
    private TaskStatus status;
}