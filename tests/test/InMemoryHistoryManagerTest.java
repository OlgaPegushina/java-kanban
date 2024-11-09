package test;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
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
    void addTaskInHistory() {
        final int taskId = manager.addNewTask(task);

        manager.getTask(taskId);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addEpicInHistory() {
        final int epicId = manager.addNewEpic(epic);

        manager.getEpic(epicId);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addSubtaskInHistory() {
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);

        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);

        final List<Task> history = manager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
    }

    @Test
    void getHistory() {
        final int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epicId);
        final int subtaskId = manager.addNewSubtask(subtask);

        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);

        final List<Task> history = manager.getHistory();

        assertEquals(history.get(0), epic, "История возвращается неверно");
        assertEquals(history.get(1), subtask, "История возвращается неверно");
    }
}
