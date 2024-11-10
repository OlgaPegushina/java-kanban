package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    void epicTest() {
        Subtask subtask1 = new Subtask( "Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", 1);
        Subtask subtask2 = new Subtask("Подзадача - 2",
                "Описание подзадачи - 2, эпической задачи - 1", 1);
        subtask2.setId(subtask1.getId());

        assertEquals(subtask1.getId(), subtask2.getId(), "Id Подзадач не совпадают.");
        assertEquals(subtask1, subtask2, "Подзадачи не совпадают.");
    }
}

