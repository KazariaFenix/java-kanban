package manager;

import manager.historymanagers.HistoryManager;
import manager.historymanagers.InMemoryHistoryManager;
import manager.taskmanager.InMemoryTaskManager;
import manager.taskmanager.TaskManager;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
