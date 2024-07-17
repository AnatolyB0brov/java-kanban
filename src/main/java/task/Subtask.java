package task;

import java.io.Serializable;
import java.time.Duration;

public class Subtask extends Task implements Serializable {

    private final Epic epic;

    public Subtask(String name, String description, Status status, Duration duration, Epic epic) {
        super(name, description, status, duration);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epic=" + epic.getName() +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
