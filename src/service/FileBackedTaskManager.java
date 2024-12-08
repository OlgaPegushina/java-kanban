package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileAutoSave;

    public FileBackedTaskManager(File fileAutoSave) {
        super();
        this.fileAutoSave = fileAutoSave;
    }

    public static FileBackedTaskManager loadFromFile(File fileAutoSave) {
        if (!Files.exists(fileAutoSave.toPath())) {
            throw new ManagerSaveException("Файл для чтения не существует");
        }

        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(fileAutoSave);

        try (BufferedReader reader = Files.newBufferedReader(fileAutoSave.toPath())) {
            String line = reader.readLine();
            while (reader.ready()) {
                line = reader.readLine();
                if (line.isBlank()) {
                    break;
                }
                Task task = backedTaskManager.taskFromString(line);
                backedTaskManager.addTaskFromFile(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return backedTaskManager;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void updateStatusEpic(int epicId) {
        super.updateStatusEpic(epicId);
        save();
    }

    private void addTaskFromFile(Task task) throws ManagerSaveException {
        if (super.id < task.getId()) {
            super.id = task.getId();
        }

        if (task instanceof Epic epic) {
            epics.put(epic.getId(), epic);
        } else if (task instanceof Subtask subtask) {
            int idEpic = subtask.getEpicId();
            Epic epic = epics.get(idEpic);

            if (epic != null) {
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtaskId(subtask.getId());
            } else {
                throw new ManagerSaveException("В файле находятся неверные данные по подзадачам. " +
                        "Нет соответсвия для Эпика. Восстановление из файла невозможно");
            }
        } else {
            tasks.put(task.getId(), task);
        }
    }

    public String taskToString(Task task) {
        final StringBuilder sb = new StringBuilder();

        sb.append(task.getId()).append(',').append(task.getType()).append(',').append(task.getTitle());
        sb.append(',').append(task.getStatus()).append(',').append(task.getDescription()).append(',');

        if (task.getType() == TypeTask.SUBTASK) {
            sb.append(((Subtask) task).getEpicId());
        }

        return sb.toString();
    }

    private void save() throws ManagerSaveException {
        if (!Files.exists(fileAutoSave.toPath())) {
            throw new ManagerSaveException("Файл для записи не существует!");
        }

        try (Writer fileWriter = new FileWriter(fileAutoSave, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                fileWriter.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                fileWriter.write(taskToString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в методе save()");
        }
    }


    public Task taskFromString(String value) {
        Task res = null;
        String[] data = value.split(",");
        Status status = switch (data[3]) {
            case "Новый" -> Status.NEW;
            case "В процессе" -> Status.IN_PROGRESS;
            case "Выполнено" -> Status.DONE;
            default -> throw new IllegalStateException("Unexpected value: " + data[3]);
        };
        if (TypeTask.SUBTASK.equals(TypeTask.valueOf(data[1]))) {
            res = new Subtask(Integer.parseInt(data[0]), data[2], data[4], status, Integer.parseInt(data[5]));
        } else if (TypeTask.EPIC.equals(TypeTask.valueOf(data[1]))) {
            res = new Epic(Integer.parseInt(data[0]), data[2], data[4], status);
        } else if (TypeTask.TASK.equals(TypeTask.valueOf(data[1]))) {
            res = new Task(Integer.parseInt(data[0]), data[2], data[4], status);
        }
        return res;
    }

    public static void main(String[] args) {
        Path path = Paths.get("file_auto_save_task.csv");
        File file = new File(String.valueOf(path));
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);

        Task task1 = new Task("Просто задача - 1", "Описание простой задачи - 1");
        backedTaskManager.addNewTask(task1);
        Task task2 = new Task("Просто Задача - 2", "Описание простой задачи - 2");
        backedTaskManager.addNewTask(task2);
        Task task3 = new Task("Просто Задача - 3", "Описание простой задачи - 3");
        backedTaskManager.addNewTask(task3);

        Epic epic1 = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        backedTaskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
        backedTaskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1 эпической задачи - 1", epic1.getId());
        backedTaskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача - 2",
                "Описание подзадачи - 2 эпической задачи - 1", epic1.getId());
        backedTaskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача - 3",
                "Описание подзадачи - 3 эпической задачи - 2", epic2.getId());
        backedTaskManager.addNewSubtask(subtask3);

        backedTaskManager.deleteTask(task2.getId());
        backedTaskManager.deleteEpic(epic1.getId());
        backedTaskManager.getSubtask(subtask3.getId()).setStatus(Status.DONE);
        backedTaskManager.updateSubtask(subtask3);

        Path path2 = Paths.get("file_auto_save_task.csv");
        File file2 = new File(String.valueOf(path2));

        System.out.println("Считываем из файла");

        FileBackedTaskManager backedTaskManager2 = loadFromFile(file2);

        System.out.println("Задачи");
        System.out.println(backedTaskManager2.getAllTasks());
        System.out.println("Эпики");
        System.out.println(backedTaskManager2.getAllEpics());
        System.out.println("Подзадачи");
        System.out.println(backedTaskManager2.getAllSubtasks());
    }
}