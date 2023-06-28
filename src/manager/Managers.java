package manager;

import manager.historymanagers.HistoryManager;
import manager.historymanagers.InMemoryHistoryManager;
import manager.taskmanager.FileBackedTaskManager;
import manager.taskmanager.HttpTaskManager;

import java.io.IOException;

public final class Managers {

    public static HttpTaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8081/register");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedDefault() {
        return new FileBackedTaskManager("resources\\SaveData.csv");
    }
}
