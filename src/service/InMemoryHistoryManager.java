package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head = null;
    private Node tail = null;
    private final Map<Integer, Node> listHistory = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void addInHistory(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node (task, null, this.tail);
        if (this.tail == null) {
            this.head = newNode;
        } else {
            tail.setNext(newNode);
        }
        this.tail = newNode;
        removeFromHistory(task.getId());
        listHistory.put(task.getId(), newNode);
    }

    @Override
    public void removeFromHistory(int id) {
        if (listHistory.containsKey(id)) {
            removeNode(listHistory.get(id));
        }
    }

    private List<Task> getTasks() {
        List<Task> listHistoryRes = new ArrayList<>();
        Node node = head;
        while (node != null) {
            listHistoryRes.add(node.getItem());
            node = node.getNext();
        }
        return listHistoryRes;
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node nodeNext = node.getNext();
            Node nodePrev = node.getPrev();
            if (nodePrev == null) {
                head = nodeNext;
                head.setPrev(null);
            } else {
                nodePrev.setNext(nodeNext);
            }
            if (nodeNext == null) {
                tail = nodePrev;
                tail.setNext(null);
            } else {
                nodeNext.setPrev(nodePrev);
            }
        }
    }
}
