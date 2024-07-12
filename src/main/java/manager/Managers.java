package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileBackedTaskManager(File databaseFile) {
        return new FileBackedTaskManager(databaseFile, getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
