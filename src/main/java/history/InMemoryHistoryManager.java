package history;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new LinkedList<>();

    private static final short QUEUE_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task != null) {
            history.addLast(task);
            if (history.size() > QUEUE_SIZE) {
                history.removeFirst();
            }
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
        return history;
    }
}
