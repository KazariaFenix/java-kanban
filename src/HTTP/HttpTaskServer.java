package HTTP;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.exception.FreeTimeException;
import manager.taskmanager.HttpTaskManager;
import model.EpicTask;
import model.SimpleTask;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private HttpTaskManager taskManager;

    public HttpTaskServer() throws IOException, InterruptedException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::handle);
        //httpServer.start();
        taskManager = Managers.getDefault();
    }

    final Gson gson = new Gson();


    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath()
                + exchange.getRequestURI().getQuery(), exchange.getRequestMethod());
        int idTask = 0;
        if (exchange.getRequestURI().getQuery() != null && exchange.getRequestURI().getQuery().contains("=")) {
            int index = exchange.getRequestURI().getQuery().indexOf("=");
            idTask = Integer.parseInt(exchange.getRequestURI().getQuery().substring(index + 1));
        }
        int codeStatus = 200;
        String responseBody;
        int count;
        switch (endpoint) {
            case GET_SIMPLETASKS:
                List<SimpleTask> simpleTaskList = taskManager.getSimpleTask();
                responseBody = gson.toJson(simpleTaskList);
                break;
            case GET_EPICTASKS:
                List<EpicTask> epicTaskList = taskManager.getEpicTask();
                responseBody = gson.toJson(epicTaskList);
                break;
            case GET_SUBTASKS:
                List<Subtask> subtaskList = taskManager.getSubtask();
                responseBody = gson.toJson(subtaskList);
                break;
            case GET_TASK_HISTORY:
                List<Task> taskHistoryList = taskManager.getHistoryManager();
                responseBody = gson.toJson(taskHistoryList);
                break;
            case GET_SIMPLETASK_BY_ID:
                Task simpleTaskId = taskManager.getIdSimple(idTask);
                responseBody = gson.toJson(simpleTaskId);
                break;
            case GET_EPICTASK_BY_ID:
                Task epicTaskId = taskManager.getIdEpic(idTask);
                responseBody = gson.toJson(epicTaskId);
                break;
            case GET_SUBTASK_BY_ID:
                Task subTaskId = taskManager.getIdSub(idTask);
                responseBody = gson.toJson(subTaskId);
                break;
            case GET_PRIORITIZED_TASK:
                List<Task> taskList = taskManager.getPrioritizedTasks();
                responseBody = gson.toJson(taskList);
                break;
            case GET_SUB_OF_EPIC:
                List<Subtask> subtasksOfEpic = taskManager.getSubOfEpicTask(idTask);
                responseBody = gson.toJson(subtasksOfEpic);
                break;
            case DELETE_ALL_SIMPLETASKS:
                taskManager.clearSimpleTask();
                responseBody = "Простые задачи очищены";
                break;
            case DELETE_ALL_EPICTASKS:
                taskManager.clearEpicTask();
                responseBody = "Многосоставные задачи очищены";
                break;
            case DELETE_ALL_SUBTASKS:
                taskManager.clearSubtask();
                responseBody = "Подзадачи успешно очищены";
                break;
            case DELETE_SIMPLETASK_BY_ID:
                try {
                    taskManager.deleteIdSimple(idTask);
                } catch (NullPointerException e) {
                    responseBody = e.getMessage();
                    break;
                }
                responseBody = "Задача под идентификатором " + idTask + " успешно удалена";
                break;
            case DELETE_EPICTASK_BY_ID:
                try {
                    taskManager.deleteIdEpicTask(idTask);
                } catch (Exception e) {
                    responseBody = e.getMessage();
                    break;
                }
                responseBody = "Задача под идентификатором " + idTask + " успешно удалена";
                break;
            case DELETE_SUBTASK_BY_ID:
                try {
                    taskManager.deleteIdSubtask(idTask);
                } catch (Exception e) {
                    responseBody = e.getMessage();
                    break;
                }
                responseBody = "Задача под идентификатором " + idTask + " успешно удалена";
                break;
            case POST_ADD_OR_UPDATE_SIMPLETASK:
                try {
                    count = 0;
                    SimpleTask simpleTask = gson.fromJson(new String(exchange.getRequestBody()
                            .readAllBytes(), StandardCharsets.UTF_8), SimpleTask.class);
                    for (Task task : taskManager.getSimpleTask()) {
                        if (task.getIdTask() == simpleTask.getIdTask()) {
                            count += 1;
                        }
                    }
                    if (count == 0) {
                        taskManager.createSimpleTask(simpleTask);
                        responseBody = "Простая задача успешно добавлена";
                    } else {
                        taskManager.updateSimpleTask(simpleTask);
                        responseBody = "Простая задача успешно обновлена";
                    }
                    break;
                } catch (FreeTimeException e) {
                    responseBody = e.getMessage();
                    break;
                }
            case POST_ADD_OR_UPDATE_EPICTASK:
                count = 0;
                EpicTask epicTask = gson.fromJson(new String(exchange.getRequestBody()
                        .readAllBytes(), StandardCharsets.UTF_8), EpicTask.class);
                for (int i = 0; i < taskManager.getEpicTask().size(); i++) {
                    if (taskManager.getEpicTask().get(i).getIdTask() == epicTask.getIdTask()) {
                        count += 1;
                    }
                }
                if (count == 0) {
                    taskManager.createEpicTask(epicTask);
                    responseBody = "Многосоставная задача успешно добавлена";
                } else {
                    taskManager.updateEpicTask(epicTask);
                    responseBody = "Многосоставная задача успешно обновлена";
                }
                break;
            case POST_ADD_OR_UPDATE_SUBTASK:
                try {
                    count = 0;
                    Subtask subtask = gson.fromJson(new String(exchange.getRequestBody()
                            .readAllBytes(), StandardCharsets.UTF_8), Subtask.class);
                    for (Task task : taskManager.getSubtask()) {
                        if (task.getIdTask() == subtask.getIdTask()) {
                            count += 1;
                            break;
                        }
                    }
                    if (count == 0) {
                        taskManager.createSubtask(subtask);
                        responseBody = "Подзадача успешно добавлена";
                    } else {
                        taskManager.updateSubtask(subtask);
                        responseBody = "Подзадача успешно обновлена";
                    }
                    break;
                } catch (FreeTimeException e) {
                    responseBody = e.getMessage();
                    break;
                }
            default:
                codeStatus = 400;
                responseBody = "Произошла ошибка, проверьте данные запроса и повторите попытку";
        }
        exchange.sendResponseHeaders(codeStatus, 0);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(responseBody.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");
        if (requestMethod.equals("GET")) {
            if (path.length == 3 && path[2].equals("null")) {
                return Endpoint.GET_PRIORITIZED_TASK;
            } else if (path.length == 4) {
                if (path[path.length - 1].equals("null")) {
                    if (path[2].equals("simple")) {
                        return Endpoint.GET_SIMPLETASKS;
                    } else if (path[2].equals("epic")) {
                        return Endpoint.GET_EPICTASKS;
                    } else if (path[2].equals("subtask")) {
                        return Endpoint.GET_SUBTASKS;
                    } else if (path[2].equals("history")) {
                        return Endpoint.GET_TASK_HISTORY;
                    } else {
                        return Endpoint.UNKNOWN;//vozmozhno stoit ubrat lishnie UNKNOWN
                    }
                }
                if (path[2].equals("simple")) {
                    return Endpoint.GET_SIMPLETASK_BY_ID;
                } else if (path[2].equals("epic")) {
                    return Endpoint.GET_EPICTASK_BY_ID;
                } else if (path[2].equals("subtask")) {
                    return Endpoint.GET_SUBTASK_BY_ID;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else if (path.length == 5) {
                if (path[2].equals("subtask") && path[3].equals("epic")) {
                    return Endpoint.GET_SUB_OF_EPIC;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        } else if (requestMethod.equals("DELETE")) {
            if (path.length == 4) {
                if (path[path.length - 1].equals("null")) {
                    if (path[2].equals("simple")) {
                        return Endpoint.DELETE_ALL_SIMPLETASKS;
                    } else if (path[2].equals("epic")) {
                        return Endpoint.DELETE_ALL_EPICTASKS;
                    } else if (path[2].equals("subtask")) {
                        return Endpoint.DELETE_ALL_SUBTASKS;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                }
                if (path[2].equals("simple")) {
                    return Endpoint.DELETE_SIMPLETASK_BY_ID;
                } else if (path[2].equals("epic")) {
                    return Endpoint.DELETE_EPICTASK_BY_ID;
                } else if (path[2].equals("subtask")) {
                    return Endpoint.DELETE_SUBTASK_BY_ID;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        } else if (requestMethod.equals("POST")) {
            if (path.length == 4) {
                if (path[2].equals("simple")) {
                    return Endpoint.POST_ADD_OR_UPDATE_SIMPLETASK;
                } else if (path[2].equals("epic")) {
                    return Endpoint.POST_ADD_OR_UPDATE_EPICTASK;
                } else if (path[2].equals("subtask")) {
                    return Endpoint.POST_ADD_OR_UPDATE_SUBTASK;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        } else {
            return Endpoint.UNKNOWN;
        }
    }

    public HttpTaskManager getTaskManager() {
        return taskManager;
    }

    public void start() {
        System.out.println("Сервер слушает порт " + PORT);
        System.out.println("Можете использовать приложение");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }
}

