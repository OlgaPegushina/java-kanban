package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private Task task;
    private Task task2;
    private Epic epic;
    private Epic epic2;
    private File file;

    @BeforeEach
    void setUp() throws IOException {
        Path path = Files.createTempFile("file_auto", ".csv");
        file = new File(String.valueOf(path));
        fileBackedTaskManager = new FileBackedTaskManager(file);
        task = new Task("Просто задача - 1", "Описание простой задачи - 1");
        task2 = new Task("Просто задача - 2", "Описание простой задачи - 2");
        epic = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
    }

    @Test
    void addNewTaskTest() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final Task savedTask = fileBackedTaskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = fileBackedTaskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void loadFromFileTest() {
        fileBackedTaskManager.addNewTask(task);
        fileBackedTaskManager.addNewTask(task2);
        FileBackedTaskManager backedTaskManager2 = FileBackedTaskManager.loadFromFile(file);
        final List<Task> tasks = backedTaskManager2.getAllTasks();

        assertEquals(2, tasks.size(), "Количество задач не верное");
        assertEquals(List.of(task, task2), tasks, "Задачи не соответствует");
    }

    @Test
    void loadFromEmptyFileTest() {
        FileBackedTaskManager backedTaskManager2 = FileBackedTaskManager.loadFromFile(file);
        final List<Task> tasks = backedTaskManager2.getAllTasks();

        assertEquals(0, tasks.size(), "Количество задач не верное");
    }

    @Test
    void updateTaskTest() {
        final int taskId = fileBackedTaskManager.addNewTask(task);
        final Task savedTask = fileBackedTaskManager.getTask(taskId);

        savedTask.setStatus(Status.DONE);
        fileBackedTaskManager.updateTask(savedTask);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void deleteTaskTest() {
        fileBackedTaskManager.addNewTask(task);
        fileBackedTaskManager.deleteTask(task.getId());

        assertTrue(fileBackedTaskManager.getAllTasks().isEmpty(), "Задача не удалилась");
        assertEquals(0, fileBackedTaskManager.getAllTasks().size(), "Задача не удалилась");
    }

    @Test
    void deleteAllTasksTest() {
        fileBackedTaskManager.addNewTask(task);
        fileBackedTaskManager.addNewTask(task);
        fileBackedTaskManager.deleteAllTasks();

        assertTrue(fileBackedTaskManager.getAllTasks().isEmpty(), "Задачи не удалились");
        assertEquals(0, fileBackedTaskManager.getAllTasks().size(), "Задачи не удалились");
    }

    @Test
    void addNewEpicTest() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        final Epic savedEpic = fileBackedTaskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = fileBackedTaskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtaskTest() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = fileBackedTaskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Сабтаск не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final int savedEpicId = savedSubtask.getEpicId();

        assertEquals(subtask.getEpicId(), savedEpicId, "Епики у сабтасок не совпадают.");

        final List<Subtask> subtasks = fileBackedTaskManager.getAllSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Сабтасков.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void updateEpicTest() {
        int epicId = fileBackedTaskManager.addNewEpic(epic);
        final Epic savedEpic = fileBackedTaskManager.getEpic(epicId);
        Epic epic2 = new Epic(epic.getId(), "Эпическая задача - 2", "Ставим вместо эпической задачи - 1", epic.getStatus());
        fileBackedTaskManager.updateEpic(epic2);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic2, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = fileBackedTaskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic2, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateSubtaskAndEpicTest() {
        int epicId = fileBackedTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        int subtaskId = fileBackedTaskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = fileBackedTaskManager.getSubtask(subtaskId);

        savedSubtask.setStatus(Status.DONE);
        fileBackedTaskManager.updateSubtask(subtask);

        assertNotNull(savedSubtask, "Сабтаск не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = fileBackedTaskManager.getAllSubtasks();

        assertNotNull(subtasks, "Сабтаски на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтаск.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
        assertEquals(fileBackedTaskManager.getEpic(epicId).getStatus(), savedSubtask.getStatus(), "Статусы сабтасков не совпадают");
    }

    @Test
    void deleteEpicTest() {
        fileBackedTaskManager.addNewEpic(epic);
        fileBackedTaskManager.deleteEpic(epic.getId());

        assertTrue(fileBackedTaskManager.getAllEpics().isEmpty(), "Эпик не удалился");
        assertEquals(0, fileBackedTaskManager.getAllEpics().size(), "Эпик не удалился");
    }

    @Test
    void deleteAllEpicsTest() {
        fileBackedTaskManager.addNewEpic(epic);
        fileBackedTaskManager.addNewEpic(epic);
        fileBackedTaskManager.deleteAllEpics();

        assertTrue(fileBackedTaskManager.getAllEpics().isEmpty(), "Эпики не удалились");
        assertEquals(0, fileBackedTaskManager.getAllEpics().size(), "Эпики не удалились");
    }

    @Test
    void deleteSubtaskTest() {
        fileBackedTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        Integer subtaskId = fileBackedTaskManager.addNewSubtask(subtask);

        assertEquals(1, epic.getSubtaskIds().size(), "ID сабтаски не зарегистрировался у эпика");

        fileBackedTaskManager.deleteSubtask(subtaskId);

        assertTrue(fileBackedTaskManager.getAllSubtasks().isEmpty(), "Сабтаск не удалился");
        assertEquals(0, fileBackedTaskManager.getAllSubtasks().size(), "Сабтаск не удалился");
        assertTrue(epic.getSubtaskIds().isEmpty(), "ID сабтаски не удалился из списка у эпика");
    }

    @Test
    void deleteAllSubtaskTest() {
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        fileBackedTaskManager.addNewSubtask(subtask);
        fileBackedTaskManager.addNewSubtask(subtask);
        fileBackedTaskManager.deleteAllSubtasks();

        assertTrue(fileBackedTaskManager.getAllSubtasks().isEmpty(), "Сабтаски не удалилися");
        assertEquals(0, fileBackedTaskManager.getAllSubtasks().size(), "Сабтаски не удалилися");
    }

    @Test
    void updateStatusEpicTest() {
        final int epicId = fileBackedTaskManager.addNewEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус NEW");

        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        fileBackedTaskManager.addNewSubtask(subtask);
        subtask.setStatus(Status.DONE);
        fileBackedTaskManager.updateSubtask(subtask);

        assertEquals(Status.DONE, epic.getStatus(), "Неверный статус DONE");

        Subtask subtask2 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic.getId());
        fileBackedTaskManager.addNewSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус IN_PROGRESS");
    }

    @AfterEach
    void afterEach() {
        file.deleteOnExit();
    }
}