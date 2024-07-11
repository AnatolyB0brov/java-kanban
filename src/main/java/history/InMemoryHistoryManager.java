package history;

import org.apache.commons.lang3.SerializationUtils;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> nodeHashMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(SerializationUtils.clone(task));
        }
    }

    @Override
    public <T extends Task> void add(List<T> taskList) {
        if (taskList != null) {
            for (Task task : taskList) {
                add(task);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(nodeHashMap.remove(id));
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        nodeHashMap.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    private List<Task> getTasks() {
        List<Task> allTasks = new ArrayList<>();
        Node<Task> headNode = head;
        while (headNode != null) {
            allTasks.add(headNode.getData());
            headNode = headNode.getNext();
        }
        return allTasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null && !nodeHashMap.isEmpty()) {
            Node<Task> prevNode = node.getPrev();
            Node<Task> nextNode = node.getNext();
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                head = nextNode;
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            } else {
                tail = prevNode;
            }
        }
    }
}
