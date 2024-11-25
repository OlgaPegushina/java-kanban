package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private TaskManager manager;
    private Task task;
    private Task task2;
    private Epic epic;
    private Epic epic2;

    @BeforeEach
    void setUp() {
        task = new Task("Просто задача - 1", "Описание простой задачи - 1");
        task2 = new Task("Просто задача - 2", "Описание простой задачи - 2");
        epic = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
        manager = Managers.getDefault();
    }

    @Test
    void addTaskInHistoryTest() {
        final int taskId1 = manager.addNewTask(task);
        final int taskId2 = manager.addNewTask(task2);
        manager.getTask(taskId1);
        manager.getTask(taskId2);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "Размер истории не верный");
        assertEquals(List.of(task, task2), manager.getHistory(), "История не соответствует");
    }

    @Test
    void removeTaskFromHistoryTest() {
        final int taskId1 = manager.addNewTask(task);
        final int taskId2 = manager.addNewTask(task2);
        manager.getTask(taskId1);
        manager.getTask(taskId2);
        manager.getTask(taskId1);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "Размер истории не верный");
        assertEquals(List.of(task2, task), manager.getHistory(), "История не соответствует");
    }

    @Test
    void addEpicInHistoryTest() {
        final int epicId1 = manager.addNewEpic(epic);
        final int epicId2 = manager.addNewEpic(epic2);
        manager.getEpic(epicId1);
        manager.getEpic(epicId2);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "Размер истории не верный");
        assertEquals(List.of(epic, epic2), manager.getHistory(), "История не соответствует");
    }

    @Test
    void removeEpicInHistoryTest() {
        final int epicId1 = manager.addNewEpic(epic);
        final int epicId2 = manager.addNewEpic(epic2);
        manager.getEpic(epicId1);
        manager.getEpic(epicId2);
        manager.getEpic(epicId1);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "Размер истории не верный");
        assertEquals(List.of(epic2, epic), manager.getHistory(), "История не соответствует");
    }

    @Test
    void addSubtaskInHistoryTest() {
        final int epicId1 = manager.addNewEpic(epic);
        final int epicId2 = manager.addNewEpic(epic2);
        Subtask subtask1 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId1);
        Subtask subtask2 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 2", epicId2);
        final int subtaskId1 = manager.addNewSubtask(subtask1);
        final int subtaskId2 = manager.addNewSubtask(subtask2);

        manager.getEpic(epicId1);
        manager.getSubtask(subtaskId1);
        manager.getEpic(epicId2);
        manager.getSubtask(subtaskId2);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(4, history.size(), "Размер истории не верный");
        assertEquals(List.of(epic, subtask1, epic2, subtask2), manager.getHistory(), "История не соответствует");
    }

    @Test
    void removeSubtaskInHistoryTest() {
        final int epicId1 = manager.addNewEpic(epic);
        final int epicId2 = manager.addNewEpic(epic2);
        Subtask subtask1 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId1);
        Subtask subtask2 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 2", epicId2);
        final int subtaskId1 = manager.addNewSubtask(subtask1);
        final int subtaskId2 = manager.addNewSubtask(subtask2);

        manager.getEpic(epicId1);
        manager.getSubtask(subtaskId1);
        manager.getEpic(epicId2);
        manager.getSubtask(subtaskId2);
        manager.getSubtask(subtaskId1);
        manager.getEpic(epicId2);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(4, history.size(), "Размер истории не верный");
        assertEquals(List.of(epic, subtask2, subtask1, epic2), manager.getHistory(), "История не соответствует");
    }

    @Test
    void getHistoryTest() {
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);

        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);
        manager.getEpic(epicId);

        final List<Task> history = manager.getHistory();

        assertEquals(history.get(0), subtask, "История возвращается неверно");
        assertEquals(history.get(1), epic, "История возвращается неверно");
    }

    @Test
    void deleteAllTasksFromHistoryTest() {
        int taskId1 = manager.addNewTask(task);
        int taskId2 = manager.addNewTask(task2);
        manager.getTask(taskId1);
        manager.getTask(taskId2);

        manager.deleteAllTasks();

        List<Task> history = manager.getHistory();

        assertTrue(history.isEmpty(), "Задачи не удалились");
    }
}
