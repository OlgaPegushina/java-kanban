package httpserver.service;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskIdException;
import exception.ManagerValidatePriority;
import exception.NotFoundException;
import model.Epic;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class EpicsHandler extends TasksHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(String query, StringBuilder response, HttpExchange exchange) throws IOException {
        try {
            if (query == null || query.isEmpty()) {
                List<Epic> allTasks = taskManager.getAllEpics();
                for (Epic task : allTasks) {
                    response.append(task.toString()).append("\n");
                }
            } else {
                int taskId = getTaskIdFromRequest(exchange);
                Epic task = taskManager.getEpic(taskId);

                if (query.contains("/subtasks")) {
                    List<Integer> subtaskIds = task.getSubtaskIds();
                    response.append("Subtask IDs: ").append(subtaskIds.toString()).append("\n");
                } else {
                    response.append(task.toString());
                }
            }
            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, response, 400, exchange);
        } catch (NotFoundException e) {
            handleErrorResponse(e, response, 404, exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, StringBuilder response) throws IOException {
        try {
            Epic newTask = readTaskFromRequest(exchange);
            int taskId = newTask.getId();
            if (taskId == 0) {
                taskId = taskManager.addNewEpic(newTask);
                response.append("Задача успешно добавлена с ID: ").append(taskId);
                sendText(exchange, response.toString(), 201);
            }
        } catch (JsonParseException e) {
            handleErrorResponse(e, response, 400, exchange);
        } catch (ManagerValidatePriority e) {
            handleErrorResponse(e, response, 406, exchange);
        }
    }

    @Override
    protected void handlePut(HttpExchange exchange, StringBuilder response) throws IOException {
        try {
            int taskId = getTaskIdFromRequest(exchange);
            Epic updatedTask = readTaskFromRequest(exchange);
            updatedTask.setId(taskId);
            taskManager.updateEpic(updatedTask);
            response.append("Задача с ID ").append(taskId).append(" успешно обновлена.");
            sendText(exchange, response.toString(), 201);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, response, 400, exchange);
        } catch (ManagerValidatePriority e) {
            handleErrorResponse(e, response, 406, exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, StringBuilder response) throws IOException {
        try {
            int taskIdToDelete = getTaskIdFromRequest(exchange);
            taskManager.deleteEpic(taskIdToDelete);
            response.append("Задача с ID: ").append(taskIdToDelete).append(" удалена.");
            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, response, 400, exchange);
        }
    }

    @Override
    protected Epic readTaskFromRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        return gson.fromJson(requestBody, Epic.class);
    }
}
