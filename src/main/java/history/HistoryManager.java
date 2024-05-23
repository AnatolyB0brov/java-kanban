package history;

import task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    <T extends Task> void add(List<T> taskList);

    List<Task> getHistory();
}
