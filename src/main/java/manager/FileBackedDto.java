package manager;

import history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.Serializable;
import java.util.HashMap;

public class FileBackedDto implements Serializable {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private int nextTaskId;
    private HistoryManager historyManager;

    public FileBackedDto() {
    }

    public FileBackedDto(HashMap<Integer, Task> tasks,
                         HashMap<Integer, Subtask> subtasks,
                         HashMap<Integer, Epic> epics,
                         int nextTaskId,
                         HistoryManager historyManager) {
        this.tasks = tasks;
        this.subtasks = subtasks;
        this.epics = epics;
        this.nextTaskId = nextTaskId;
        this.historyManager = historyManager;
    }

    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return new HashMap<>(subtasks);
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public int getNextTaskId() {
        return nextTaskId;
    }

    public void setNextTaskId(int nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }
}
