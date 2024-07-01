package utils;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import task.InMemoryTaskManager;
import task.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
