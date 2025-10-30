package com.taskmanager.repository;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;



@DataJpaTest
class TaskRepositoryTest {

    // 1. El repositorio que queremos probar
    @Autowired
    private TaskRepository taskRepository;

    // 2. La utilidad para interactuar con la DB de prueba
    @Autowired
    private TestEntityManager entityManager; // A!

    @Test
    void whenFindByStatus_thenReturnTasks() {
        // 1. GIVEN: Prepara los datos en la base de datos
        Task taskTodo = new Task(
                null,
                "Task 1: Should be found",
                "Desc 1",
                TaskStatus.COMPLETED,
                null // La fecha se llenará automáticamente
        );

        // PERSISTIR
        entityManager.persist(taskTodo);
        entityManager.flush(); // Garantiza que los cambios se guarden en la DB de prueba antes de la búsqueda


        // 2. WHEN: Ejecuta el método del repositorio
        List<Task> foundTasks = taskRepository.findByStatus(TaskStatus.COMPLETED); // Asume que TaskStatus.COMPLETED es el estado que quieres buscar

        // 3. THEN: Comprueba el resultado
        assertThat(foundTasks)
                .isNotEmpty()
                .hasSize(1)
                .extracting(Task::getStatus) // Extrae la propiedad 'status' de los objetos de la lista
                .contains(TaskStatus.COMPLETED);

    }
}