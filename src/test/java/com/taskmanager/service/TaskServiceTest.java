package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    // 1. Instancia del servicio que queremos probar, con las dependencias inyectadas.
    @InjectMocks
    private TaskService taskService;

    // 2. Dependencias simuladas (Mocks) que el servicio usa.
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    // Create the input DTO
    TaskRequest request = new TaskRequest("Test Task","Description", TaskStatus.IN_PROGRESS);

    Task entity = new Task();

    TaskResponse response = new TaskResponse();

    // Método de prueba
    @Test
    void saveTask_shouldSaveAndReturnResponse() {

        // 1. Msimula mapeo (Request ->  Entity)
        Mockito.when(taskMapper.toEntity(request)).thenReturn(entity);

        // 2. Msimula mapeo (Entity -> Saved Entity)
        Mockito.when(taskRepository.save(entity)).thenReturn(entity);

        // 3. simula mapeo (Saved Entity -> Response)
        Mockito.when(taskMapper.toResponse(entity)).thenReturn(response);

        //ejecución
        TaskResponse actualResponse = taskService.saveTask(request);

        // 4. ASERCIÓN (ASSERT)
        Assertions.assertEquals(response,actualResponse);

        // 5. VERIFICACIÓN (VERIFY)
        // Confirma que el método clave del mock fue llamado.
        Mockito.verify(taskRepository, Mockito.times(1)).save(entity);
    }

    @Test
    void findTaskById_shouldReturnTask_whenFound() {
        Long id = 1L;

        // Asignamos el ID a la entidad y respuesta para la prueba
        entity.setId(id);
        response.setId(id);

        // MOCKING: When findById(id) is called, return an Optional containing the entity
        Mockito.when( taskRepository.findById(id) ).thenReturn( java.util.Optional.of(entity)  );

        // MOCKING: Simular el mapeo a respuesta
        Mockito.when(taskMapper.toResponse(entity)).thenReturn(response);

        // 1. EJECUTAR EL MÉTODO BAJO PRUEBA (ACT)
        TaskResponse actualResponse = taskService.findTaskById(id);

        // 2. VERIFICAR (ASSERT y VERIFY)
        Assertions.assertEquals(response,actualResponse);
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
    }
    @Test
    void findTaskById_shouldThrowException_whenNotFound() {
        Long id = 999L; // Un ID que no existe

        // GIVEN (MOCKING)
        // Simula que el repositorio NO encuentra el ID
        Mockito.when( taskRepository.findById(id) ).thenReturn( java.util.Optional.empty() );

        // WHEN (EXECUTION & ASSERTION)
        // 1. ASERCIÓN: Esperamos que la ejecución lance la excepción
        Assertions.assertThrows(

                ResourceNotFoundException.class,

                // 2. ¿Qué código la lanza? (Ejecución)
                () -> taskService.findTaskById(id)
        );

        // THEN (VERIFY)
        // 3. VERIFICACIÓN: Confirmamos que el repositorio fue llamado
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);

    }
    @Test
    void updateTask_shouldUpdateAndReturnResponse_whenFound() {
        Long id = 1L;

        // 1. GIVEN (MOCKS)
        // Simular que la tarea original es ENCONTRADA
        Mockito.when(taskRepository.findById(id)).thenReturn(java.util.Optional.of(entity));

        // Simular el guardado de la ENTIDAD ACTUALIZADA
        Mockito.when(taskRepository.save(entity)).thenReturn(entity);

        // Simular el mapeo a RESPUESTA FINAL
        Mockito.when(taskMapper.toResponse(entity)).thenReturn(response);

        // 2. WHEN (EXECUTION)
        TaskResponse actualResponse = taskService.updateTask(id, request);

        // 3. THEN (ASSERT & VERIFY)
        // ASERCIÓN: El resultado devuelto es el esperado
        Assertions.assertEquals(response, actualResponse);

        // VERIFICACIÓN 1: Confirmamos que se BÚSCÓ la tarea original
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id); // <-- ¡Este es nuevo!

        // VERIFICACIÓN 2: Confirmamos que se GUARDÓ la entidad actualizada
        Mockito.verify(taskRepository, Mockito.times(1)).save(entity);

    }
    @Test
    void updateTask_shouldThrowException_whenNotFound() {
        Long id = 999L;

        // GIVEN: Simula que el repositorio NO encuentra el ID
        Mockito.when(taskRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // WHEN/THEN: Aserción

        Assertions.assertThrows(

                ResourceNotFoundException.class,

                // 2. ¿Qué código la lanza? (Ejecución)
                () -> taskService.updateTask(id,request)
        );

        // VERIFY: Confirmar que findById fue llamado.
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void deleteTask_shouldCallDelete_whenFound() {
        Long id = 1L;
        entity.setId(id);

        // 1. GIVEN (MOCKS)
        // Simular que la tarea original es ENCONTRADA
        //Mockito.when(taskRepository.findById(id)).thenReturn(java.util.Optional.of(entity));
        Mockito.when(taskRepository.existsById(id)).thenReturn(true);

        // 2. WHEN (EXECUTION)
        taskService.deleteTask(id);

        // 3. THEN (VERIFY)
        // Verificar que se llamó a findById(id)
        //Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
        Mockito.verify(taskRepository, Mockito.times(1)).existsById(id);

        // VERIFICAR: Comprueba que el método delete fue llamado con la entidad
        //Mockito.verify( taskRepository, Mockito.times(1)).delete(entity);
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void deleteTask_shouldCallDelete_whenNotFound() {
        Long id = 1L;
        entity.setId(id);

        // 1. GIVEN (MOCKS)
        // Simular que la tarea no fue encontrada
        //Mockito.when(taskRepository.findById(id)).thenReturn(java.util.Optional.empty());
        Mockito.when(taskRepository.existsById(id)).thenReturn(false);

        // 2. WHEN (EXECUTION)
        Assertions.assertThrows(

                ResourceNotFoundException.class,
                () -> taskService.deleteTask(id)
        );

        // 3. THEN (VERIFY)
        // Verificar que se llamó a findById(id)
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
        Mockito.verify(taskRepository, Mockito.never()).deleteById(id);

    }

    @Test
    void findAllTasks_shouldReturnListOfResponses() {
        // 1. GIVEN: Define las listas de prueba
        List<Task> taskList = List.of(entity); // Asume que 'entity' está definida
        List<TaskResponse> responseList = List.of(response); // Asume que 'response' está definida

        // 2. MOCKING - Simular la búsqueda en el repositorio
        Mockito.when( taskRepository.findAll()).thenReturn(taskList);

        // 3. MOCKING - Simular el mapeo de la lista
        Mockito.when(taskMapper.toResponse(Mockito.any(Task.class)))
                .thenReturn(responseList.get(0));

        // ... EXECUTION & VERIFICATION
        // 1. EJECUTAR EL MÉTODO BAJO PRUEBA (ACT)
        List<TaskResponse> actualResponseList = taskService.findAllTasks();

        // 2. VERIFICAR (ASSERT y VERIFY)
        Assertions.assertEquals(responseList,actualResponseList);
        Mockito.verify(taskRepository, Mockito.times(1)).findAll();

    }
}