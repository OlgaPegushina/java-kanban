import model.Epic;
import model.Subtask;
import model.Task;

import service.Managers;
import service.TaskManager;


public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Просто задача - 1", "Описание простой задачи - 1");
        int task1Id = manager.addNewTask(task1);
        Task task2 = new Task("Просто Задача - 2", "Описание простой задачи - 2");
        int task2Id = manager.addNewTask(task2);

        Epic epic1 = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        int epic1Id = manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
        int epic2Id = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic1.getId());
        int subtask1Id = manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача - 2",
"Описание подзадачи - 2, эпической задачи - 1", epic1.getId());
        int subtask2Id = manager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача - 3",
                "Описание подзадачи - 3, эпической задачи - 2", epic2.getId());
        int subtask3Id = manager.addNewSubtask(subtask3);

        manager.getTask(2);
        manager.getTask(1);
        manager.getTask(2);
        manager.getEpic(4);
        manager.getSubtask(6);
        manager.getSubtask(5);
        manager.getEpic(3);
        manager.getSubtask(7);
        manager.getTask(1);
        manager.getTask(1);
        manager.getSubtask(6);
        manager.getSubtask(5);

        printAllTasks(manager);
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

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
