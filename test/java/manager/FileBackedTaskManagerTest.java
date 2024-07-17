package manager;

import exception.ManagerSaveException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    static File databaseFile;

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getFileBackedTaskManager(databaseFile);
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        databaseFile = File.createTempFile("Database-", ".txt");
    }

    @Test
    void checkIfStartTimeAndDurationRestoredFromFile() {
        LocalDateTime taskTime = LocalDateTime.of(2024, 10, 1, 6, 0);
        LocalDateTime subtaskTime = LocalDateTime.of(2024, 10, 1, 8, 0);
        TaskManager restoredTaskManager = FileBackedTaskManager.loadFromFile(databaseFile);
        assertEquals(taskTime, restoredTaskManager.getTaskById(taskId).getStartTime(),
                "Время начала задачи не восстановилось");
        assertEquals(subtaskTime, restoredTaskManager.getSubtaskById(subtaskId).getStartTime(),
                "Время начала подзадачи не восстановилось");
        assertEquals(Duration.of(60, ChronoUnit.MINUTES), restoredTaskManager.getTaskById(taskId).getDuration(),
                "Длительность задачи не восстановилась");
        assertEquals(Duration.of(60, ChronoUnit.MINUTES), restoredTaskManager.getSubtaskById(subtaskId)
                .getDuration(), "Длительность подзадачи не восстановилась");
    }

    @Test
    void checkIfHistoryRestoredFromFile() {
        taskManager.getTaskById(0);
        taskManager.getSubtaskList();
        taskManager.getEpicsList();
        List<Task> taskInHistoryList = taskManager.getHistory();
        TaskManager restoredTaskManager = FileBackedTaskManager.loadFromFile(databaseFile);
        boolean isFound;
        for (Task taskInHistory : taskInHistoryList) {
            isFound = false;
            for (Task anotherHistoryTask : restoredTaskManager.getHistory()) {
                if (checkTaskOnEquals(anotherHistoryTask, taskInHistory)) {
                    isFound = true;
                    break;
                }
            }
            assertTrue(isFound, "История " + taskInHistory.getId() + " не сохранилась в файл");
        }
    }

    @Test
    void checkIfTasksRestoredFromFile() {
        TaskManager restoredTaskManager = FileBackedTaskManager.loadFromFile(databaseFile);
        Task t = restoredTaskManager.getTaskList().getFirst();
        assertTrue(checkTaskOnEquals(t, task), "Задача не сохранилась в файл");
        boolean isFound;
        List<Subtask> subtaskList = restoredTaskManager.getSubtaskList();
        for (Subtask s : subtaskList) {
            isFound = false;
            if (checkTaskOnEquals(s, subtask) && s.getEpic().getId().equals(subtask.getEpic().getId())) {
                isFound = true;
            }
            if (checkTaskOnEquals(s, secondSubtask) && s.getEpic().getId().equals(secondSubtask.getEpic().getId())) {
                isFound = true;
            }
            assertTrue(isFound, "Подзадача " + s.getId() + " не сохранилась в файл");
        }
        isFound = false;
        Epic e = restoredTaskManager.getEpicsList().getFirst();
        if (checkTaskOnEquals(e, epic)) {
            Set<Subtask> subtasksE = e.getSubtasks();
            Set<Subtask> subtasksEpic = epic.getSubtasks();
            for (Subtask sE : subtasksE) {
                for (Subtask sEpic : subtasksEpic) {
                    if (checkTaskOnEquals(sE, sEpic) && sE.getEpic().getId().equals(sEpic.getEpic().getId())) {
                        isFound = true;
                        break;
                    }
                }
                assertTrue(isFound, "Ошибка сохранения подзадач эпика");
                isFound = false;
            }
            isFound = true;
        }
        assertTrue(isFound, "Эпик не сохранился в файл");
    }

    @Test
    void checkIfNextTaskIdRestoredFromFile() {
        TaskManager restoredTaskManager = FileBackedTaskManager.loadFromFile(databaseFile);
        int taskId = taskManager.createTask(new Task("name", "des", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES)));
        int restoredManagerTaskId = restoredTaskManager.createTask(new Task("name", "des", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES)));
        assertEquals(taskId, restoredManagerTaskId, "Генератора идентификаторов не восстановлен");
    }

    @Test
    void fileNotFoundTest() {
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(
                new File("testPath/testFile.txt")),"Если файл не найден - " +
                "должно быть выброшено исключение");
    }

    <T extends Task> boolean checkTaskOnEquals(T firstTask, T secondTask) {
        return Objects.equals(firstTask.getId(), secondTask.getId()) && firstTask.getName().equals(secondTask.getName())
                && firstTask.getDescription().equals(secondTask.getDescription())
                && firstTask.getStatus().equals(secondTask.getStatus());
    }
}