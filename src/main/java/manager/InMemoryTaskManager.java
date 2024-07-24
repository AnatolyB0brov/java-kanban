package manager;

import exception.ManagerSaveException;
import history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>();

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
        removeFromPrioritizedTasks(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        removeFromPrioritizedTasks(subtasks.values());
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
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
    public int createTask(Task task) throws ManagerSaveException {
        if (isCrossing(task)) {
            throw new ManagerSaveException("Пересечение по времени с другими задачами!");
        }
        task.setId(this.nextTaskId);
        tasks.put(this.nextTaskId, task);
        addToPrioritizedTasks(task);
        this.nextTaskId++;
        return task.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) throws ManagerSaveException {
        if (isCrossing(subtask)) {
            throw new ManagerSaveException("Пересечение по времени с другими задачами!");
        }
        if (subtask.getId() == null || !subtasks.containsKey(subtask.getId())) {
            subtask.setId(this.nextTaskId);
            subtasks.put(this.nextTaskId, subtask);
            addToPrioritizedTasks(subtask);
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
    public boolean updateTask(Task task) throws ManagerSaveException {
        if (isCrossing(task)) {
            throw new ManagerSaveException("Пересечение по времени с другими задачами!");
        }
        if (task.getId() != null && tasks.containsKey(task.getId())) {
            removeFromPrioritizedTasks(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            addToPrioritizedTasks(task);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) throws ManagerSaveException {
        if (isCrossing(subtask)) {
            throw new ManagerSaveException("Пересечение по времени с другими задачами!");
        }
        if (subtask.getId() != null && subtasks.containsKey(subtask.getId())) {
            removeFromPrioritizedTasks(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            addToPrioritizedTasks(subtask);
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
        removeFromPrioritizedTasks(tasks.get(id));
        return tasks.remove(id) != null;
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpic().getId());
            epic.removeSubtask(subtask);
            removeFromPrioritizedTasks(subtasks.get(subtask.getId()));
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        historyManager.add(prioritizedTasks.stream().toList());
        return prioritizedTasks;
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

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    private <T extends Task> void removeFromPrioritizedTasks(Collection<T> taskList) {
        taskList.forEach(this::removeFromPrioritizedTasks);
    }

    private boolean isCrossing(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
/*        for (Task t : prioritizedTasks) {
            if (task.getId() != null && t.getId().equals(task.getId())) {
                continue;
            }
            if (task.getStartTime().isBefore(t.getEndTime()) && t.getStartTime().isBefore(task.getEndTime())) {
                return true;
            }
        }
        return false;*/

        return prioritizedTasks.stream()
                .filter(t -> task.getId() == null || !t.getId().equals(task.getId()))
                .anyMatch(t -> task.getStartTime().isBefore(t.getEndTime())
                        && t.getStartTime().isBefore(task.getEndTime()));
    }
}
