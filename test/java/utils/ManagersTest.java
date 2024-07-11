package utils;

import history.HistoryManager;
import org.junit.jupiter.api.Test;
import taskManager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void taskManagerClassAlwaysReturnsInitializedManager() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(taskManager,"TaskManager не инициализирован");
        assertNotNull(historyManager, "HistoryManager не инициализирован");
    }
}