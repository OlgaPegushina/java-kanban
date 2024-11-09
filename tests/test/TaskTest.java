package test;

import model.Status;
import model.Task;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private TaskManager manager;
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Просто задача - 1", "Описание простой задачи - 1");
        manager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        final int taskId = manager.addNewTask(task);
        final Task savedTask = manager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTaskTest() {
        final int taskId = manager.addNewTask(task);
        final Task savedTask = manager.getTask(taskId);

        savedTask.setStatus(Status.DONE);
        manager.updateTask(savedTask);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void deleteTaskTest() {
        manager.addNewTask(task);
        manager.deleteTask(task.getId());

        assertTrue(manager.getAllTasks().isEmpty(), "Задача не удалилась");
        assertEquals(0, manager.getAllTasks().size(), "Задача не удалилась");
    }

    @Test
    void deleteAllTasksTest() {
        manager.addNewTask(task);
        manager.addNewTask(task);
        manager.deleteAllTasks();

        assertTrue(manager.getAllTasks().isEmpty(), "Задачи не удалились");
        assertEquals(0, manager.getAllTasks().size(), "Задачи не удалились");
    }
}