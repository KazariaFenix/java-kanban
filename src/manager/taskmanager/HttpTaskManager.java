package manager.taskmanager;

import com.google.gson.*;
import http.KVTaskClient;
import manager.exception.ManagerSaveException;
import manager.historymanagers.HistoryManager;
import model.*;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;
    private Gson gson;
    private String key;

    public HttpTaskManager(String uri) {
        super(uri);
        kvTaskClient = new KVTaskClient(URI.create(uri));
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gson = gsonBuilder.create();
        key = "manager";
    }

    public static HttpTaskManager load(String key) {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8081/register");
        String taskManager = manager.getKvTaskClient().load(key);
        String[] values = taskManager.split("\\n");
        manager.loadFromServer(values);
        return manager;
    }

    private void loadFromServer(String[] values) {
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
        kvTaskClient.put(key, managerToJson);
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
    protected Task fromString(String value) {
        JsonElement jsonElement = JsonParser.parseString(value);
        if (!jsonElement.isJsonObject()) {
            throw new ManagerSaveException("Невернsе данные для восстановления задачи");
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
            EpicTask epicTask = new EpicTask(jsonObject.get("nameTask").getAsString(),
                    jsonObject.get("descriptionTask").getAsString(), jsonObject.get("idTask").getAsInt(),
                    StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                    gson.fromJson(jsonObject.get("duration").getAsJsonObject(), Duration.class),
                    null);
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
