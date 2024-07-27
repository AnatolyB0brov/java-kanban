package manager;

import exception.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;
    Subtask secondSubtask;
    int taskId;
    Integer epicId;
    int subtaskId;
    int secondSubtaskId;

    @BeforeEach
    void beforeEach() {
        taskManager = createTaskManager();
        task = new Task("Test task", "Task description", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES));
        task.setStartTime(LocalDateTime.of(2024, 10, 1, 6, 0));
        epic = new Epic("Test epic", "Epic description");
        epicId = taskManager.createEpic(epic);
        subtask = new Subtask("Test subtask", "Subtask description", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES), epicId);
        subtask.setStartTime(LocalDateTime.of(2024, 10, 1, 8, 0));
        secondSubtask = new Subtask("Test second subtask", "Second subtask description",
                Status.NEW, Duration.of(60, ChronoUnit.MINUTES), epicId);
        secondSubtask.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 0));
        taskId = taskManager.createTask(task);

        subtaskId = taskManager.createSubtask(subtask);
        secondSubtaskId = taskManager.createSubtask(secondSubtask);
    }

    protected abstract TaskManager createTaskManager();

    @Test
    void createTask() {
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getName(), savedTask.getName(), "Названия не совпадают.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описания не совпадают.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы не совпадают.");
        final List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals(task.getName(), tasks.get(0).getName(), "Названия не совпадают.");
        assertEquals(task.getDescription(), tasks.get(0).getDescription(), "Описания не совпадают.");
        assertEquals(task.getStatus(), tasks.get(0).getStatus(), "Статусы не совпадают.");
    }

    @Test
    void checkTasksOnEqualsIfIdEquals() {
        Epic epicFromAnotherManager = new Epic("epicFromAnotherManager",
                "epicFromAnotherManager description");
        TaskManager anotherTaskManager = createTaskManager();
        Integer epicFromAnotherManagerId = anotherTaskManager.createEpic(epicFromAnotherManager);
        Subtask subtaskFromAnotherManager = new Subtask("subtaskFromAnotherManager",
                "subtaskFromAnotherManager description", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES), epicFromAnotherManagerId);
        epicFromAnotherManager.addOrUpdateSubtask(subtaskFromAnotherManager);
        Task taskFromAnotherManager = new Task("taskFromAnotherManager",
                "taskFromAnotherManager description", Status.NEW, Duration.of(60, ChronoUnit.MINUTES));
        int subtaskFromAnotherManagerId = anotherTaskManager.createSubtask(subtaskFromAnotherManager);
        int taskFromAnotherManagerId = anotherTaskManager.createTask(taskFromAnotherManager);
        assertEquals(taskId, subtaskFromAnotherManagerId, "ID задачи и подзадачи не совпадают");
        assertEquals(task, subtaskFromAnotherManager, "Задача и подзадача не совпадают");
        assertEquals(epicId, epicFromAnotherManagerId, "ID эпиков не совпадают");
        assertEquals(task, subtaskFromAnotherManager, "Задача и подзадача не совпадают");
        assertEquals(subtaskId, taskFromAnotherManagerId, "ID подзадачи и задачи не совпадают");
        assertEquals(subtask, taskFromAnotherManager, "Подзадача и задача не совпадают");
    }

    @Test
    void createAllTypeOfTasksAndCheckFields() {
        Task task1 = taskManager.getTaskById(taskId);
        Epic epic1 = taskManager.getEpicById(epicId);
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId);
        assertEquals(task, task1, "Задачи не совпадают");
        assertEquals(task.getId(), task1.getId(), "Id задач не совпадают");
        assertEquals(task.getName(), task1.getName(), "Названия задач не совпадают");
        assertEquals(task.getDescription(), task1.getDescription(), "Описания задач не совпадают");
        assertEquals(epic, epic1, "Эпики не совпадают");
        assertEquals(epic.getId(), epic1.getId(), "Id эпиков не совпадают");
        assertEquals(epic.getName(), epic1.getName(), "Названия эпиков не совпадают");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описания эпиков не совпадают");
        assertEquals(subtask, subtask1, "Подзадачи не совпадают");
        assertEquals(subtask.getId(), subtask1.getId(), "Id подзадач не совпадают");
        assertEquals(subtask.getName(), subtask1.getName(), "Названия подзадач не совпадают");
        assertEquals(subtask.getDescription(), subtask1.getDescription(), "Описания подзадач не совпадают");
        assertEquals(4, getSizeOfAllTaskInMemory(), "Количество всех созданных задач не " +
                "соответсвует действительности");
    }

    @Test
    void checkHistoryQueueAndDuplicates() {
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "История не пустая");
        for (int i = 0; i < 3; i++) {
            taskManager.getTaskById(taskId);
            assertEquals(taskId, taskManager.getHistory().getLast().getId(),
                    "Задача - не последний элемент в очереди");
            taskManager.getEpicById(epicId);
            assertEquals(epicId, taskManager.getHistory().getLast().getId(),
                    "Эпик - не последний элемент в очереди");
            taskManager.getSubtaskById(subtaskId);
            assertEquals(subtaskId, taskManager.getHistory().getLast().getId(),
                    "Подзадача - не последний элемент в очереди");
        }
        assertEquals(3, taskManager.getHistory().size(), "В истории неверное количество записей");
        history = taskManager.getHistory();
        List<Integer> idList = new ArrayList<>();
        boolean noDuplicates = true;
        for (Task taskInHistory : history) {
            if (idList.contains(taskInHistory.getId())) {
                noDuplicates = false;
                break;
            }
            idList.add(taskInHistory.getId());
        }
        assertTrue(noDuplicates, "В истории есть дубликаты");
    }

    @Test
    void checkHistoryNotModified() {
        Task task1 = taskManager.getTaskById(taskId);
        Epic epic1 = taskManager.getEpicById(epicId);
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId);
        List<Task> tasksFromManager = new ArrayList<>();
        tasksFromManager.add(task1);
        tasksFromManager.add(epic1);
        tasksFromManager.add(subtask1);
        task1.setDescription("New task description");
        epic1.setDescription("New epic description");
        subtask1.setDescription("New subtask description");
        List<Task> history = taskManager.getHistory();
        for (Task taskFromHistory : history) {
            for (Task taskFromManager : tasksFromManager) {
                if (taskFromManager.getId().equals(taskFromHistory.getId())) {
                    assertNotEquals(taskFromManager.getDescription(), taskFromHistory.getDescription(),
                            "Описание объекта с id = " + taskFromHistory.getId() + " в истории изменилось");
                }
            }
        }
    }

    @Test
    void deleteSubtaskById() {
        Epic testEpic = taskManager.getEpicById(epicId);
        int epicSizeBeforeDelete = testEpic.getSubtasks().size();
        taskManager.deleteSubtaskById(subtaskId);
        assertNotEquals(testEpic.getSubtasks().size(), epicSizeBeforeDelete);
    }

    @Test
    void deleteEpicById() {
        taskManager.deleteEpicById(epicId);
        assertEquals(0, taskManager.getEpicsList().size());
        assertEquals(0, taskManager.getSubtaskList().size(), "При удалении эпика не удалились его " +
                "подзадачи");
    }

    @Test
    void testEpicStatuses() {
        Subtask thirdSubtask = new Subtask("Test subtask 3", "Subtask description 3", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES), epicId);
        taskManager.createSubtask(thirdSubtask);
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW");
        thirdSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(thirdSubtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
        subtask.setStatus(Status.DONE);
        secondSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(secondSubtask);
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
        thirdSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(thirdSubtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void taskNotCrossing() {
        Task testTask = new Task("Test task 1", "Task description 1", Status.NEW,
                Duration.of(60, ChronoUnit.MINUTES));
        testTask.setStartTime(LocalDateTime.of(2024, 10, 1, 6, 0));
        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(testTask), "Проверка на " +
                "пересечение по времени работает неверно");
    }

    @Test
    void checkIfSubtasksHaveEpic() {
        List<Subtask> subtasks = taskManager.getSubtaskList();
        for (Subtask s : subtasks) {
            assertNotNull(s.getEpicId(), "У подзадачи с id " + s.getId() + " нет эпика");
        }
    }

    int getSizeOfAllTaskInMemory() {
        return taskManager.getTaskList().size() + taskManager.getEpicsList().size()
                + taskManager.getSubtaskList().size();
    }
}