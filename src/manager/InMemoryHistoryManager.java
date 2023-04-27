package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        history.add(task);
    }
}
