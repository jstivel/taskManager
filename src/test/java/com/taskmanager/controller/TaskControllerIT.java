package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// Inicia el contexto completo de Spring Boot, incluyendo Controllers, Services y Repositories.
@SpringBootTest
// Configura el MockMvc para simular peticiones HTTP.
@AutoConfigureMockMvc
class TaskControllerIT {

    // Utilidad para simular peticiones HTTP (GET, POST, etc.)
    @Autowired
    private MockMvc mockMvc;

    // Utilidad para convertir objetos Java a JSON y viceversa
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTask_shouldReturn201() throws Exception {
        // 1. GIVEN: Crea el DTO de la petición
        TaskRequest request = new TaskRequest(
                "Integration Test Task",
                "Details via MockMvc",
                TaskStatus.IN_PROGRESS
        );

        // Convierte el objeto Java a la cadena JSON que se enviará en la petición
        String requestJson = objectMapper.writeValueAsString(request);

        // 2. WHEN & THEN: Simular la petición POST y verificar
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType( MediaType.APPLICATION_JSON)
                        .content( requestJson )
                )
                .andExpect(status().isCreated()) // Verifica el código HTTP 201
                .andExpect(jsonPath("$.id").exists()) // Verifica que el objeto creado tiene un ID
        ;
    }

    @Test
    void testGetTasks_shouldReturnArray() throws Exception {
        // 1. GIVEN: Crear una tarea usando POST para que exista en la DB
        TaskRequest request = new TaskRequest("Task for GET test", "Details", TaskStatus.COMPLETED);
        String requestJson = objectMapper.writeValueAsString(request);

        // Ejecutar el POST y verificar que se creó (esto puebla la DB)
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated());

        // 2. WHEN & THEN: Simular la petición GET y verificar
        mockMvc.perform(
                        get("/api/v1/tasks")
                )
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$").isArray()) // La respuesta es un array
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1)))) // El array tiene al menos 1 elemento
        ;
    }

    @Test
    void testUpdateTask_shouldReturn200AndUpdatedData() throws Exception {
        // 1. GIVEN: Crear la tarea original
        // Paso 1.1: Simular el POST original (igual que antes)


        String originalRequestJson = objectMapper.writeValueAsString(
                new TaskRequest("Old Title", "Old Desc", TaskStatus.IN_PROGRESS)
        );

        // Ejecutar el POST y capturar la respuesta
        String responseContent = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalRequestJson)
                )
                .andExpect(status().isCreated()) // Aseguramos que se creó
                // ⬇️ Capturar el JSON de la respuesta para obtener el ID
                .andReturn().getResponse().getContentAsString();

        // Paso 1.2: Extraer el ID de la respuesta
        Long taskId = objectMapper.readTree(responseContent).get("id").asLong();

        // 2. WHEN: Simular la petición PUT (Actualización)
        String updatedTitle = "New Title Integration Test";
        String updatedDescription = "Updated Description";

        // Crear el DTO con los nuevos datos
        TaskRequest updatedRequest = new TaskRequest(
                updatedTitle,
                updatedDescription,
                TaskStatus.COMPLETED // Cambiamos el estado
        );
        String updatedRequestJson = objectMapper.writeValueAsString(updatedRequest);

        mockMvc.perform(put("/api/v1/tasksv/{id}", taskId)
                        .contentType( MediaType.APPLICATION_JSON)
                        .content( updatedRequestJson )
                )
                .andExpect(status().isOk()) // Verifica el código HTTP 201
                .andExpect(jsonPath("$.title").value( updatedTitle ))
                .andExpect(jsonPath("$.description").value( updatedDescription ))
        ;
    }

    @Test
    void testGetTaskById() throws Exception {
        // 1. GIVEN: Crear una tarea usando POST para que exista en la DB
        TaskRequest request = new TaskRequest("Task for GET test", "Details", TaskStatus.COMPLETED);
        String requestJson = objectMapper.writeValueAsString(request);

        // Ejecutar el POST y verificar que se creó (esto puebla la DB)
        String responseContent = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(responseContent).get("id").asLong();

        // 2. WHEN & THEN: Simular la petición GET y verificar
        mockMvc.perform(
                        get("/api/v1/tasks/{id}", taskId)
                )
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.title").value( "Task for GET test" ))
        ;
    }

    @Test
    void testDeleteTask() throws Exception {
        // 1. GIVEN: Crear una tarea usando POST para que exista en la DB
        TaskRequest request = new TaskRequest("Task for GET test", "Details", TaskStatus.COMPLETED);
        String requestJson = objectMapper.writeValueAsString(request);

        // Ejecutar el POST y verificar que se creó (esto puebla la DB)
        String responseContent = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(responseContent).get("id").asLong();

        // 2. WHEN & THEN
        mockMvc.perform(
                        delete("/api/v1/tasks/{id}", taskId)
                )
                .andExpect(status().isNoContent()) // HTTP 204
        ;
        // 3. VERIFICATION: Try to GET the resource and expect 404
        mockMvc.perform(
                get("/api/v1/tasks/{id}", taskId)
        )
                .andExpect( status().isNotFound() ) // Verify 404 Not Found
        ;
    }
    @Test
    void testUpdateTask_shouldReturn404_whenNotFound() throws Exception {
        Long nonExistentId = 999L;

        // 1. GIVEN: Crear un JSON de actualización
        String updateJson = objectMapper.writeValueAsString(
                new TaskRequest("Non-existent update", "details", TaskStatus.COMPLETED)
        );


        // 2. WHEN: Simular la petición PUT (Actualización)

        mockMvc.perform(put("/api/v1/tasks/{id}", nonExistentId)
                        .contentType( MediaType.APPLICATION_JSON)
                        .content( updateJson )
                )
                .andExpect(status().isNotFound()) // Verifica el código HTTP 201

        ;
    }
    @Test
    void testDeleteTask_shouldReturn404_whenNotFound() throws Exception {
        // 1. GIVEN
        Long nonExistentId = 999L;

        // 2. WHEN & THEN
        mockMvc.perform(
                        delete("/api/v1/tasks/{id}", nonExistentId)
                )
                .andExpect(status().isNotFound()) // HTTP 204
        ;

    }
}