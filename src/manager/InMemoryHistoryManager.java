package manager;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();
    private static final int HISTORY_MAX_VALUE = 10;

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > HISTORY_MAX_VALUE) {
            history.removeFirst();
        }
    }
}
