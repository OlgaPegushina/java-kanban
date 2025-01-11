package service;

import model.Epic;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class NewInMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    @Override
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        task = new Task("Просто задача - 1", "Описание простой задачи - 1");
        epic = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
    }

    @AfterEach
    @Override
    public void finish() {

    }
}


