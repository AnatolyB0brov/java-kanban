package task;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Epic extends Task implements Serializable {

    private final HashSet<Subtask> subtasks = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void addOrUpdateSubtask(Subtask subtask) {
        if (subtasks.contains(subtask)) {
            subtasks.remove(subtask);
        }
        subtasks.add(subtask);
        updateStatus();
        updateStartTimeAndDuration();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
        updateStartTimeAndDuration();
    }

    public Set<Subtask> getSubtasks() {
        return new HashSet<>(subtasks);
    }

    @Override
    public void setStatus(Status status) {
        System.out.println("Can't change status by this method");
    }

    @Override
    public void setStartTime(LocalDateTime localDateTime) {
        System.out.println("Can't start time by this method");
    }

    private void updateStartTimeAndDuration() {
        startTime = subtasks.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(null);
        duration = subtasks.stream().map(subtask -> subtask.duration).reduce(Duration.ZERO, Duration::plus);
    }

    private void updateStatus() {
        if (subtasks.isEmpty()) {
            status = Status.NEW;
        } else {
            boolean isAllNew = true;
            boolean isAllDone = true;
            for (Subtask subtask : subtasks) {
                switch (subtask.getStatus()) {
                    case NEW -> isAllDone = false;
                    case IN_PROGRESS -> {
                        status = Status.IN_PROGRESS;
                        return;
                    }
                    case DONE -> isAllNew = false;
                }
            }
            if (isAllDone) {
                status = Status.DONE;
            } else if (isAllNew) {
                status = Status.NEW;
            } else {
                status = Status.IN_PROGRESS;
            }
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
