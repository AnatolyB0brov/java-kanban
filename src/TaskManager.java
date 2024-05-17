import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class TaskManager {
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();
    private static int nextTaskId = 0;

    public static ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public static ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    public static ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public static void deleteAllTasks() {
        tasks.clear();
    }

    public static void deleteAllSubtasks() {
        subtasks.clear();
    }

    public static void deleteAllEpics() {
        epics.clear();
    }

    public static Task getTaskById(int id) {
        return tasks.get(id);
    }

    public static Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public static Epic getEpicById(int id) {
        return epics.get(id);
    }

    public static void createTask(Task task) {
        task.setId(TaskManager.nextTaskId);
        tasks.put(TaskManager.nextTaskId, task);
        TaskManager.nextTaskId++;
    }

    public static void createSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        if (epic.getId() == null || !epics.containsKey(epic.getId())) {
            createEpic(epic);
        } else {
            Set<Subtask> epicSubtasks = epic.getSubtasks();
            epic = epics.get(epic.getId());
            for (Subtask s : epicSubtasks) {
                epic.addSubtask(s);
            }
        }
        subtask.setId(TaskManager.nextTaskId);
        subtasks.put(TaskManager.nextTaskId, subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        TaskManager.nextTaskId++;
    }

    public static void createEpic(Epic epic) {
        epic.setId(TaskManager.nextTaskId);
        epics.put(TaskManager.nextTaskId, epic);
        TaskManager.nextTaskId++;
        for (Subtask subtask : epic.getSubtasks()) {
            createSubtask(subtask);
        }
    }

    private static void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isAllNew = true;
            boolean isAllDone = true;
            for (Subtask subtask : epic.getSubtasks()) {
                switch (subtask.getStatus()) {
                    case NEW -> isAllDone = false;
                    case IN_PROGRESS -> {
                        isAllNew = false;
                        isAllDone = false;
                    }
                    case DONE -> isAllNew = false;
                }
            }
            if (isAllNew) {
                epic.setStatus(Status.NEW);
            } else if (isAllDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public static boolean updateTask(Task task) {
        if (task.getId() != null) {
            if (tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
                return true;
            }
        }
        return false;
    }

    public static boolean updateSubtask(Subtask subtask) {
        if (subtask.getId() != null) {
            if (subtasks.containsKey(subtask.getId())) {
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpic().getId());
                updateEpicStatus(epic);
                return true;
            }
        }
        return false;
    }

    public static boolean updateEpic(Epic epic) {
        if (epic.getId() != null) {
            if (epics.containsKey(epic.getId())) {
                epics.put(epic.getId(), epic);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    public static boolean deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpic().getId());
            epic.removeSubtask(subtask);
            subtasks.remove(id);
            return true;
        }
        return false;
    }

    public static boolean deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Subtask subtask:epic.getSubtasks()){
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
            return true;
        }
        return false;
    }

    public static Set<Subtask> getEpicSubtasks(Epic epic) {
        if (epic.getId() != null) {
            if (epics.containsKey(epic.getId())) {
                return epics.get(epic.getId()).getSubtasks();
            }
        }
        return null;
    }
}
