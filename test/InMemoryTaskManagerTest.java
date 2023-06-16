import manager.taskmanager.InMemoryTaskManager;
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
    public void whenExceptionFreeTime() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        Subtask subtask = new Subtask("New1", "NewDesc1", 0,
                StatusTask.DONE, Duration.ofMinutes(7), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        assertEquals(epicTask.getSubtaskList().size(), 0, "Добавилась неподходящая история");
    }
}
