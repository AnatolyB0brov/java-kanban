import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task buyBread = new Task("Buy bread", "Go to the store and buy bread", Status.NEW);
        Task buyMilk = new Task("Buy milk", "Go to the store and buy milk", Status.NEW);
        Epic relocate = new Epic("Relocate", "Relocate in Moscow");
        Epic learnEnglish = new Epic("Learn English", "Learn English language");
        Subtask buyTicket = new Subtask("Buy ticket", "Buy a train ticket to Moscow",
                Status.IN_PROGRESS, relocate);
        Subtask rentHousing = new Subtask("Rent housing", "Rent housing in Moscow", Status.NEW,
                relocate);
        Subtask englishCourses = new Subtask("English courses", "Sign up for English courses",
                Status.NEW, learnEnglish);
        taskManager.createTask(buyBread);
        taskManager.createTask(buyMilk);
        taskManager.createSubtask(buyTicket);
        taskManager.createSubtask(rentHousing);
        taskManager.createSubtask(englishCourses);
        printTasks("Before status update", taskManager);
        buyBread.setStatus(Status.DONE);
        taskManager.updateTask(buyBread);
        englishCourses.setStatus(Status.DONE);
        taskManager.updateSubtask(englishCourses);
        printTasks("After status update", taskManager);
        taskManager.deleteTaskById(1);
        taskManager.deleteSubtaskById(3);
        taskManager.deleteEpicById(2);
        printTasks("After delete", taskManager);
    }

    public static void printTasks(String title, TaskManager taskManager) {
        System.out.println(title);
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicsList());
        System.out.println();
        System.out.println();
        System.out.println();
    }
}
