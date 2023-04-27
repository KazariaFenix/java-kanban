package manager;

public final class Managers {
    private static TaskManager taskManager;
    private static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
