package manager.taskmanager;

import HTTP.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import manager.historymanagers.HistoryManager;
import model.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;
    Gson gson = new Gson();
    String key;

    public HttpTaskManager(String uri) throws IOException, InterruptedException {
        super(uri);
        kvTaskClient = new KVTaskClient(URI.create(uri));
        key = getKvTaskClient().getAPI_token();
    }

    public static HttpTaskManager load(String key, HttpTaskManager manager) {
        try {
            String taskManager = manager.getKvTaskClient().load(key);
            String[] values = taskManager.split("\\n");
            manager.loadFromServer(values);
            return manager;
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void loadFromServer(String[] values) throws IOException {
        for (int i = 0; i < values.length - 1; i++) {
            if (!values[i].isBlank()) {
                Task task = fromString(values[i]);
                if (task != null && id <= task.getIdTask()) {
                    id = task.getIdTask();
                }
            }
        }
        String history = values[values.length - 1];
        if (history.isBlank()) {
            return;
        }
        List<Integer> saveHistory = historyFromString(history);
        for (int k = 0; k < saveHistory.size(); k++) {
            if (storingSimple.containsKey(saveHistory.get(k))) {
                historyManager.add(storingSimple.get(saveHistory.get(k)));
            } else if (storingEpic.containsKey(saveHistory.get(k))) {
                historyManager.add(storingEpic.get(saveHistory.get(k)));
            } else if (storingSubtask.containsKey(saveHistory.get(k))) {
                historyManager.add(storingSubtask.get(saveHistory.get(k)));
            }
        }
    }

    @Override
    protected void save() {
        String managerToJson = "";
        for (SimpleTask simpleTask : storingSimple.values()) {
            managerToJson += gson.toJson(simpleTask) + "\n";
        }
        for (EpicTask epicTask : storingEpic.values()) {
            managerToJson += gson.toJson(epicTask) + "\n";
        }
        for (Subtask subtask : storingSubtask.values()) {
            managerToJson += gson.toJson(subtask) + "\n";
        }
        managerToJson += "\n " + historyToString(historyManager);
        try {
            kvTaskClient.put(key, managerToJson);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static List<Integer> historyFromString(String value) {
        String[] arrayId = value.split(",");
        List<Integer> saveHistory = new ArrayList<>();
        for (String s : arrayId) {
            if (s.isBlank()) {
                return saveHistory;
            }
            if (s.length() > 1) {
                s = s.substring(1);
            }
            saveHistory.add(Integer.parseInt(s));
        }
        return saveHistory;
    }

    protected static String historyToString(HistoryManager manager) {
        List<String> stringId = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            stringId.add(Integer.toString(task.getIdTask()));
        }
        String listId = String.join(",", stringId);
        return listId;
    }

    @Override
    protected Task fromString(String value) throws IOException {
        JsonElement jsonElement = JsonParser.parseString(value);
        if (!jsonElement.isJsonObject()) {
            throw new IOException("Неверное данные для восстановления задачи");
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("epicId")) {
            Subtask subtask = new Subtask(jsonObject.get("nameTask").getAsString(),
                    jsonObject.get("descriptionTask").getAsString(), jsonObject.get("idTask").getAsInt(),
                    StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                    gson.fromJson(jsonObject.get("duration").getAsJsonObject(), Duration.class),
                    gson.fromJson(jsonObject.get("startTime").getAsJsonObject(), LocalDateTime.class),
                    jsonObject.get("epicId").getAsInt());
            createSubtask(subtask);
            return subtask;
        } else if (jsonObject.has("subtaskList")) {
            EpicTask epicTask = null;
            if (jsonObject.get("startTime") != null && jsonObject.get("startTime").isJsonNull()) {
                epicTask = new EpicTask(jsonObject.get("nameTask").getAsString(),
                        jsonObject.get("descriptionTask").getAsString(), jsonObject.get("idTask").getAsInt(),
                        StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                        gson.fromJson(jsonObject.get("duration").getAsJsonObject(), Duration.class),
                        gson.fromJson(jsonObject.get("startTime").getAsJsonObject(), LocalDateTime.class));
            } else {
                epicTask = new EpicTask(jsonObject.get("nameTask").getAsString(),
                        jsonObject.get("descriptionTask").getAsString(), jsonObject.get("idTask").getAsInt(),
                        StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                        gson.fromJson(jsonObject.get("duration").getAsJsonObject(), Duration.class),
                        null);
            }
            createEpicTask(epicTask);
            return epicTask;
        } else {
            SimpleTask simpleTask = new SimpleTask(jsonObject.get("nameTask").getAsString(),
                    jsonObject.get("descriptionTask").getAsString(), jsonObject.get("idTask").getAsInt(),
                    StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                    gson.fromJson(jsonObject.get("duration").getAsJsonObject(), Duration.class),
                    gson.fromJson(jsonObject.get("startTime").getAsJsonObject(), LocalDateTime.class));
            createSimpleTask(simpleTask);
            return simpleTask;
        }
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }
}
