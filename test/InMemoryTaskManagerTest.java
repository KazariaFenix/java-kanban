import manager.Managers;
import manager.taskmanager.InMemoryTaskManager;
import manager.taskmanager.TaskManager;
import model.EpicTask;
import model.SimpleTask;
import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest <InMemoryTaskManager> {

    @BeforeEach
    public void createTaskManagerObject() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void whenEqualsClearManager() {
        TaskManager newTaskManager = new InMemoryTaskManager();
        assertEquals(newTaskManager.hashCode(), taskManager.hashCode(), "Менеджеры не совпадают");
        assertTrue(newTaskManager.equals(taskManager), "Менеджеры создаются разными");
    }
}
