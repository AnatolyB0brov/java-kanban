package task;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    //Ты пишешь:" Модификатор final не имеет смысла использовать у метода, он ни на что в данном случае не влияет"
    //Он влияет на то, что я запрещаю его переопределение в классах - потомках. А нужно это исходя из задания.
    //В задании есть следующие строки

/*    Из описания задачи видно, что эпик не управляет своим статусом самостоятельно. Это значит:
            1) Пользователь не должен иметь возможности поменять статус эпика самостоятельно.*/

    //Получается, я должен сделать недоступным вызов setStatus у эпика, а как это сделать, не запретив наследование -
    //я не знаю
    public final void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Task otherTask = (Task) obj;
        return otherTask.id == this.id;
    }

    @Override
    public final int hashCode() {
        return id * 31;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
