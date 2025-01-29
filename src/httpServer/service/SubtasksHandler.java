package httpServer.service;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskIdException;
import exception.ManagerValidatePriority;
import exception.NotFoundException;
import model.Subtask;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class SubtasksHandler extends TasksHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(String query, StringBuilder response, HttpExchange exchange) throws IOException {
        try {
            if (query == null || query.isEmpty()) {
                List<Subtask> allTasks = taskManager.getAllSubtasks();
                for (Subtask task : allTasks) {
                    response.append(task.toString()).append("\n");
                }
            } else {
                int taskId = getTaskIdFromRequest(exchange);
                Subtask task = taskManager.getSubtask(taskId);
                response.append(task.toString());
            }

            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, response, 400, exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, StringBuilder response) throws IOException {
        try {
            Subtask newTask = readTaskFromRequest(exchange);
            int taskId = newTask.getId();
            if (taskId == 0) {
                taskId = taskManager.addNewSubtask(newTask);
                if (taskId == -1) {
                    throw new NotFoundException("Без Эпика подзадача не может быть создана");
                }
                response.append("Задача успешно добавлена с ID: ").append(taskId);
            } else {
                taskManager.updateSubtask(newTask);
                response.append("Задача с ID ").append(taskId).append(" успешно обновлена.");
            }
            sendText(exchange, response.toString(), 201);
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
            Subtask updatedTask = readTaskFromRequest(exchange);
            updatedTask.setId(taskId);
            taskManager.updateSubtask(updatedTask);
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
            taskManager.deleteSubtask(taskIdToDelete);
            response.append("Задача с ID: ").append(taskIdToDelete).append(" удалена.");
            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException | URISyntaxException e) {
            handleErrorResponse(e, response, 400, exchange);
        }
    }

    @Override
    protected Subtask readTaskFromRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        return gson.fromJson(requestBody, Subtask.class);
    }
}
