package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.Endpoint;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        if (Objects.requireNonNull(endpoint) == Endpoint.GET) {
            handleGetHistory(exchange);
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        String responseString = gson.toJson(history);
        writeResponse(exchange, responseString, 200);
    }
}
