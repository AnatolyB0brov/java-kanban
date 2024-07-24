package manager;

import exception.ManagerSaveException;
import history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File databaseFile;

    public FileBackedTaskManager(File databaseFile, HistoryManager historyManager) {
        super(historyManager);
        this.databaseFile = databaseFile;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean isDone = super.updateTask(task);
        save();
        return isDone;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean isDone = super.updateSubtask(subtask);
        save();
        return isDone;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean isDone = super.updateEpic(epic);
        save();
        return isDone;
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean isDone = super.deleteTaskById(id);
        save();
        return isDone;
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        boolean isDone = super.deleteSubtaskById(id);
        save();
        return isDone;
    }

    @Override
    public boolean deleteEpicById(int id) {
        boolean isDone = super.deleteEpicById(id);
        save();
        return isDone;
    }

    @Override
    public List<Task> getTaskList() {
        List<Task> taskList = super.getTaskList();
        save();
        return taskList;
    }

    @Override
    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = super.getSubtaskList();
        save();
        return subtaskList;
    }

    @Override
    public List<Epic> getEpicsList() {
        List<Epic> epicList = super.getEpicsList();
        save();
        return epicList;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    public void save() throws ManagerSaveException {
        FileBackedDto fileBackedDto = new FileBackedDto(getTasks(), getSubtasks(), getEpics(), getNextTaskId(),
                getHistoryManager());
        try (FileOutputStream fileOutputStream = new FileOutputStream(databaseFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(fileBackedDto);
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object obj = objectInputStream.readObject();
            if (obj instanceof FileBackedDto fileBackedDto) {
                FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file,
                        fileBackedDto.getHistoryManager());
                fileBackedTaskManager.setTasks(fileBackedDto.getTasks());
                fileBackedTaskManager.setSubtasks(fileBackedDto.getSubtasks());
                fileBackedTaskManager.setEpics(fileBackedDto.getEpics());
                fileBackedTaskManager.setNextTaskId(fileBackedDto.getNextTaskId());
                return fileBackedTaskManager;
            } else {
                throw new ManagerSaveException("Неверный тип объекта в файле");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new ManagerSaveException(e);
        }
    }
}
