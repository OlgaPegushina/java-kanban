package service;

import exception.ManagerValidatePriority;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int id = 0;
    protected final Set<Task> sortedTaskByTime = new TreeSet<>((task1, task2) -> {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime startTime2 = task2.getStartTime();

        if (startTime1 == null && startTime2 == null) {
            return 0; // Оба времени начала равны
        }
        if (startTime1 == null) {
            return 1; // Первое время равно null, значит оно "больше", чтобы не мешалось
        }
        if (startTime2 == null) {
            return -1; // Второе время равно null, значит оно "больше", чтобы не мешалось
        }
        return startTime1.compareTo(startTime2); // Сравнение двух непустых значений
    });

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
        sortedTaskByTime.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        deleteAllTasksFromHistory(subtasks);

        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic.getId());
            updateDurationTimeEpic(epic);
        });
        sortedTaskByTime.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllTasksFromHistory(subtasks);
        sortedTaskByTime.removeAll(subtasks.values());
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
        if (task.getStartTime() != null) {
            validateTaskPriority(task);
            sortedTaskByTime.add(task);
        }
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
            if (subtask.getStartTime() != null) {
                validateTaskPriority(subtask);
                sortedTaskByTime.add(subtask);
            }
            ++id;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.addSubtaskId(id);
            updateStatusEpic(epicId);
            updateDurationTimeEpic(epic);
            return id;
        }
        return -1;
    }

    private void addSortedAfterUpdateTask(Task task) {
        if (task.getStartTime() != null) {
            validateTaskPriority(task);
            sortedTaskByTime.remove(task);
            sortedTaskByTime.add(task);
        } else {
            sortedTaskByTime.remove(task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            addSortedAfterUpdateTask(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            addSortedAfterUpdateTask(subtask);
            int epicId = subtask.getEpicId();
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic(epicId);
            updateDurationTimeEpic(epics.get(epicId));
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
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.removeFromHistory(id);
            sortedTaskByTime.remove(tasks.get(id));
            tasks.remove(id);
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId);
            sortedTaskByTime.remove(subtasks.get(id));
            epic.getSubtaskIds().remove(id);
            inMemoryHistoryManager.removeFromHistory(id);
            subtasks.remove(id);
            updateStatusEpic(epicId);
            updateDurationTimeEpic(epic);
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            List<Integer> subtaskIdsClone = new ArrayList<>(epic.getSubtaskIds());

            subtaskIdsClone.forEach(subtaskId -> {
                inMemoryHistoryManager.removeFromHistory(subtaskId);
                epic.getSubtaskIds().remove(subtaskId);
                sortedTaskByTime.removeIf(task -> subtaskIdsClone.contains(task.getId()));
                subtasks.remove(subtaskId);
            });

            inMemoryHistoryManager.removeFromHistory(epicId);
            epics.remove(epicId);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epicSubtasks = epic.getSubtaskIds()
                    .stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());
        }
        return epicSubtasks;
    }

    private void updateStatusEpic(int epicId) {
        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);

        if (epicSubtasks.isEmpty()) {
            epics.get(epicId).setStatus(Status.NEW);
        } else {
            long countDone = epicSubtasks.stream()
                    .filter(subtask -> subtask.getStatus() == Status.DONE)
                    .count();
            long countNew = epicSubtasks.stream()
                    .filter(subtask -> subtask.getStatus() == Status.NEW)
                    .count();
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
        tasksToDelete.keySet().forEach(inMemoryHistoryManager::removeFromHistory);
    }

    private void updateDurationTimeEpic(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());

        LocalDateTime minStartTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime maxEndTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        Duration duration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);

        epic.setStartTime(minStartTime);
        epic.setEndTime(maxEndTime);
        epic.setDuration(duration);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return sortedTaskByTime;
    }

    // проверка на пересечение
    protected void validateTaskPriority(Task task) {
        if (task.getStartTime() != null) {
            boolean isValidate = sortedTaskByTime.stream()
                    .filter(sortedTask -> !sortedTask.equals(task))
                    .anyMatch(sortedTask -> task.getStartTime().isBefore(sortedTask.getEndTime()) && task.getEndTime().isAfter(sortedTask.getStartTime()));
            if (isValidate) {
                System.out.println("У задачи " + task + "\nневерно задано стартовое время или продолжительность!");
                throw new ManagerValidatePriority("Задача пересекается по времени с уже существующими. Её выполнение невозможно!");
            }
        }
    }
}
