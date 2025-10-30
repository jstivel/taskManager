package com.taskmanager.controller;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @RequestBody @Valid TaskRequest request
    ) {
        return taskService.saveTask(request);
    }

    @GetMapping
    public List<TaskResponse>  findAllTasks()  {
        return taskService.findAllTasks();
    }

    @GetMapping ("/{id}")
    public TaskResponse findTaskById(
            @PathVariable Long id
    ) {
        return taskService.findTaskById(id);
    }

    @PutMapping ("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT )
    public void deleteTask(
            @PathVariable Long id
    ) {
        taskService.deleteTask(id);
    }
}
