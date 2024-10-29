package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;

    //2.Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    //a.

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //b.

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic.getId());
        }
        subtasks.clear();
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    //c.

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    //d.

    public int addNewTask(Task task) {
        ++id;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        ++id;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public Integer addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic != null) {
            ++id;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.addSubtaskId(id);
            updateStatusEpic(epicId);
        }
        return id;
    }

    //e.

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            int epicId = subtask.getEpicId();
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic(epicId);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicOld = epics.get(epic.getId());
            epicOld.setTitle(epic.getTitle());
            epicOld.setDescription(epic.getDescription());
        }
    }

    //f.

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId);

            epic.getSubtaskIds().remove(id);
            subtasks.remove(id);
            updateStatusEpic(epicId);
        }
    }

    public void deleteEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> subtaskIdsClone = new ArrayList<>(epic.getSubtaskIds());

            for (Integer subtaskId : subtaskIdsClone) {
                epic.getSubtaskIds().remove(subtaskId);
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    //3.Дополнительные методы:

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    private void updateStatusEpic(int epicId) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epicId);
        int countNew = 0;
        int countDone = 0;
        if (epicSubtasks.isEmpty()) {
            epics.get(epicId).setStatus(Status.NEW);
        } else {
            for (Subtask subtask : epicSubtasks) {
                if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                    if (countNew != 0) {
                        epics.get(epicId).setStatus(Status.IN_PROGRESS);
                        return;
                    }
                } else if (subtask.getStatus() == Status.NEW) {
                    countNew++;
                    if (countDone != 0) {
                        epics.get(epicId).setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }
            }
            if (countDone == epicSubtasks.size()) {
                epics.get(epicId).setStatus(Status.DONE);
            } else if (countNew == epicSubtasks.size()) {
                epics.get(epicId).setStatus(Status.NEW);
                } else {
                    epics.get(epicId).setStatus(Status.IN_PROGRESS);
                }
        }
    }
}
