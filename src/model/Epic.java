package model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public Epic(String title, String description) {
        super(title, description);
        this.status = Status.NEW;
    }

   public void addSubtaskId(int subtaskId) {
       this.subtaskIds.add(subtaskId);
   }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "\nEpic{" +
                "subtaskIds=" + subtaskIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
