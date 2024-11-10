package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void taskTest() {
        Task task1 = new Task("Просто задача - 1", "Описание простой задачи - 1");
        Task task2 = new Task("Просто задача - 2", "Описание простой задачи - 1");
        task2.setId(task1.getId());

        assertEquals(task1.getId(), task2.getId(), "Id Задач не совпадают.");
        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}