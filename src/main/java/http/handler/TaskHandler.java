package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import http.Endpoint;
import http.handler.BaseHttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET: {
                handleGetAllTasks(exchange);
                break;
            }
            case GET_BY_ID: {
                handleGetTaskById(exchange);
                break;
            }
            case CREATE_OR_UPDATE: {
                handleCreateOrUpdateTask(exchange);
                break;
            }
            case DELETE: {
                handleDeleteAllTasks(exchange);
                break;
            }
            case DELETE_BY_ID: {
                handleDeleteTaskById(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> taskList = taskManager.getTaskList();
        String responseString = gson.toJson(taskList);
        writeResponse(exchange, responseString, 200);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                code = 404;
                responseString = "Задача с идентификатором " + id + " не найдена";
            } else {
                responseString = gson.toJson(task);
                code = 200;
            }
        } catch (NumberFormatException e) {
            code = 400;
            responseString = "Некорректный идентификатор задачи";
        }
        writeResponse(exchange, responseString, code);
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String responseString;
        int code;
        Optional<Task> optionalTask = parseTask(exchange);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            try {
                if (task.getId() == null) {
                    createTask(exchange, task);
                } else {
                    updateTask(exchange, task);
                }
                return;
            } catch (ManagerSaveException e) {
                code = 400;
                responseString = "Ошибка добавления задачи. " + e.getMessage();
            }
        } else {
            code = 400;
            responseString = "Объект для создания сформирован некорректно";
        }
        writeResponse(exchange, responseString, code);
    }

    private void createTask(HttpExchange exchange, Task task) throws IOException {
        String responseString;
        int code;
        try {
            int id = taskManager.createTask(task);
            task.setId(id);
            responseString = gson.toJson(task);
            code = 200;
        } catch (ManagerSaveException e) {
            code = 400;
            responseString = "Ошибка добавления задачи. " + e.getMessage();
        }
        writeResponse(exchange, responseString, code);
    }

    private void updateTask(HttpExchange exchange, Task task) throws IOException {
        String responseString;
        int code;
        try {
            if (!taskManager.updateTask(task)) {
                code = 400;
                responseString = "Ошибка обновления задачи с идентификатором " + task.getId();
            } else {
                responseString = "Успешное обновление";
                code = 201;
            }
        } catch (ManagerSaveException e) {
            code = 400;
            responseString = "Ошибка обновления задачи. " + e.getMessage();
        }
        writeResponse(exchange, responseString, code);
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        String responseString = "Успешное удаление.";
        writeResponse(exchange, responseString, 201);
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            if (!taskManager.deleteTaskById(id)) {
                code = 404;
                responseString = "Задача с идентификатором " + id + " не найдена";
            } else {
                responseString = "Успешное удаление";
                code = 201;
            }
        } catch (NumberFormatException e) {
            code = 400;
            responseString = "Некорректный идентификатор задачи";
        }
        writeResponse(exchange, responseString, code);
    }

    private Optional<Task> parseTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Task task = gson.fromJson(body, Task.class);
            return Optional.of(task);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
