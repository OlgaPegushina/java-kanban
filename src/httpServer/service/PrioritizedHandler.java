package httpServer.service;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskIdException;
import exception.NotFoundException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

public class PrioritizedHandler extends TasksHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(String query, StringBuilder response, HttpExchange exchange) throws IOException {
        try {
            Set<Task> sortedTaskByTime = taskManager.getPrioritizedTasks();
            if (sortedTaskByTime == null) {
                sortedTaskByTime = new TreeSet<>((task1, task2) -> {
                    LocalDateTime startTime1 = task1.getStartTime();
                    LocalDateTime startTime2 = task2.getStartTime();
                    return 0;
                });
            }
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
}
