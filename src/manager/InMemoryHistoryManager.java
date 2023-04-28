package manager;

import model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();
    private final int historyMaxValue = 10;

    @Override
    public LinkedList<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > historyMaxValue) {
            history.removeFirst();
        }
    }
}
