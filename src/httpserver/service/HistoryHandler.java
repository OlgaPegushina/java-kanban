package httpserver.service;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskIdException;
import exception.NotFoundException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends PrioritizedHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(StringBuilder response, HttpExchange exchange) throws IOException {
        try {
            List<Task> history = taskManager.getHistory();
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
