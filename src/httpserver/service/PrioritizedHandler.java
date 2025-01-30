package httpserver.service;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskIdException;
import exception.NotFoundException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    protected final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder response = new StringBuilder();
        try {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                handleGet(response, exchange);
            } else {
                sendUnsupportedMethod(response, exchange);
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

    protected void handleGet(StringBuilder response, HttpExchange exchange) throws IOException {
        try {
            Set<Task> sortedTaskByTime = taskManager.getPrioritizedTasks();
            for (Task task : sortedTaskByTime) {
                response.append(task.toString()).append("\n");
            }
            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException e) {
            handleErrorResponse(e, response, 400, exchange);
        } catch (NotFoundException e) {
            handleErrorResponse(e, response, 404, exchange);
        }
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
