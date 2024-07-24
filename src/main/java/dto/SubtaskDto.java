package dto;

import task.Status;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskDto extends Task {

    private Integer epicId;

    public SubtaskDto(Integer id,
                      String name,
                      String description,
                      Status status,
                      Duration duration,
                      Integer epicId,
                      LocalDateTime startTime) {
        super(name, description, status, duration);
        this.id = id;
        this.epicId = epicId;
        this.startTime = startTime;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
