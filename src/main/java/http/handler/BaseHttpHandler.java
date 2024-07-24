package http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import http.adapter.DurationAdapter;
import http.Endpoint;
import http.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected Gson gson;

    public BaseHttpHandler() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    protected void writeResponse(HttpExchange exchange,
                                 String responseString,
                                 int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_OR_UPDATE;
            }
        }
        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_BY_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_BY_ID;
            }
        }
        if (pathParts.length == 4) {
            if (requestMethod.equals("GET") && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_EPICS_SUBTASKS;
            }
        }
        return Endpoint.UNKNOWN;
    }
}