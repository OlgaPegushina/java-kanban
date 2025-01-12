package service;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileAutoSave;

    public FileBackedTaskManager(File fileAutoSave) {
        super();
        this.fileAutoSave = fileAutoSave;
    }

    public static FileBackedTaskManager loadFromFile(File fileAutoSave) {
        if (!Files.exists(fileAutoSave.toPath())) {
            throw new ManagerLoadException("Файл для чтения не существует!");
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
            throw new ManagerLoadException("Ошибка чтения файла");
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

    private void addTaskFromFile(Task task) {
        if (super.id < task.getId()) {
            super.id = task.getId();
        }

        if (task instanceof Epic epic) {
            epics.put(epic.getId(), epic);
        } else if (task instanceof Subtask subtask) {
            int idEpic = subtask.getEpicId();
            Epic epic = epics.get(idEpic);

            if (epic != null) {
                validateTaskPriority(subtask);
                sortedTaskByTime.add(subtask);
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtaskId(subtask.getId());
            } else {
                throw new ManagerLoadException("В файле находятся неверные данные по подзадачам. " +
                        "Нет соответствия для Эпика. Восстановление из файла невозможно");
            }
        } else {
            validateTaskPriority(task);
            sortedTaskByTime.add(task);
            tasks.put(task.getId(), task);
        }
    }

    private String taskToString(Task task) {
        final StringBuilder sb = new StringBuilder();
        long minutes = task.getDuration().toMinutes();

        sb.append(task.getId()).append(',').append(task.getType()).append(',').append(task.getTitle());
        sb.append(',').append(task.getStatus()).append(',').append(task.getDescription()).append(',');
        sb.append(task.getStartTime()).append(',').append(minutes).append(',').append(task.getEndTime()).append(',');

        if (task instanceof Subtask subtask) {
            sb.append(subtask.getEpicId());
        }

        return sb.toString();
    }

    private void save() {
        if (!Files.exists(fileAutoSave.toPath())) {
            throw new ManagerSaveException("Файл для записи не существует!");
        }

        try (Writer fileWriter = new FileWriter(fileAutoSave, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,,endTime,epicId\n");
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
            throw new ManagerSaveException("Ошибка записи в методе save()");
        }
    }


    private Task taskFromString(String value) {
        Task res = null;
        String[] data = value.split(",");
        Status status = Status.getName(data[3]);
        LocalDateTime startDateTime = (Objects.equals(data[5], "null")) ? null : LocalDateTime.parse(data[5]);
        LocalDateTime endDateTime = (Objects.equals(data[7], "null")) ? null : LocalDateTime.parse(data[7]);
        long minutes = Long.parseLong(data[6]);

        TypeTask typeTask = TypeTask.valueOf(data[1]);

        if (TypeTask.SUBTASK.equals(typeTask)) {
            res = new Subtask(Integer.parseInt(data[0]), data[2], data[4], status, startDateTime,
                    Duration.ofMinutes(minutes), Integer.parseInt(data[8]));
        } else if (TypeTask.EPIC.equals(typeTask)) {
            res = new Epic(Integer.parseInt(data[0]), data[2], data[4], status, startDateTime,
                    Duration.ofMinutes(minutes), endDateTime);
        } else if (TypeTask.TASK.equals(typeTask)) {
            res = new Task(Integer.parseInt(data[0]), data[2], data[4], status, startDateTime,
                    Duration.ofMinutes(minutes));
        }
        return res;
    }

    public static void main(String[] args) {
        Path path = Paths.get("file_auto_save_task.csv");
        File file = new File(String.valueOf(path));
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);

        Task task1 = new Task("Просто задача - 1", "Описание простой задачи - 1",
                LocalDateTime.of(2024, 10, 1, 10, 0), Duration.ofHours(2));
        backedTaskManager.addNewTask(task1);
        Task task2 = new Task("Просто Задача - 2", "Описание простой задачи - 2",
                LocalDateTime.of(2024, 10, 1, 1, 0), Duration.ofHours(2));
        backedTaskManager.addNewTask(task2);
        Task task3 = new Task("Просто Задача - 3", "Описание простой задачи - 3",
                LocalDateTime.of(2024, 10, 1, 6, 0), Duration.ofHours(2));
        backedTaskManager.addNewTask(task3);

        Epic epic1 = new Epic("Эпическая задача - 1",
                "Описание эпической задачи - 1");
        backedTaskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпическая задача - 2",
                "Описание эпической задачи - 2");
        backedTaskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача - 1",
                "Описание подзадачи - 1 эпической задачи - 1", LocalDateTime.of(2024, 10,
                1, 3, 30), Duration.ofHours(2), epic1.getId());
        backedTaskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача - 2",
                "Описание подзадачи - 2 эпической задачи - 1", LocalDateTime.of(2024, 10,
                1, 0, 0), Duration.ofHours(1), epic1.getId());
        backedTaskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача - 3",
                "Описание подзадачи - 3 эпической задачи - 2", LocalDateTime.of(2024, 10,
                1, 9, 0), Duration.ofHours(1), epic2.getId());
        backedTaskManager.addNewSubtask(subtask3);


        backedTaskManager.deleteTask(task2.getId());
        backedTaskManager.getSubtask(subtask3.getId()).setStatus(Status.DONE);
        backedTaskManager.updateSubtask(subtask3);

        System.out.println("Отсортированный список 1:\n" + backedTaskManager.getPrioritizedTasks());

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

        System.out.println("Отсортированный список 2:\n" + backedTaskManager2.getPrioritizedTasks());
    }
}
