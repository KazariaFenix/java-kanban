package manager.taskmanager;

import HTTP.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;
    Gson gson = new Gson();
    public HttpTaskManager(String uri) throws IOException, InterruptedException {
        super(uri);
        kvTaskClient = new KVTaskClient(URI.create(uri));
    }

    public static HttpTaskManager loadFromFile(String key) {
        try {
            HttpTaskManager newTaskManager = new HttpTaskManager("http://localhost:8081/");
            String taskManager = newTaskManager.getKvTaskClient().load(key);
            String[] values = taskManager.split("\\r\\n");
            newTaskManager.load(values);
            return newTaskManager;
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void load(String[] values) throws IOException {
        for (int i = 1; i < values.length - 1; i++) {
            if (!values[i].isBlank()) {
                Task task = fromString(values[i]);
                if (task != null && id <= task.getIdTask()) {
                    id = task.getIdTask();
                }
            }
        }
        String arrayHistory = values[values.length - 1];
        List<Integer> saveHistory = historyFromString(arrayHistory);
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
        managerToJson = "id,type,name,status,description,duration,localdatetime,epic\n";
        for (SimpleTask simpleTask : storingSimple.values()) {
            managerToJson += gson.toJson(simpleTask) + "\n";
        }
        for (EpicTask epicTask : storingEpic.values()) {
            managerToJson += gson.toJson(epicTask) + "\n";
        }
        for (Subtask subtask : storingSubtask.values()) {
            managerToJson += gson.toJson(subtask) + "\n";
        }
        managerToJson += "\n" + historyToString(historyManager);
        try {
            kvTaskClient.put(kvTaskClient.getAPI_token(), managerToJson);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
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
                    Duration.ofMinutes(Long.parseLong(jsonObject.get("duration").getAsString())),
                            LocalDateTime.parse(jsonObject.get("startTime").getAsString()),
                            jsonObject.get("epicId").getAsInt());
            createSubtask(subtask);
            return subtask;
        } else if (jsonObject.has("subtaskList")) {
            EpicTask epicTask = new EpicTask(jsonObject.get("nameTask").getAsString(),
                    jsonObject.get("descriptionTask").getAsString(), jsonObject.get("idTask").getAsInt(),
                    StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                    Duration.ofMinutes(Long.parseLong(jsonObject.get("duration").getAsString())),
                    LocalDateTime.parse(jsonObject.get("startTime").getAsString()));
            createEpicTask(epicTask);
            return epicTask;
        } else {
            SimpleTask simpleTask = new SimpleTask(jsonObject.get("nameTask").getAsString(),
                    jsonObject.get("descriptionTask").getAsString(), jsonObject.get("idTask").getAsInt(),
                    StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                    Duration.ofMinutes(Long.parseLong(jsonObject.get("duration").getAsString())),
                    LocalDateTime.parse(jsonObject.get("startTime").getAsString()));
            createSimpleTask(simpleTask);
            return simpleTask;
        }
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }
}
