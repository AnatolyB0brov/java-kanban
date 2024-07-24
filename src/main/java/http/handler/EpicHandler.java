package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.Endpoint;
import http.handler.BaseHttpHandler;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET: {
                handleGetAllEpics(exchange);
                break;
            }
            case GET_BY_ID: {
                handleGetEpicById(exchange);
                break;
            }
            case GET_EPICS_SUBTASKS: {
                handleGetEpicSubtasks(exchange);
                break;
            }
            case CREATE_OR_UPDATE: {
                handleCreateEpic(exchange);
                break;
            }
            case DELETE: {
                handleDeleteAllEpics(exchange);
                break;
            }
            case DELETE_BY_ID: {
                handleDeleteEpicById(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epicList = taskManager.getEpicsList();
        String responseString = gson.toJson(epicList);
        writeResponse(exchange, responseString, 200);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                code = 404;
                responseString = "Эпик с идентификатором " + id + " не найден";
            } else {
                responseString = gson.toJson(epic);
                code = 200;
            }
        } catch (NumberFormatException e) {
            code = 400;
            responseString = "Некорректный идентификатор эпика";
        }
        writeResponse(exchange, responseString, code);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                code = 404;
                responseString = "Эпик с идентификатором " + id + " не найден";
            } else {
                responseString = gson.toJson(epic.getSubtasks());
                code = 200;
            }
        } catch (NumberFormatException e) {
            code = 400;
            responseString = "Некорректный идентификатор эпика";
        }
        writeResponse(exchange, responseString, code);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String responseString;
        int code;
        Optional<Epic> optionalEpic = parseEpic(exchange);
        if (optionalEpic.isPresent()) {
            Epic epic = optionalEpic.get();
            int id = taskManager.createEpic(epic);
            epic.setId(id);
            responseString = gson.toJson(epic);
            code = 200;
        } else {
            code = 400;
            responseString = "Объект для создания сформирован некорректно";
        }
        writeResponse(exchange, responseString, code);
    }

    private void handleUpdateEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            Optional<Epic> optionalEpic = parseEpic(exchange);
            if (optionalEpic.isPresent()) {
                if (!taskManager.updateEpic(optionalEpic.get())) {
                    code = 400;
                    responseString = "Ошибка обновления эпика с идентификатором " + id;
                } else {
                    responseString = "Успешное обновление";
                    code = 201;
                }
            } else {
                code = 400;
                responseString = "Объект для обновления сформирован некорректно";
            }
        } catch (NumberFormatException e) {
            code = 400;
            responseString = "Некорректный идентификатор эпика";
        }
        writeResponse(exchange, responseString, code);
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        String responseString = "Успешное удаление.";
        writeResponse(exchange, responseString, 201);
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String responseString;
        int code;
        try {
            int id = Integer.parseInt(pathParts[2]);
            if (!taskManager.deleteEpicById(id)) {
                code = 404;
                responseString = "Эпик с идентификатором " + id + " не найден";
            } else {
                responseString = "Успешное удаление";
                code = 201;
            }
        } catch (NumberFormatException e) {
            code = 400;
            responseString = "Некорректный идентификатор эпика";
        }
        writeResponse(exchange, responseString, code);
    }

    private Optional<Epic> parseEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Epic Epic = gson.fromJson(body, Epic.class);
            return Optional.of(Epic);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
