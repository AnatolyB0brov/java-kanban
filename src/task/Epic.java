package task;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task{
    private final Set<Subtask> subtasks = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void addSubtask(Subtask subtask){
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask){
        subtasks.remove(subtask);
    }

    public Set<Subtask> getSubtasks() {
        return new HashSet<>(subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
