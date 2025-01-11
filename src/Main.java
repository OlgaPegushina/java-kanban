import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Просто задача - 1", "Описание простой задачи - 1",
                LocalDateTime.of(2024, 10, 1, 10, 0), Duration.ofHours(2));
        int task1Id = manager.addNewTask(task1);
        Task task2 = new Task("Просто Задача - 2", "Описание простой задачи - 2",
                LocalDateTime.of(2024, 10, 1, 1, 0), Duration.ofHours(2));
        int task2Id = manager.addNewTask(task2);
        Task task3 = new Task("Просто Задача - 3", "Описание простой задачи - 3",
                LocalDateTime.of(2024, 10, 1, 6, 0), Duration.ofHours(2));
        int task3Id = manager.addNewTask(task3);

        Epic epic1 = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        int epic1Id = manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
        int epic2Id = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1 эпической задачи - 1", LocalDateTime.of(2024, 10,
                1, 3, 30), Duration.ofHours(2), epic1.getId());
        int subtask1Id = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача - 2",
                "Описание подзадачи - 2 эпической задачи - 1", LocalDateTime.of(2024, 10,
                1, 0, 0), Duration.ofHours(1), epic1.getId());
        int subtask2Id = manager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача - 3",
                "Описание подзадачи - 3 эпической задачи - 2", LocalDateTime.of(2024, 10,
                1, 9, 0), Duration.ofHours(1), epic2.getId());
        int subtask3Id = manager.addNewSubtask(subtask3);

        manager.getTask(task2Id);
        manager.getTask(task1Id);
        manager.getTask(task2Id);
        manager.getEpic(epic2Id);
        manager.getSubtask(subtask2Id);
        manager.getSubtask(subtask1Id);
        manager.getEpic(epic1Id);
        manager.getSubtask(subtask3Id);
        manager.getTask(task3Id);
        manager.getTask(task1Id);
        manager.getSubtask(subtask2Id);
        manager.getSubtask(subtask1Id);

        printAllTasks(manager);
        printHistory(manager);

        manager.deleteTask(task1Id);
        System.out.println("\n Удаляем задачу - 1");
        printHistory(manager);

        manager.deleteEpic(epic1Id);
        System.out.println("\n Удаляем эпик - 1 с тремя подзадачами");
        printHistory(manager);

        manager.deleteAllTasks();
        System.out.println("\n Удаляем все задачи");
        printHistory(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
