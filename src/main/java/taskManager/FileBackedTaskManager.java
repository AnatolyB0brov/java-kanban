package taskManager;

import exception.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File databaseFile;

    public FileBackedTaskManager(File databaseFile) {
        super();
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
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
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

    public void save() {
        FileBackedDto fileBackedDto = new FileBackedDto(getTasks(), getSubtasks(), getEpics());
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
            FileBackedDto fileBackedDto = (FileBackedDto) objectInputStream.readObject();
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            fileBackedTaskManager.setTasks(fileBackedDto.getTasks());
            fileBackedTaskManager.setSubtasks(fileBackedDto.getSubtasks());
            fileBackedTaskManager.setEpics(fileBackedDto.getEpics());
            return fileBackedTaskManager;
        } catch (IOException | ClassNotFoundException e) {
            throw new ManagerSaveException(e);
        }
    }
}
