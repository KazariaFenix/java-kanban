import manager.HistoryManager;
import manager.Managers;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest <T extends HistoryManagerTest> {
    HistoryManager historyManager;
    SimpleTask simpleTask;
    EpicTask epicTask;
    Subtask zeroSubtask;
    Subtask firstSubtask;

    @BeforeEach
    public void createObjectInMemoryHistoryManager() {
        historyManager = Managers.getDefaultHistory();
        simpleTask = new SimpleTask("Test0", "TestDesc0", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        epicTask = new EpicTask("EpicTest0", "EpicTestDesc0", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        zeroSubtask = new Subtask("SubTest0", "SubDescTest0", 3, StatusTask.NEW,
                Duration.ofMinutes(3), LocalDateTime.now(), epicTask.getIdTask());
        firstSubtask = new Subtask("SubTest1", "SubDescTest1", 4, StatusTask.NEW,
                Duration.ofMinutes(3), LocalDateTime.now(), epicTask.getIdTask());
    }

    @Test
    public void whenAddHistoryNormal() {
        historyManager.add(simpleTask);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(1, history.size(), "История содержит неверное количество элементов");
    }

    @Test
    public void whenAddHistoryClearList() {
        historyManager.add(simpleTask);
        historyManager.remove(simpleTask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не null");
        assertEquals(history.size(), 0, "История содержит неверное количество элементов");
    }

    @Test
    public void whenAddHistoryDuplication() {
        historyManager.add(simpleTask);
        historyManager.add(simpleTask);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(1, history.size(), "История содержит неверное количество элементов");
    }

    @Test
    public void whenAddHistoryAndDeleteBegin() {
        historyManager.add(simpleTask);
        historyManager.add(epicTask);
        historyManager.add(zeroSubtask);
        historyManager.add(firstSubtask);
        historyManager.remove(simpleTask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(history.size(), 3, "История содержит неверное количество элементов");
    }

    @Test
    public void whenAddHistoryAndDeleteMiddle() {
        historyManager.add(simpleTask);
        historyManager.add(epicTask);
        historyManager.add(zeroSubtask);
        historyManager.add(firstSubtask);
        historyManager.remove(zeroSubtask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(history.size(), 3, "История содержит неверное количество элементов");
    }

    @Test
    public void whenAddHistoryAndDeleteEnd() {
        historyManager.add(simpleTask);
        historyManager.add(epicTask);
        historyManager.add(zeroSubtask);
        historyManager.add(firstSubtask);
        historyManager.remove(firstSubtask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(history.size(), 3, "История содержит неверное количество элементов");
    }

    @Test
    public void whenRemoveHistoryNormal() {
        historyManager.add(simpleTask);
        historyManager.add(epicTask);
        historyManager.remove(epicTask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(history.size(), 1, "История содержит неверное количество элементов");
    }

    @Test
    public void whenRemoveHistoryClearList() {
        historyManager.add(simpleTask);
        historyManager.remove(simpleTask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(history.size(), 0, "История содержит неверное количество элементов");
    }

    @Test
    public void whenRemoveHistoryDuplication() {
        historyManager.add(simpleTask);
        historyManager.add(simpleTask);
        historyManager.add(epicTask);
        historyManager.add(epicTask);
        historyManager.remove(simpleTask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(1, history.size(), "История содержит неверное количество элементов");
    }

    @Test
    public void whenRemoveHistoryDeleteAndBegin() {
        historyManager.add(simpleTask);
        historyManager.add(epicTask);
        historyManager.add(zeroSubtask);
        historyManager.add(firstSubtask);
        historyManager.remove(simpleTask.getIdTask());
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая");
        assertEquals(history.size(), 3, "История содержит неверное количество элементов");
    }
}//очень много повторяющихся методов, на мой взгляд нарушается принцип DRY, но, конечно, могу ошибаться
