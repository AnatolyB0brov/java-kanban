import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int nextTaskId = 0;

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createTask(Task task) {
        task.setId(this.nextTaskId);
        tasks.put(this.nextTaskId, task);
        this.nextTaskId++;
    }

    public void createSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        if (epic.getId() == null || !epics.containsKey(epic.getId())) {
            createEpic(epic);
        } else {
            Set<Subtask> epicSubtasks = epic.getSubtasks();
            epic = epics.get(epic.getId());
            for (Subtask s : epicSubtasks) {
                epic.addOrUpdateSubtask(s);
            }
        }
        subtask.setId(this.nextTaskId);
        subtasks.put(this.nextTaskId, subtask);
        epic.addOrUpdateSubtask(subtask);
        this.nextTaskId++;
    }

    public void createEpic(Epic epic) {
        epic.setId(this.nextTaskId);
        epics.put(this.nextTaskId, epic);
        this.nextTaskId++;
        for (Subtask subtask : epic.getSubtasks()) {
            createSubtask(subtask);
        }
    }

    public boolean updateTask(Task task) {
        if (task.getId() != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return true;
        }
        return false;
    }

    public boolean updateSubtask(Subtask subtask) {
        if (subtask.getId() != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpic().getId());
            epic.addOrUpdateSubtask(subtask);
            return true;

        }
        return false;
    }

    public boolean updateEpic(Epic epic) {
        if (epic.getId() != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }

    public boolean deleteTaskById(int id) {
        return tasks.remove(id) != null;
    }

    public boolean deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpic().getId());
            epic.removeSubtask(subtask);
            subtasks.remove(id);
            return true;
        }
        return false;
    }

    public boolean deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
            return true;
        }
        return false;
    }

    public Set<Subtask> getEpicSubtasks(Epic epic) {
        if (epic.getId() != null) {
            if (epics.containsKey(epic.getId())) {
                return epics.get(epic.getId()).getSubtasks();
            }
        }
        return null;
    }
}
