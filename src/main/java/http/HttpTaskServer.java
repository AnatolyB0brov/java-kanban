package http;

import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Scanner;

public class HttpTaskServer {
    private static HttpServer httpServer;

    private static final int PORT = 8080;


    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        createAndStartServer(taskManager);
        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (!Objects.equals(input, "exit")) {
            System.out.println("Для остановки введите exit");
            input = scanner.next();
        }
        stopServer();
    }

    public static void createAndStartServer(TaskManager taskManager) throws IOException {
        if (httpServer != null) {
            stopServer();
        }
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("Сервер запущен на " + PORT + " порту.");
    }

    public static void stopServer() {
        httpServer.stop(0);
    }
}
