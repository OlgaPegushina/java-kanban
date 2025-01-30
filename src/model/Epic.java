package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String title, String description) {
        super(title, description);
        this.status = Status.NEW;
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration,
                LocalDateTime endTime) {
        super(id, title, description, status, startTime, duration);
        this.endTime = endTime;
        subtaskIds = new ArrayList<>();
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskIds == null) {
            subtaskIds = new ArrayList<>(); // Инициализация если не инициализировано при http запросах, н.п. при Insomnia
        }
        if (subtaskId > 0) {
            this.subtaskIds.add(subtaskId);
        }
    }

    public List<Integer> getSubtaskIds() {
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
