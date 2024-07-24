package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    TaskManager taskManager;
    Gson gson;
    final String HOST_URL = "http://localhost:8080/";

    @BeforeEach
    void beforeEach() throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        taskManager = Managers.getDefault();
        HttpTaskServer.createAndStartServer(taskManager);
    }

    @AfterEach
    void afterEach() {
        HttpTaskServer.stopServer();
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, description, status, duration);
        String taskJson = gson.toJson(task);
        URI url = URI.create(HOST_URL + "tasks");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers
                    .ofString(taskJson)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            List<Task> tasks = taskManager.getTaskList();
            assertNotNull(tasks, "Задачи не возвращаются");
            assertEquals(1, tasks.size(), "Неверное количество задач");
            assertEquals(name, tasks.get(0).getName(), "Некорректное имя");
            assertEquals(description, tasks.get(0).getDescription(), "Некорректное описание");
            assertEquals(duration, tasks.get(0).getDuration(), "Некорректная длительность");
        }
    }

    @Test
    void createEpicAndSubtask() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        String epicName = "Epic name test";
        String epicDescription = "epic description test";
        Epic epic = new Epic(epicName, epicDescription);
        String jsonEpic = gson.toJson(epic);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest epicRequest = HttpRequest.newBuilder().uri(URI.create(HOST_URL + "epics"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonEpic)).build();
            HttpResponse<String> epicResponse = httpClient.send(epicRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, epicResponse.statusCode(), "Эпик не был создан");
            List<Epic> epicList = taskManager.getEpicsList();
            assertNotNull(epicList, "Эпики не возвращаются");
            assertEquals(1, epicList.size(), "Неверное количество эпиков");
            assertEquals(epicName, epicList.get(0).getName(), "Некорректное имя эпика");
            assertEquals(epicDescription, epicList.get(0).getDescription(), "Некорректное описание эпика");
            epic = gson.fromJson(epicResponse.body(), Epic.class);
            Subtask subtask = new Subtask(name, description, status, duration, epic.getId());
            String jsonSubtask = gson.toJson(subtask);
            HttpRequest subtaskRequest = HttpRequest.newBuilder().uri(URI.create(HOST_URL + "subtasks"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();
            HttpResponse<String> subtaskResponse = httpClient
                    .send(subtaskRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, subtaskResponse.statusCode(), "Подзадача не была создана");
            List<Subtask> subtasks = taskManager.getSubtaskList();
            assertNotNull(subtasks, "Подзадачи не возвращаются");
            assertEquals(1, subtasks.size(), "Неверное количество подзадач");
            assertEquals(name, subtasks.get(0).getName(), "Некорректное имя");
            assertEquals(description, subtasks.get(0).getDescription(), "Некорректное описание");
            assertEquals(duration, subtasks.get(0).getDuration(), "Некорректная длительность");
            assertNotNull(subtasks.get(0).getEpicId(), "У подзадачи нет эпика");
        }
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, description, status, duration);
        taskManager.createTask(task);
        URI url = URI.create(HOST_URL + "tasks");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            List<Task> tasksFromHttp = gson.fromJson(httpResponse.body(), new TypeToken<List<Task>>() {
            }.getType());
            assertNotNull(tasksFromHttp, "Задачи не возвращаются");
            assertEquals(1, tasksFromHttp.size(), "Неверное количество задач");
            assertEquals(name, tasksFromHttp.get(0).getName(), "Некорректное имя");
            assertEquals(description, tasksFromHttp.get(0).getDescription(), "Некорректное описание");
            assertEquals(duration, tasksFromHttp.get(0).getDuration(), "Некорректная длительность");
        }
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, description, status, duration);
        int taskId = taskManager.createTask(task);
        URI url = URI.create(HOST_URL + "tasks/" + taskId);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            Task taskFromHttp = gson.fromJson(httpResponse.body(), Task.class);
            assertNotNull(taskFromHttp, "Задача не возвращается");
            assertEquals(name, taskFromHttp.getName(), "Некорректное имя");
            assertEquals(description, taskFromHttp.getDescription(), "Некорректное описание");
            assertEquals(duration, taskFromHttp.getDuration(), "Некорректная длительность");
        }
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Updated description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, "Test description", status, duration);
        int taskId = taskManager.createTask(task);
        task.setId(taskId);
        task.setDescription(description);
        String taskJson = gson.toJson(task);
        URI url = URI.create(HOST_URL + "tasks");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers
                    .ofString(taskJson)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            List<Task> tasks = taskManager.getTaskList();
            assertEquals(1, tasks.size(), "Неверное количество задач");
            assertEquals(name, tasks.get(0).getName(), "Некорректное имя");
            assertEquals(description, tasks.get(0).getDescription(), "Некорректное описание");
            assertEquals(duration, tasks.get(0).getDuration(), "Некорректная длительность");
        }
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, description, status, duration);
        taskManager.createTask(task);
        taskManager.createTask(task);
        URI url = URI.create(HOST_URL + "tasks");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            List<Task> tasks = taskManager.getTaskList();
            assertEquals(0, tasks.size(), "Задачи не удалены");
        }
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, description, status, duration);
        int taskId = taskManager.createTask(task);
        Task secondTask = new Task(name, description, status, duration);
        int secondTaskId = taskManager.createTask(secondTask);
        URI url = URI.create(HOST_URL + "tasks/" + taskId);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            List<Task> tasks = taskManager.getTaskList();
            assertEquals(1, tasks.size(), "Задача не удалена");
            assertEquals(secondTaskId, tasks.get(0).getId(), "Удалена задача не с тем идентификатором");
        }
    }


    @Test
    void getAllSubtasks() throws IOException, InterruptedException {
        String epicName = "Epic name test";
        String epicDescription = "epic description test";
        Epic epic = new Epic(epicName, epicDescription);
        int epicId = taskManager.createEpic(epic);
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Subtask subtask = new Subtask(name, description, status, duration, epicId);
        taskManager.createSubtask(subtask);
        URI url = URI.create(HOST_URL + "subtasks");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            List<Subtask> subtasksFromHttp = gson.fromJson(httpResponse.body(), new TypeToken<List<Subtask>>() {
            }.getType());
            assertNotNull(subtasksFromHttp, "Подзадачи не возвращаются");
            assertEquals(1, subtasksFromHttp.size(), "Неверное количество задач");
            assertEquals(name, subtasksFromHttp.get(0).getName(), "Некорректное имя");
            assertEquals(description, subtasksFromHttp.get(0).getDescription(), "Некорректное описание");
            assertEquals(duration, subtasksFromHttp.get(0).getDuration(), "Некорректная длительность");
            assertEquals(epicId, subtasksFromHttp.get(0).getEpicId(), "Некорректный идентификатор эпика");
        }
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        String epicName = "Epic name test";
        String epicDescription = "epic description test";
        Epic epic = new Epic(epicName, epicDescription);
        int epicId = taskManager.createEpic(epic);
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Subtask subtask = new Subtask(name, description, status, duration, epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        URI url = URI.create(HOST_URL + "subtasks/" + subtaskId);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            Subtask subtaskFromHttp = gson.fromJson(httpResponse.body(), Subtask.class);
            assertNotNull(subtaskFromHttp, "Подзадача не возвращается");
            assertEquals(name, subtaskFromHttp.getName(), "Некорректное имя");
            assertEquals(description, subtaskFromHttp.getDescription(), "Некорректное описание");
            assertEquals(duration, subtaskFromHttp.getDuration(), "Некорректная длительность");
            assertEquals(epicId, subtaskFromHttp.getEpicId(), "Некорректный идентификатор эпика");
        }
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        String epicName = "Epic name test";
        String epicDescription = "epic description test";
        Epic epic = new Epic(epicName, epicDescription);
        int epicId = taskManager.createEpic(epic);
        String name = "Test name";
        String description = "Updated description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Subtask subtask = new Subtask(name, description, status, duration, epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        subtask.setId(subtaskId);
        String subtaskJson = gson.toJson(subtask, Subtask.class);
        URI url = URI.create(HOST_URL + "subtasks");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers
                    .ofString(subtaskJson)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            Subtask subtaskFromManager = taskManager.getSubtaskById(subtaskId);
            assertNotNull(subtaskFromManager, "Подзадача не возвращается");
            assertEquals(name, subtaskFromManager.getName(), "Некорректное имя");
            assertEquals(description, subtaskFromManager.getDescription(), "Некорректное описание");
            assertEquals(duration, subtaskFromManager.getDuration(), "Некорректная длительность");
            assertEquals(epicId, subtaskFromManager.getEpicId(), "Некорректный идентификатор эпика");
        }
    }

    @Test
    void deleteAllSubtasks() throws IOException, InterruptedException {
        String epicName = "Epic name test";
        String epicDescription = "epic description test";
        Epic epic = new Epic(epicName, epicDescription);
        int epicId = taskManager.createEpic(epic);
        String name = "Test name";
        String description = "Updated description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Subtask subtask = new Subtask(name, description, status, duration, epicId);
        taskManager.createSubtask(subtask);
        int subtaskId = taskManager.createSubtask(subtask);
        URI url = URI.create(HOST_URL + "subtasks");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            List<Subtask> subtasks = taskManager.getSubtaskList();
            assertEquals(0, subtasks.size(), "Подзадачи не удалены");
        }
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        String epicName = "Epic name test";
        String epicDescription = "epic description test";
        Epic epic = new Epic(epicName, epicDescription);
        int epicId = taskManager.createEpic(epic);
        String name = "Test name";
        String description = "Updated description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Subtask subtask = new Subtask(name, description, status, duration, epicId);
        int firstSubtaskId = taskManager.createSubtask(subtask);
        Subtask secondSubtask = new Subtask(name, description, status, duration, epicId);
        int subtaskId = taskManager.createSubtask(secondSubtask);
        URI url = URI.create(HOST_URL + "subtasks/" + subtaskId);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            List<Subtask> subtasks = taskManager.getSubtaskList();
            assertEquals(1, subtasks.size(), "Подзадача не удалена");
            assertEquals(firstSubtaskId, subtasks.get(0).getId(), "Удалена подзадача не с тем идентификатором");
        }
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        URI url = URI.create(HOST_URL + "epics");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            List<Epic> epicsFromHttp = gson.fromJson(httpResponse.body(), new TypeToken<List<Epic>>() {
            }.getType());
            assertNotNull(epicsFromHttp, "Эпики не возвращаются");
            assertEquals(1, epicsFromHttp.size(), "Неверное количество эпиков");
            assertEquals(name, epicsFromHttp.get(0).getName(), "Некорректное имя");
            assertEquals(description, epicsFromHttp.get(0).getDescription(), "Некорректное описание");
        }
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Epic epic = new Epic(name, description);
        int epicId = taskManager.createEpic(epic);
        URI url = URI.create(HOST_URL + "epics/" + epicId);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            Epic epicFromHttp = gson.fromJson(httpResponse.body(), Epic.class);
            assertNotNull(epicFromHttp, "Эпики не возвращаются");
            assertEquals(name, epicFromHttp.getName(), "Некорректное имя");
            assertEquals(description, epicFromHttp.getDescription(), "Некорректное описание");
        }
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Epic epic = new Epic(name, description);
        int epicId = taskManager.createEpic(epic);
        taskManager.createEpic(epic);
        URI url = URI.create(HOST_URL + "epics");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            List<Epic> epics = taskManager.getEpicsList();
            assertEquals(0, epics.size(), "Эпики не удалены");
        }
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Epic epic = new Epic(name, description);
        int epicId = taskManager.createEpic(epic);
        Epic secondEpic = new Epic(name, description);
        int secondEpicId = taskManager.createEpic(secondEpic);
        URI url = URI.create(HOST_URL + "epics/" + secondEpicId);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode());
            List<Epic> epics = taskManager.getEpicsList();
            assertEquals(1, epics.size(), "Эпик не удален");
            assertEquals(epicId, epics.get(0).getId(), "Удален эпик не с тем идентификатором");
        }
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, description, status, duration);
        taskManager.createTask(task);
        taskManager.getTaskList();
        URI url = URI.create(HOST_URL + "history");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            List<Task> history = taskManager.getHistory();
            assertNotNull(history, "История не возвращается");
            assertEquals(1, history.size(), "Неверное количество задач");
            assertEquals(name, history.get(0).getName(), "Некорректное имя");
            assertEquals(description, history.get(0).getDescription(), "Некорректное описание");
            assertEquals(duration, history.get(0).getDuration(), "Некорректная длительность");
        }
    }

    @Test
    void getPrioritized() throws IOException, InterruptedException {
        String name = "Test name";
        String description = "Test description";
        Status status = Status.NEW;
        Duration duration = Duration.of(60, ChronoUnit.MINUTES);
        Task task = new Task(name, description, status, duration);
        task.setStartTime(LocalDateTime.of(2024, 10, 1, 6, 0));
        taskManager.createTask(task);
        URI url = URI.create(HOST_URL + "prioritized");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            TreeSet<Task> prioritized = taskManager.getPrioritizedTasks();
            assertNotNull(prioritized, "История не возвращается");
            assertEquals(1, prioritized.size(), "Неверное количество задач");
            assertEquals(name, prioritized.getFirst().getName(), "Некорректное имя");
            assertEquals(description, prioritized.getFirst().getDescription(), "Некорректное описание");
            assertEquals(duration, prioritized.getFirst().getDuration(), "Некорректная длительность");
        }
    }

    @Test
    void ifEndpointIsNotCorrect() throws IOException, InterruptedException {
        URI url = URI.create(HOST_URL + "notcorrect");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, httpResponse.statusCode());
        }
    }
}