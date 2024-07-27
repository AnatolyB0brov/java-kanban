package utils;

import history.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    public void taskManagerClassAlwaysReturnsInitializedManager() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(taskManager,"TaskManager не инициализирован");
        assertNotNull(historyManager, "HistoryManager не инициализирован");
    }
}