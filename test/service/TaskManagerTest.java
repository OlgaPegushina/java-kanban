package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private TaskManager manager;
    private Task task;
    private Epic epic;

    @BeforeEach
    void setUp() {
        task = new Task("Просто задача - 1", "Описание простой задачи - 1");
        epic = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        manager = Managers.getDefault();
    }

    @Test
    void addNewTaskTest() {
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

    @Test
    void addNewEpicTest() {
        final int epicId = manager.addNewEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtaskTest() {
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        int subtaskId = manager.addNewSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Сабтаск не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final int savedEpicId = savedSubtask.getEpicId();

        assertEquals(subtask.getEpicId(), savedEpicId, "Епики у сабтасок не совпадают.");

        final List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Сабтасков.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void updateEpicTest() {
        int epicId = manager.addNewEpic(epic);
        final Epic savedEpic = manager.getEpic(epicId);
        Epic epic2 = new Epic(epic.getId(), "Эпическая задача - 2", "Ставим вместо эпической задачи - 1", epic.getStatus());
        manager.updateEpic(epic2);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic2, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic2, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateSubtaskAndEpicTest() {
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        int subtaskId = manager.addNewSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(subtaskId);

        savedSubtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        assertNotNull(savedSubtask, "Сабтаск не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtasks, "Сабтаски на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтаск.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
        assertEquals(manager.getEpic(epicId).getStatus(), savedSubtask.getStatus(), "Статусы сабтасков не совпадают");
    }

    @Test
    void deleteEpicTest() {
        manager.addNewEpic(epic);
        manager.deleteEpic(epic.getId());

        assertTrue(manager.getAllEpics().isEmpty(), "Эпик не удалился");
        assertEquals(0, manager.getAllEpics().size(), "Эпик не удалился");
    }

    @Test
    void deleteAllEpicsTest() {
        manager.addNewEpic(epic);
        manager.addNewEpic(epic);
        manager.deleteAllEpics();

        assertTrue(manager.getAllEpics().isEmpty(), "Эпики не удалились");
        assertEquals(0, manager.getAllEpics().size(), "Эпики не удалились");
    }

    @Test
    void deleteSubtaskTest() {
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        int subtaskId = manager.addNewSubtask(subtask);
        manager.deleteSubtask(subtaskId);

        assertTrue(manager.getAllSubtasks().isEmpty(), "Сабтаск не удалился");
        assertEquals(0, manager.getAllSubtasks().size(), "Сабтаск не удалился");
    }

    @Test
    void deleteAllSubtaskTest() {
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        manager.addNewSubtask(subtask);
        manager.addNewSubtask(subtask);
        manager.deleteAllSubtasks();

        assertTrue(manager.getAllSubtasks().isEmpty(), "Сабтаски не удалилися");
        assertEquals(0, manager.getAllSubtasks().size(), "Сабтаски не удалилися");
    }

    @Test
    void updateStatusEpicTest() {
        final int epicId = manager.addNewEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус NEW");

        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        manager.addNewSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        assertEquals(Status.DONE, epic.getStatus(), "Неверный статус DONE");

        Subtask subtask2 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        manager.addNewSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус IN_PROGRESS");
    }
}
