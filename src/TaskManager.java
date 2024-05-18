import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TaskManager {
    //СПАСИБО БОЛЬШОЕ ЗА ВСЕ ПРЕДЫДУЩИЕ И ЭТО РЕВЬЮ!
    //К сожалению, после этого ревью очень много вопросов осталось.

    //Ты пишешь: "Эти поля не должны обладать модификатором static. Иначе мы не сможем создать два экземпляра этого
    // класса. Поскольку коллекции с задачами статические, то это не будет иметь смысла, а это может быть очень важно"

    //Я специально сделал static, чтобы был только один TaskManager, т.к. исходя из его задач, он очень похож
    // на сервисный класс, а коллекции tasks, subtasks, epics - это хранилки, на данный момент заменяющие БД.
    //Таким образом сервис выполняет какие-то действия с данными из БД. Для чего нам понадобится несколько таких
    //сервисов - я не знаю. Данные в БД то всё равно одни. Одна канбан доска - одна база данных, я так подумал.
    //Если мы захотим развернуть несколько канбан-досок, так просто возьмем несколько докер-контейнеров, в каждом из
    //которых будет крутиться своя доска.

    //Если бы мы уже спринг проходили, то по логике задач класса TaskManager = TaskService. В TaskService  были бы
    //репозитории для работы с Task, Subtask, Epic. Не делали же мы бы несколько TaskService по каким-то причинам.
    //Если не убедил, то уберу static в следующей итерации, т.к. еще 5ый спринт нужно успеть закрыть.
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();
    private static int nextTaskId = 0;

    public static ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public static ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    public static ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public static void deleteAllTasks() {
        tasks.clear();
    }

    public static void deleteAllSubtasks() {
        subtasks.clear();
    }

    public static void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public static Task getTaskById(int id) {
        return tasks.get(id);
    }

    public static Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public static Epic getEpicById(int id) {
        return epics.get(id);
    }


    //Ты пишешь:"А установить значение id в сам Task? Иначе это поле у всех задач будет null, так как присваивать им
    // идентификаторы мы можем только в менеджере.А исходя из твоего метода equals, все задачи будут равны"

    //Так у меня первая строка в этом методе делает(и делала, это я не сейчас поправил) setId. Или речь о другом чем-то?
    public static void createTask(Task task) {
        task.setId(TaskManager.nextTaskId);
        tasks.put(TaskManager.nextTaskId, task);
        TaskManager.nextTaskId++;
    }

    //Отвечаю на вопрос по повторному получению эпика и по новому добавлению задач в эпик
    //Например, кто-то создаст в методе main объекты класса Epic и Subtask.(назовем их epic777 и subtask555)
    //присвоит epic777 существующий в коллекции epics id (пусть это будет 7) и положит этот epic777 в subtask555 и
    // вызовет createSubtask. Но самого объекта то epic777 в коллекции нет, а в коллекции epics, у эпика с  id 7 уже
    // есть другие Subtask. И что с ними будет? Мы их перезатрем, получается. Вот и приходится получать по переданному
    // в subtask эпику эпик, который уже есть в коллекции и с ним работать.
    public static void createSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        if (epic.getId() == null || !epics.containsKey(epic.getId())) {
            createEpic(epic);
        } else {
            Set<Subtask> epicSubtasks = epic.getSubtasks();
            epic = epics.get(epic.getId());//Ты пишешь:"Зачем нам еще раз получать Epic, если мы уже работаем с ним?"
            for (Subtask s : epicSubtasks) {//Ты пишешь:"Зачем добавлять все подзадачи снова, разве они уже не
                epic.addSubtask(s);         //присутствуют в эпике?
            }
        }
        subtask.setId(TaskManager.nextTaskId);
        subtasks.put(TaskManager.nextTaskId, subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        TaskManager.nextTaskId++;
    }

    public static void createEpic(Epic epic) {
        epic.setId(TaskManager.nextTaskId);
        epics.put(TaskManager.nextTaskId, epic);
        TaskManager.nextTaskId++;
        for (Subtask subtask : epic.getSubtasks()) {
            createSubtask(subtask);
        }
    }

    private static void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isAllNew = true;
            for (Subtask subtask : epic.getSubtasks()) {
                switch (subtask.getStatus()) {
                    case IN_PROGRESS -> {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                    case DONE -> isAllNew = false;
                }
            }
            if (isAllNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.DONE);
            }
        }
    }

    public static boolean updateTask(Task task) {
        if (task.getId() != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            return true;
        }
        return false;
    }

    public static boolean updateSubtask(Subtask subtask) {
        if (subtask.getId() != null && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpic().getId());
            updateEpicStatus(epic);
            return true;

        }
        return false;
    }

    public static boolean updateEpic(Epic epic) {
        if (epic.getId() != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }

    public static boolean deleteTaskById(int id) {
        return tasks.remove(id) != null;
    }

    public static boolean deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpic().getId());
            epic.removeSubtask(subtask);
            subtasks.remove(id);
            return true;
        }
        return false;
    }

    public static boolean deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
            return true;
        }
        return false;
    }

    //Ты пишешь:"Этот метод должен получать на вход только идентификатор, иначе в нем нет смысла, можно у переменной
    // epic сразу позвать метод .getSubtasks()"

    //Согласен, что по id логичнее, но по заданию надо было именно по эпику получать.
    //И не согласен с тем, что "у переменной epic сразу позвать метод .getSubtasks()" - это будет не тоже самое
    //Опять таки, можно в main создать epic с существующим id, но без subtasks. А в коллекции epics у эипика с тем
    //же id будут subtasks. Тогда будет разный результат.
    public static Set<Subtask> getEpicSubtasks(Epic epic) {
        if (epic.getId() != null) {
            if (epics.containsKey(epic.getId())) {
                return epics.get(epic.getId()).getSubtasks();
            }
        }
        return null;
    }
}
