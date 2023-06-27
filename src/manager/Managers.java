package manager;

import manager.historymanagers.HistoryManager;
import manager.historymanagers.InMemoryHistoryManager;
import manager.taskmanager.FileBackedTaskManager;
import manager.taskmanager.HttpTaskManager;
import manager.taskmanager.TaskManager;

import java.io.IOException;
import java.nio.file.Paths;

public final class Managers {

    public static HttpTaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8081/");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedDefault() {
        return new FileBackedTaskManager("resources\\SaveData.csv");
    }
}
