package httpserver.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.BadRequestException;
import exception.InvalidTaskIdException;
import exception.ManagerValidatePriorityException;
import exception.NotFoundException;
import httpserver.adapter.DurationAdapter;
import httpserver.adapter.LocalDateTimeAdapter;
import model.Task;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TasksHandler extends BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder response = new StringBuilder();
        try {
            URI requestURI = exchange.getRequestURI();
            String query = requestURI.getQuery();
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGet(query, response, exchange);
                    break;
                case "POST":
                    handlePost(exchange, response);
                    break;
                case "PUT":
                    handlePut(query, exchange, response);
                    break;
                case "DELETE":
                    handleDelete(query, exchange, response);
                    break;
                default:
                    sendUnsupportedMethod(response, exchange);
                    break;
            }
        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "500 Internal Server Error", 500);
        } finally {
            exchange.close();
        }
    }

    protected void handleGet(String query, StringBuilder response, HttpExchange exchange) throws IOException {
        try {
            if (query == null || query.isEmpty()) {
                List<Task> allTasks = taskManager.getAllTasks();
                for (Task task : allTasks) {
                    response.append(task.toString()).append("\n");
                }
            } else {
                int taskId = getTaskIdFromRequest(query);
                Task task = taskManager.getTask(taskId);
                response.append(task.toString());
            }

            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, response, 400, exchange);
        }
    }

    protected void handlePost(HttpExchange exchange, StringBuilder response) throws IOException {
        try {
            Task newTask = readTaskFromRequest(exchange);
            int taskId = newTask.getId();
            if (taskId == 0) {
                taskId = taskManager.addNewTask(newTask);
                response.append("Задача успешно добавлена с ID: ").append(taskId);
            } else {
                taskManager.updateTask(newTask);
                response.append("Задача с ID ").append(taskId).append(" успешно обновлена.");
            }
            sendText(exchange, response.toString(), 201);
        } catch (JsonParseException | BadRequestException e) {
            handleErrorResponse(e, response, 400, exchange);
        } catch (ManagerValidatePriorityException e) {
            handleErrorResponse(e, response, 406, exchange);
        }
    }

    protected void handlePut(String query, HttpExchange exchange, StringBuilder response) throws IOException {
        try {
            int taskId = getTaskIdFromRequest(query);
            Task updatedTask = readTaskFromRequest(exchange);
            updatedTask.setId(taskId);
            taskManager.updateTask(updatedTask);
            response.append("Задача с ID ").append(taskId).append(" успешно обновлена.");
            sendText(exchange, response.toString(), 201);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException
                 | BadRequestException e) {
            handleErrorResponse(e, response, 400, exchange);
        } catch (ManagerValidatePriorityException e) {
            handleErrorResponse(e, response, 406, exchange);
        }
    }

    protected void handleDelete(String query, HttpExchange exchange, StringBuilder response) throws IOException {
        try {
            int taskIdToDelete = getTaskIdFromRequest(query);
            taskManager.deleteTask(taskIdToDelete);
            response.append("Задача с ID: ").append(taskIdToDelete).append(" удалена.");
            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, response, 400, exchange);
        }
    }

    protected Task readTaskFromRequest(HttpExchange exchange) {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        // Проверка на пустое тело запроса
        if (requestBody.isEmpty()) {
            throw new BadRequestException("Ошибка: тело запроса не может быть пустым.");
        }
        return gson.fromJson(requestBody, Task.class);
    }

    protected int getTaskIdFromRequest(String query) throws URISyntaxException {
        if (query != null && query.startsWith("id=")) {
            String[] params = query.split("=");
            if (params.length == 2) {
                String taskIdStr = params[1];

                if (taskIdStr == null || taskIdStr.isEmpty()) {
                    throw new InvalidTaskIdException("ID задачи не может быть пустым.");
                }

                String trimmedTaskIdStr = taskIdStr;
                if (taskIdStr.contains("/subtasks")) {
                    trimmedTaskIdStr = taskIdStr.substring(0, taskIdStr.indexOf("/subtasks"));
                }
                try {
                    return Integer.parseInt(trimmedTaskIdStr);
                } catch (NumberFormatException e) {
                    throw new InvalidTaskIdException("ID задачи должен быть целым числом");
                }
            }
        }
        throw new IllegalArgumentException("ID задачи не указан в строке запроса.");
    }

    protected void handleErrorResponse(Exception e, StringBuilder response, int statusCode, HttpExchange exchange) throws IOException {
        response.append(e.getMessage());
        sendText(exchange, response.toString(), statusCode);
    }

    protected void sendUnsupportedMethod(StringBuilder response, HttpExchange exchange) throws IOException {
        response.append("Метод не поддерживается.");
        sendText(exchange, response.toString(), 405);
    }
}