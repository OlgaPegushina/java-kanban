package service;

import exception.ManagerValidatePriority;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Epic epic;

    @BeforeEach
    public abstract void setUp() throws IOException;

    @AfterEach
    public abstract void finish();

    @Test
    public void addNewTaskTest() {
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskTest() {
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final int savedEpicId = savedSubtask.getEpicId();

        assertEquals(subtask.getEpicId(), savedEpicId, "Эпики у подзадач не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void addNewEpicTest() {
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateTaskTest() {
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        savedTask.setStatus(Status.DONE);
        taskManager.updateTask(savedTask);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void updateEpicTest() {
        int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);
        Epic epic2 = new Epic(epic.getId(), "Эпическая задача - 2", "Ставим вместо эпической задачи - 1", epic.getStatus());
        taskManager.updateEpic(epic2);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic2, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic2, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateSubtaskAndEpicTest() {
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        savedSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);

        assertNotNull(savedSubtask, "Подзадачи не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
        assertEquals(taskManager.getEpic(epicId).getStatus(), savedSubtask.getStatus(), "Статусы подзадач не совпадают");
    }

    @Test
    void deleteTaskTest() {
        taskManager.addNewTask(task);
        taskManager.deleteTask(task.getId());

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задача не удалилась");
        assertEquals(0, taskManager.getAllTasks().size(), "Задача не удалилась");
    }

    @Test
    void deleteAllTasksTest() {
        taskManager.addNewTask(task);
        taskManager.addNewTask(task);
        taskManager.deleteAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалились");
        assertEquals(0, taskManager.getAllTasks().size(), "Задачи не удалились");
    }

    @Test
    void deleteEpicTest() {
        taskManager.addNewEpic(epic);
        taskManager.deleteEpic(epic.getId());

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпик не удалился");
        assertEquals(0, taskManager.getAllEpics().size(), "Эпик не удалился");
    }

    @Test
    void deleteAllEpicsTest() {
        taskManager.addNewEpic(epic);
        taskManager.addNewEpic(epic);
        taskManager.deleteAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалились");
        assertEquals(0, taskManager.getAllEpics().size(), "Эпики не удалились");
    }

    @Test
    void deleteSubtaskTest() {
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        Integer subtaskId = taskManager.addNewSubtask(subtask);

        assertEquals(1, epic.getSubtaskIds().size(), "ID подзадачи не зарегистрировался у эпика");

        taskManager.deleteSubtask(subtaskId);

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадача не удалилася");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадача не удалилася");
        assertTrue(epic.getSubtaskIds().isEmpty(), "ID подзадачи не удалился из списка у эпика");
    }

    @Test
    void deleteAllSubtaskTest() {
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask);
        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалилися");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не удалилися");
    }

    @Test
    void updateStatusEpicTest() {
        final int epicId = taskManager.addNewEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус NEW");

        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        taskManager.addNewSubtask(subtask);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(Status.DONE, epic.getStatus(), "Неверный статус DONE");

        Subtask subtask2 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        taskManager.addNewSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус IN_PROGRESS");
    }

    @Test
    public void validateTaskPriorityTest() {
        Task task1 = new Task("Задача- 1", "Описание 1", LocalDateTime.of(2023, 10,
                1, 10, 0), Duration.ofHours(2));
        Task task2 = new Task("Задача- 2", "Описание 2", LocalDateTime.of(2023, 10,
                1, 11, 0), Duration.ofHours(1));

        taskManager.addNewTask(task1);
        Exception exception = assertThrows(ManagerValidatePriority.class, () -> {
            taskManager.addNewTask(task2);
        });

        assertEquals("Задача пересекается по времени с уже существующими. Её выполнение невозможно!",
                exception.getMessage());
    }

    @Test
    public void validateEpicAndSubtaskPriorityTest() {
        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", LocalDateTime.of(2024, 10,
                1, 10, 0), Duration.ofHours(2), epic.getId());
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", LocalDateTime.of(2024, 10,
                1, 11, 0), Duration.ofHours(1), epic.getId());
        Exception exception = assertThrows(ManagerValidatePriority.class, () -> {
            taskManager.addNewSubtask(subtask2);
        });

        assertEquals("Задача пересекается по времени с уже существующими. Её выполнение невозможно!",
                exception.getMessage());
    }
}