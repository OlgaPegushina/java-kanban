package httpServer.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskIdException;
import exception.NotFoundException;
import httpServer.adapter.DurationAdapter;
import httpServer.adapter.LocalDateTimeAdapter;
import model.Epic;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryHandler extends TasksHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(String query, StringBuilder response, HttpExchange exchange) throws IOException {
        try {
            List<Task> history = taskManager.getHistory();
            if (history == null) {
                history = new ArrayList<>();
            }
            for (Task task : history) {
                response.append(task.toString()).append("\n");
            }

            sendText(exchange, response.toString(), 200);
        } catch (JsonParseException | InvalidTaskIdException | IllegalArgumentException e) {
            handleErrorResponse(e, response, 400, exchange);
        } catch (NotFoundException e) {
            handleErrorResponse(e, response, 404, exchange);
        }
    }
}
