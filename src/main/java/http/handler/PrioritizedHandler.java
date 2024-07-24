package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.Endpoint;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.Objects;
import java.util.TreeSet;


public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        if (Objects.requireNonNull(endpoint) == Endpoint.GET) {
            handleGetPrioritizedTasks(exchange);
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String responseString = gson.toJson(prioritizedTasks);
        writeResponse(exchange, responseString, 200);
    }
}
