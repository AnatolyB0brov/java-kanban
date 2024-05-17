import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {

        Task buyBread = new Task( "Buy bread","Go to the store and buy bread", Status.NEW);
        Task buyMilk = new Task( "Buy milk","Go to the store and buy milk", Status.NEW);
        Epic relocate = new Epic("Relocate", "Relocate in Moscow");
        Epic learnEnglish = new Epic("Learn English", "Learn English language");
        Subtask buyTicket = new Subtask("Buy ticket", "Buy a train ticket to Moscow",
                Status.IN_PROGRESS, relocate);
        Subtask rentHousing = new Subtask("Rent housing", "Rent housing in Moscow", Status.NEW,
                relocate);
        Subtask englishCourses = new Subtask("English courses", "Sign up for English courses",
                Status.NEW, learnEnglish);
        TaskManager.createTask(buyBread);
        TaskManager.createTask(buyMilk);
        TaskManager.createSubtask(buyTicket);
        TaskManager.createSubtask(rentHousing);
        TaskManager.createSubtask(englishCourses);
        printTasks("Before status update");
        buyBread.setStatus(Status.DONE);
        TaskManager.updateTask(buyBread);
        englishCourses.setStatus(Status.DONE);
        TaskManager.updateSubtask(englishCourses);
        printTasks("After status update");
        TaskManager.deleteTaskById(1);
        TaskManager.deleteSubtaskById(3);
        TaskManager.deleteEpicById(2);
        printTasks("After delete");
    }

    public static void printTasks(String title){
        System.out.println(title);
        System.out.println(TaskManager.getTaskList());
        System.out.println(TaskManager.getSubtaskList());
        System.out.println(TaskManager.getEpicsList());
        System.out.println();
        System.out.println();
        System.out.println();
    }
}
