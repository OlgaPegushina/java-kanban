package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    void TaskManagerGetDefaultTest() {
        final TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Объект TaskManager не создан");
    }

    @Test
    void HistoryManagerGetDefaultHistory() {
        final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertNotNull(inMemoryHistoryManager, "Объект HistoryManager не создан");
    }
}
