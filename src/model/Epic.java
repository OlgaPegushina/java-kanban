package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> listOfSubtaskId = new ArrayList<>();

    public Epic(int id, String title, String description, Status status, ArrayList<Integer> listOfSubtaskId) {
        super(id, title, description, status);
        this.listOfSubtaskId = listOfSubtaskId;
    }

    public Epic(String title, String description) {
        super(title, description);
        this.status = Status.NEW;
    }

   public void addSubtaskId(int subtaskId) {
       this.listOfSubtaskId.add(subtaskId);
   }

    public ArrayList<Integer> getListOfSubtaskId() {
        return listOfSubtaskId;
    }

    @Override
    public String toString() {
        return "\nEpic{" +
                "listOfSubtaskId=" + listOfSubtaskId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
