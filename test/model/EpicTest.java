package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    @Test
    void epicTest() {
        Epic epic1 = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        Epic epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
        epic2.setId(epic1.getId());

        assertEquals(epic1.getId(), epic2.getId(), "Id Эпиков не совпадают.");
        assertEquals(epic1, epic2, "Эпики не совпадают.");
    }
}

