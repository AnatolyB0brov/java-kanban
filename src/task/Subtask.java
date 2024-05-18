package task;

public class Subtask extends Task{

    private final Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    //В ревью ты написал "Зачем нам этот метод, если мы можем использовать просто setStatus"
    //Я не могу использовать setStatus, т.к. он это метод
    //родителя, а в там он final. Почему он final - ответил в классе Task
    public void setSubtaskStatus(Status status) {
        super.setStatus(status);
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
