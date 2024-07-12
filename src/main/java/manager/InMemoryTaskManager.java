package manager;

import history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int nextTaskId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>(tasks.values());
        historyManager.add(taskList);
        return taskList;
    }

    @Override
    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>(subtasks.values());
        historyManager.add(subtaskList);
        return subtaskList;
    }

    @Override
    public List<Epic> getEpicsList() {
        List<Epic> epicList = new ArrayList<>(epics.values());
        historyManager.add(epicList);
        return epicList;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int createTask(Task task) {
        task.setId(this.nextTaskId);
        tasks.put(this.nextTaskId, task);
        this.nextTaskId++;
        return task.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask.getId() == null || !subtasks.containsKey(subtask.getId())) {
            subtask.setId(this.nextTaskId);
            subtasks.put(this.nextTaskId, subtask);
            this.nextTaskId++;
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
            epic.addOrUpdateSubtask(subtask);
        }
        return subtask.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic.getId() != null && epics.containsKey(epic.getId())) {
            return epic.getId();
        }
        epic.setId(this.nextTaskId);
        epics.put(this.nextTaskId, epic);
        this.nextTaskId++;
        for (Subtask subtask : epic.getSubtasks()) {
            createSubtask(subtask);
        }
        return epic.getId();
    }

    @Override
    public boolean updateTask(Task task) {
        if (task.getId() != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtask.getId() != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpic().getId());
            epic.addOrUpdateSubtask(subtask);
            return true;

        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epic.getId() != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTaskById(int id) {
        return tasks.remove(id) != null;
    }

    @Override
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

    @Override
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

    @Override
    public Set<Subtask> getEpicSubtasks(Epic epic) {
        if (epic.getId() != null) {
            if (epics.containsKey(epic.getId())) {
                return epics.get(epic.getId()).getSubtasks();
            }
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    protected HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    protected HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    protected void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks.clear();
        this.tasks.putAll(tasks);
    }

    protected void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks.clear();
        this.subtasks.putAll(subtasks);
    }

    protected void setEpics(HashMap<Integer, Epic> epics) {
        this.epics.clear();
        this.epics.putAll(epics);
    }

    protected int getNextTaskId() {
        return nextTaskId;
    }

    protected void setNextTaskId(int nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }
}
