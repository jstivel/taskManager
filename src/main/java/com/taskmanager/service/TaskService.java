package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Marca la clase como un componente de servicio de Spring
@RequiredArgsConstructor // Lombok: Inyecta dependencias a través del constructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    // Métodos CRUD (Crear, Leer, Actualizar, Eliminar)

    // 1. Crear Tarea
    @Transactional // Asegura que la operación sea atómica en la base de datos
    public TaskResponse saveTask(TaskRequest request) {

        Task task=taskRepository.save(taskMapper.toEntity(request));

        return taskMapper.toResponse(task);
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> findAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public TaskResponse findTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        return taskMapper.toResponse(task);
    }
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {

        // 1. Obtener la entidad Task existente, o lanzar 404
        Task existingTask =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        // 2. Actualizar los campos de existingTask con los datos de 'request'
        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setStatus(request.getStatus());

        // 3. Guardar y devolver
        return taskMapper.toResponse(taskRepository.save(existingTask));
    }
    @Transactional
    public void deleteTask(Long id) {

        // 1. Verificar si la tarea existe
        if (taskRepository.existsById(id)) {
            // 2. Eliminar
            taskRepository.deleteById(id);
        } else {
            // 3. Lanzar la excepción si no existe
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }
    }
}