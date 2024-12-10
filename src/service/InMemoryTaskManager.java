package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int id = 0;

    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        deleteAllTasksFromHistory(tasks);
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        deleteAllTasksFromHistory(subtasks);
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllTasksFromHistory(subtasks);
        subtasks.clear();
        deleteAllTasksFromHistory(epics);
        epics.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        inMemoryHistoryManager.addInHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        inMemoryHistoryManager.addInHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        inMemoryHistoryManager.addInHistory(epic);
        return epic;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public int addNewTask(Task task) {
        ++id;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        ++id;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic != null) {
            ++id;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.addSubtaskId(id);
            updateStatusEpic(epicId);
            return id;
        }
        return -1;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            int epicId = subtask.getEpicId();
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic(epicId);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicOld = epics.get(epic.getId());
            epicOld.setTitle(epic.getTitle());
            epicOld.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteTask(int id) {
        inMemoryHistoryManager.removeFromHistory(id);
        tasks.remove(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId);
            epic.getSubtaskIds().remove(id);
            inMemoryHistoryManager.removeFromHistory(id);
            subtasks.remove(id);
            updateStatusEpic(epicId);
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subtaskIdsClone = new ArrayList<>(epic.getSubtaskIds());

            for (Integer subtaskId : subtaskIdsClone) {
                inMemoryHistoryManager.removeFromHistory(subtaskId);
                epic.getSubtaskIds().remove(subtaskId);
                subtasks.remove(subtaskId);
            }
            inMemoryHistoryManager.removeFromHistory(epicId);
            epics.remove(epicId);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    private void updateStatusEpic(int epicId) {
        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);
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

    private void deleteAllTasksFromHistory(Map<Integer, ? extends Task> tasksToDelete) {
        for (Integer keyId : tasksToDelete.keySet()) {
            inMemoryHistoryManager.removeFromHistory(keyId);
        }
    }
}
