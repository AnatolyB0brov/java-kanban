package task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    //Пришлось этот метод переписать вот так. Раньше я не использовал сontains и remove, но коллекция subtasks
    //была HashSet. Затем я заметил, что при добавлении в HashSet объекта с id = null, а затем, если у этого объекта
    //изменить id = 1, например, то в дальнейшем, при добавлении другого объекта(обновлении объекта) с id = 1 возникает
    //дублирование объектов. Т.е. метод equals у них не вызывается. Он лишь считает хэш при первоначальном добавлении.
    // Разумеется, что изначальный хэш у объекта с id=null другой. Если я поле объекта меняю на id=1, хеш не
    // пересчитывается. В итоге объекты, равные по equals, для HashSet всё равно разные.
    // HashSet вообще не вызывал equals ни при каких обстоятельствах, даже когда я не менял поле id, а сразу добавлял
    // объект с id = 1, а затем обновлял объект с id = 1, то equals не вызывался, но дублирования не было, т.к., как я
    // понимаю, хешкоды были одинаковые. Возможно, я сделал неверные выводы, но с сontains и remove всё работает верно,
    // а с обычным HashSet возникают дубли, если поле id поменялось в процессе работы с null на реальный идентификатор,
    // а затем объект попытались обновить через addOrUpdateSubtask. Попытался объяснить, но сумбурно получилось.
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
            if (isAllNew) {
                status = Status.NEW;
            } else {
                status = Status.DONE;
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
