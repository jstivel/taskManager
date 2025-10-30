package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskMapper {

    // 1. Convertir Entidad a DTO de Respuesta (Para enviar al cliente)
    public TaskResponse toResponse(Task task) {

        return new TaskResponse(
                 task.getId(),task.getTitle(),task.getDescription(),task.getStatus(),task.getCreatedAt()
        );
    }

    // 2. Convertir DTO de Petición a Entidad (Para guardar en DB)
    public Task toEntity(TaskRequest request) {

        return new Task(
                null,request.getTitle(),  request.getDescription(),request.getStatus(), LocalDateTime.now()
        );
    }
}