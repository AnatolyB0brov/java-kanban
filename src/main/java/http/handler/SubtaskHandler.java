package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import http.Endpoint;
import manager.TaskManager;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET: {
                handleGetAllSubtasks(exchange);
                break;
            }
            case GET_BY_ID: {
                handleGetSubtaskById(exchange);
                break;
            }
            case CREATE_OR_UPDATE: {
                handleCreateOrUpdateSubtask(exchange);
                break;
            }
            case DELETE: {
                handleDeleteAllSubtasks(exchange);
                break;
            }
            case DELETE_BY_ID: {
                handleDeleteSubtaskById(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtaskList = taskManager.getSubtaskList();
        String responseString = gson.toJson(subtaskList);
        writeResponse(exchange, responseString, 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask == null) {
                code = 404;
                responseString = "Задача с идентификатором " + id + " не найдена";
            } else {
                responseString = gson.toJson(subtask);
                code = 200;
            }
        } catch (NumberFormatException e) {
            code = 400;
            responseString = "Некорректный идентификатор задачи";
        }
        writeResponse(exchange, responseString, code);
    }


    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String responseString;
        int code;
        Optional<Subtask> optionalSubtask = parseSubtask(exchange);
        if (optionalSubtask.isPresent()) {
            Subtask subtask = optionalSubtask.get();
            try {
                if (subtask.getId() == null) {
                    createSubtask(exchange, subtask);
                } else {
                    updateSubtask(exchange, subtask);
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

    private void createSubtask(HttpExchange exchange, Subtask subtask) throws IOException {
        String responseString;
        int code;
        try {
            int id = taskManager.createSubtask(subtask);
            subtask.setId(id);
            responseString = gson.toJson(subtask);
            code = 200;
        } catch (ManagerSaveException e) {
            code = 400;
            responseString = "Ошибка добавления задачи. " + e.getMessage();
        }
        writeResponse(exchange, responseString, code);
    }

    private void updateSubtask(HttpExchange exchange, Subtask subtask) throws IOException {
        String responseString;
        int code;
        try {
            if (!taskManager.updateSubtask(subtask)) {
                code = 400;
                responseString = "Ошибка обновления задачи с идентификатором " + subtask.getId();
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

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        String responseString = "Успешное удаление.";
        writeResponse(exchange, responseString, 201);
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            if (!taskManager.deleteSubtaskById(id)) {
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

    private Optional<Subtask> parseSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            return Optional.of(subtask);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
