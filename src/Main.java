import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        System.out.println("Создаем задачи:\n");

        Task task1 = new Task("Просто задача - 1", "Описание простой задачи - 1");
        manager.addNewTask(task1);
        Task task2 = new Task("Просто Задача - 2", "Описание простой задачи - 2");
        manager.addNewTask(task2);

        Epic epic1 = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
        manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask( "Подзадача - 1",
                "Описание подзадачи - 1, эпической задачи - 1", epic1.getId());
        manager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача - 2",
"Описание подзадачи - 2, эпической задачи - 1", epic1.getId());
        manager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask( "Подзадача - 3",
                "Описание подзадачи - 3, эпической задачи - 2", epic2.getId());
        manager.addNewSubtask(subtask3);

        System.out.println(manager.getAllTasks().toString());
        System.out.println(manager.getAllEpics().toString());
        System.out.println(manager.getAllSubtasks().toString() + '\n');

        System.out.println("Обновляем задачи и смотрим статусы:\n");

        Task task3 = new Task(task1.getId(), "Просто задача - 3", "Ставим вместо простой задачи - 1, меняем статус", Status.IN_PROGRESS);
        manager.updateTask(task3);
        Subtask subtask4 = new Subtask(subtask2.getId(), "Подзадача - 4", "Ставим вместо подзадачи - 2, эпической задачи - 1, меняем статус", Status.DONE, subtask2.getEpicId());
        manager.updateSubtask(subtask4);

        System.out.println(manager.getAllTasks().toString());
        System.out.println(manager.getAllSubtasks().toString());
        System.out.println(manager.getAllEpics().toString());

        Epic epic3 = new Epic(epic1.getId(), "Эпическая задача - 3", "Ставим вместо эпической задачи - 1", epic1.getStatus(), epic1.getListOfSubtaskId());
        manager.updateEpic(epic3);

        System.out.println(manager.getAllEpics().toString());

        Subtask subtask5 = new Subtask(subtask1.getId(), "Подзадача - 5", "Ставим вместо подзадачи - 1, эпической задачи - 3, меняем статус", Status.DONE, subtask1.getEpicId());
        manager.updateSubtask(subtask5);
        subtask3 = new Subtask(subtask3.getId(), "Подзадача - 3",
                "Меняем статус", Status.DONE, subtask3.getEpicId());
        manager.updateSubtask(subtask3);

        System.out.println(manager.getAllSubtasks().toString());
        System.out.println(manager.getAllEpics().toString());

        subtask5 = new Subtask(subtask5.getId(), "Подзадача - 5", "Меняем статус", Status.IN_PROGRESS, subtask5.getEpicId());
        manager.updateSubtask(subtask5);
        subtask4 = new Subtask(subtask4.getId(), "Подзадача - 4", "Меняем статус", Status.NEW, subtask4.getEpicId());
        manager.updateSubtask(subtask4);

        System.out.println(manager.getAllSubtasks().toString());
        System.out.println(manager.getAllEpics().toString());

        subtask5 = new Subtask(subtask5.getId(), "Подзадача - 5", "Меняем статус", Status.NEW, subtask5.getEpicId());
        manager.updateSubtask(subtask5);

        System.out.println(manager.getAllSubtasks().toString());
        System.out.println(manager.getAllEpics().toString());

        System.out.println("\nУдаляем задачи:\n");

        System.out.println("Удаляем простую задачу - 1");
        manager.deleteTask(task1.getId());
        System.out.println(manager.getAllTasks().toString());
        System.out.println("Удаляем подзадачу - 3, эпической задачи - 2");
        manager.deleteSubtask(subtask3.getId());
        System.out.println(manager.getAllSubtasks().toString() + '\n');
        System.out.println("Удаляем эпическую задачу - 1");
        manager.deleteEpic(epic1.getId());
        System.out.println(manager.getAllEpics().toString());
        System.out.println(manager.getAllSubtasks().toString() + '\n');

        /*
        System.out.println("Удаляем все эпические задачи вместе с подзадачами");
        manager.deleteAllEpics();

        System.out.println(manager.getAllEpics().toString());
        System.out.println(manager.getAllSubtasks().toString());

        //System.out.println("Удаляем все простые задачи");
        manager.deleteAllTasks();*/

        //System.out.println(manager.getAllTasks().toString());

        //System.out.println("Удаляем все подзадачи и очищаем эпики");
        //manager.deleteAllSubtasks();
        //System.out.println(manager.getAllSubtasks().toString());
        //System.out.println(manager.getAllEpics().toString());
    }
}
