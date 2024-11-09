package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int SIZE_HISTORY = 10;
    private final List<Task> history = new ArrayList<>(SIZE_HISTORY);

    private void removeHistoryFirst() {
        if (!history.isEmpty() && history.size() == SIZE_HISTORY) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void addInHistory(Task task) {
        if (task != null && SIZE_HISTORY != 0) {
            removeHistoryFirst();
            history.add(task);
        }
    }
}
