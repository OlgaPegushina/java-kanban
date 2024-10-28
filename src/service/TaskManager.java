package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public class TaskManager {
    private final Repository repository = new Repository();
    private static int id = 0;


    //2.Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    //a.

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(repository.getTasks().values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(repository.getSubtasks().values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(repository.getEpics().values());
    }

    //b.

    public void deleteAllTasks() {
        repository.getTasks().clear();
    }

    public void deleteAllSubtasks() {
        ArrayList<Subtask> listOfSubtaskClone = new ArrayList<>(repository.getSubtasks().values());
       for (Subtask subtask : listOfSubtaskClone) {
           deleteSubtask(subtask.getId());
       }
    }

    public void deleteAllEpics() {
        deleteAllSubtasks();
        repository.getEpics().clear();
    }

    //c.

    public Task getTask(int id) {
        return repository.getTasks().get(id);
    }

    public Subtask getSubtask(int id) {
        return repository.getSubtasks().get(id);
    }

    public Epic getEpic(int id) {
        return repository.getEpics().get(id);
    }

    //d.

    public void addNewTask(Task task) {
        ++id;
        task.setId(id);
        repository.getTasks().put(id, task);
    }

    public void addNewEpic(Epic epic) {
        ++id;
        epic.setId(id);
        repository.getEpics().put(id, epic);
    }

    public void addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = repository.getEpics().get(epicId);

        if (epic != null) {
            ++id;
            subtask.setId(id);
            repository.getSubtasks().put(id, subtask);
            epic.addSubtaskId(id);
            if (epic.getStatus() == Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    //e.

    public void updateTask(Task task) {
        repository.getTasks().put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        repository.getSubtasks().put(subtask.getId(), subtask);
        epicUpdateStatus(epicId);
    }

    public void updateEpic(Epic epic) {
        Epic epicOld = repository.getEpics().get(epic.getId());
        epicOld.setTitle(epic.getTitle());
        epicOld.setDescription(epic.getDescription());
    }

    //f.

    public void deleteTask(Integer id) {
        repository.getTasks().remove(id);
    }

    public void deleteSubtask(Integer id) {
        int epicId = repository.getSubtasks().get(id).getEpicId();
        Epic epic = repository.getEpics().get(epicId);

        epic.getListOfSubtaskId().remove(id);
        repository.getSubtasks().remove(id);
        epicUpdateStatus(epicId);
    }

    public void deleteEpic(int epicId) {
        ArrayList<Integer> listOfSubtaskIdClone = new ArrayList<>(repository.getEpics().get(epicId).getListOfSubtaskId());

        for (Integer subtaskId : listOfSubtaskIdClone) {
            deleteSubtask(subtaskId);
        }
        repository.getEpics().remove(epicId);
    }

    //3.Дополнительные методы:

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for(Subtask subtask : repository.getSubtasks().values()) {
            if(subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    private void epicUpdateStatus(int epicId) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epicId);
        int countNew = 0;
        int countDone = 0;
        if (epicSubtasks.isEmpty()) {
            repository.getEpics().get(epicId).setStatus(Status.NEW);
        } else {
            for (Subtask subtask : epicSubtasks) {
                if (subtask.getStatus() == Status.IN_PROGRESS) {
                    repository.getEpics().get(epicId).setStatus(Status.IN_PROGRESS);
                    return;
                } else if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                    if (countNew != 0) {
                        repository.getEpics().get(epicId).setStatus(Status.IN_PROGRESS);
                        return;
                    } else if (countDone == epicSubtasks.size()) {
                        repository.getEpics().get(epicId).setStatus(Status.DONE);
                    }
                } else if (subtask.getStatus() == Status.NEW) {
                    countNew++;
                    if (countDone != 0) {
                        repository.getEpics().get(epicId).setStatus(Status.IN_PROGRESS);
                        return;
                    } else if (countNew == epicSubtasks.size()) {
                        repository.getEpics().get(epicId).setStatus(Status.NEW);
                    }
                }
            }
        }
    }
}
