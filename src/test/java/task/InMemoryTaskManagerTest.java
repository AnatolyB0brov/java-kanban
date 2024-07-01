package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Managers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

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
        taskManager = Managers.getDefault();
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
    void checkTasksOnEqualsIfIdEqualsTest() {
        Epic epicFromAnotherManager = new Epic("epicFromAnotherManager",
                "epicFromAnotherManager description");
        Subtask subtaskFromAnotherManager = new Subtask("subtaskFromAnotherManager",
                "subtaskFromAnotherManager description", Status.NEW,
                epicFromAnotherManager);
        epicFromAnotherManager.addOrUpdateSubtask(subtaskFromAnotherManager);
        Task taskFromAnotherManager = new Task("taskFromAnotherManager",
                "taskFromAnotherManager description", Status.NEW);
        TaskManager anotherTaskManager = Managers.getDefault();
        int epicFromAnotherManagerId = anotherTaskManager.createEpic(epicFromAnotherManager);
        int subtaskFromAnotherManagerId = anotherTaskManager.createSubtask(subtaskFromAnotherManager);
        int taskFromAnotherManagerId = anotherTaskManager.createTask(taskFromAnotherManager);

        assertEquals(taskId, epicFromAnotherManagerId, "ID задачи и эпика не совпадают");
        assertEquals(task, epicFromAnotherManager, "Задача и эпик не совпадают");
        assertEquals(epicId, subtaskFromAnotherManagerId, "ID эпика и подзадачи не совпадают");
        assertEquals(epic, subtaskFromAnotherManager, "Эпик и подзадача не совпадают");

        assertEquals(subtaskId, taskFromAnotherManagerId, "ID подзадачи и задачи не совпадают");
        assertEquals(subtask, taskFromAnotherManager, "Подзадача и задача не совпадают");
    }

    @Test
    void createAllTypeOfTasksAndCheckFieldsTest() {
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
    void checkHistoryTest() {
        for (int i = 0; i < 3; i++) {
            Task task1 = taskManager.getTaskById(taskId);
            assertEquals(taskId, taskManager.getHistory().getLast().getId(),
                    "Задача - не последний элемент в очереди");
            Epic epic1 = taskManager.getEpicById(epicId);
            assertEquals(epicId, taskManager.getHistory().getLast().getId(),
                    "Эпик - не последний элемент в очереди");
            Subtask subtask1 = taskManager.getSubtaskById(subtaskId);
            assertEquals(subtaskId, taskManager.getHistory().getLast().getId(),
                    "Подзадача - не последний элемент в очереди");
        }
        assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void checkHistoryNotModifiedTest() {
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
    void deleteSubtaskByIdTest() {
        Epic testEpic = taskManager.getEpicById(epicId);
        int epicSizeBeforeDelete = testEpic.getSubtasks().size();
        taskManager.deleteSubtaskById(subtaskId);
        assertNotEquals(testEpic.getSubtasks().size(),epicSizeBeforeDelete);
    }

    @Test
    void deleteEpicByIdTest() {
        taskManager.deleteEpicById(epicId);
        assertEquals(0,taskManager.getEpicsList().size());
        assertEquals(0,taskManager.getSubtaskList().size(),"При удалении эпика не удалились его " +
                "подзадачи");
    }

    int getSizeOfAllTaskInMemory() {
        return taskManager.getTaskList().size() + taskManager.getEpicsList().size()
                + taskManager.getSubtaskList().size();
    }
}