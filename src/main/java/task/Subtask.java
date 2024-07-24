package task;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.time.Duration;

public class Subtask extends Task implements Serializable {

    private final Integer epicId;

    public Subtask(String name, String description, Status status, Duration duration, Integer epicId) {
        super(name, description, status, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epic=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
