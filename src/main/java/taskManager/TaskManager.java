package taskManager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> getTaskList();

    List<Subtask> getSubtaskList();

    List<Epic> getEpicsList();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    int createTask(Task task);

    int createSubtask(Subtask subtask);

    int createEpic(Epic epic);

    boolean updateTask(Task task);

    boolean updateSubtask(Subtask subtask);

    boolean updateEpic(Epic epic);

    boolean deleteTaskById(int id);

    boolean deleteSubtaskById(int id);

    boolean deleteEpicById(int id);

    Set<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getHistory();
}
