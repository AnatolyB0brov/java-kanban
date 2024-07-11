package task;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task implements Serializable {

    private final Set<Subtask> subtasks = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void addOrUpdateSubtask(Subtask subtask) {
        if (subtasks.contains(subtask)) {
            subtasks.remove(subtask);
        }
        subtasks.add(subtask);
        updateStatus();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
    }

    public Set<Subtask> getSubtasks() {
        return new HashSet<>(subtasks);
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("Can't change status by this method");
    }

    private void updateStatus() {
        if (subtasks.isEmpty()) {
            status = Status.NEW;
        } else {
            boolean isAllNew = true;
            for (Subtask subtask : subtasks) {
                switch (subtask.getStatus()) {
                    case IN_PROGRESS -> {
                        status = Status.IN_PROGRESS;
                        return;
                    }
                    case DONE -> isAllNew = false;
                }
            }
            status = isAllNew ? Status.NEW : Status.DONE;
        }
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
