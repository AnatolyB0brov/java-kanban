package taskManager;

import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import manager.Managers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    TaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;
    Subtask secondSubtask;
    int taskId;
    int epicId;
    int subtaskId;
    int secondSubtaskId;
    File databaseFile;

    @BeforeEach
    void beforeEach() throws IOException {
        databaseFile = File.createTempFile("Database-", ".txt");
        taskManager = Managers.getFileBackedTaskManager(databaseFile);
        task = new Task("Test task", "Task description", Status.NEW);
        epic = new Epic("Test epic", "Epic description");
        subtask = new Subtask("Test subtask", "Subtask description", Status.NEW, epic);
        secondSubtask = new Subtask("Test second subtask", "Second subtask description",
                Status.NEW, epic);
        taskId = taskManager.createTask(task);
        epicId = taskManager.createEpic(epic);
        subtaskId = taskManager.createSubtask(subtask);
        secondSubtaskId = taskManager.createSubtask(secondSubtask);
    }

    @Test
    void checkIfFileBackedTaskManagerSuccessfullyRestoredFromFile() {
        TaskManager anotherTaskManager = FileBackedTaskManager.loadFromFile(databaseFile);
        Task t = anotherTaskManager.getTaskList().getFirst();
        assertTrue(checkTaskOnEquals(t, task), "Задача не сохранилась в файл");
        boolean isFound;
        List<Subtask> subtaskList = anotherTaskManager.getSubtaskList();
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
        Epic e = anotherTaskManager.getEpicsList().getFirst();
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

    <T extends Task> boolean checkTaskOnEquals(T firstTask, T secondTask) {
        return Objects.equals(firstTask.getId(), secondTask.getId()) && firstTask.getName().equals(secondTask.getName())
                && firstTask.getDescription().equals(secondTask.getDescription())
                && firstTask.getStatus().equals(secondTask.getStatus());
    }
}