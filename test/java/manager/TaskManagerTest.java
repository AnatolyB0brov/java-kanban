package manager;

import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;
    Subtask secondSubtask;
    int taskId;
    int epicId;
    int subtaskId;
    int secondSubtaskId;

    @BeforeEach
    void beforeEach() {
        taskManager = createTaskManager();
        task = new Task("Test task", "Task description", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES));
        task.setStartTime(LocalDateTime.of(2024, 10, 1, 6, 0));
        epic = new Epic("Test epic", "Epic description");
        subtask = new Subtask("Test subtask", "Subtask description", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES), epic);
        subtask.setStartTime(LocalDateTime.of(2024, 10, 1, 8, 0));
        secondSubtask = new Subtask("Test second subtask", "Second subtask description",
                Status.NEW, Duration.of(60, ChronoUnit.MINUTES), epic);
        secondSubtask.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 0));
        taskId = taskManager.createTask(task);
        epicId = taskManager.createEpic(epic);
        subtaskId = taskManager.createSubtask(subtask);
        secondSubtaskId = taskManager.createSubtask(secondSubtask);
    }

    protected abstract TaskManager createTaskManager();

}