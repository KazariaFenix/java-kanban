import manager.Managers;
import model.EpicTask;
import model.SimpleTask;
import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    public void createTaskManagerObject() {
        taskManager = Managers.getDefault();
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
        //Очень странное поведение теста, когда все вместе, то выдает ошибку, а по отдельности работает корректно
    }

    @Test
    public void whenExceptionOverTimeMax() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now().plusMonths(15));
        assertFalse(taskManager.checkingFreeTime(simpleTask));
    }

    @Test
    public void getPriority() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        EpicTask epicTask = new EpicTask("EpicTest0", "EpicTestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("SubTest0", "SubDescTest0", 0, StatusTask.NEW,
                Duration.ofMinutes(16), LocalDateTime.now().plusMinutes(15), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask("SubTest0", "SubDescTest0", 0, StatusTask.NEW,
                Duration.ofMinutes(16), null, epicTask.getIdTask());
        taskManager.createSubtask(subtask1);
        assertEquals(epicTask.getDuration().toMinutes(), 16);
        System.out.println(taskManager.getPrioritizedTasks());
        assertEquals(taskManager.getPrioritizedTasks().first(), simpleTask, "Неверная сортировка");
        taskManager.deleteIdEpicTask(epicTask.getIdTask());
        assertEquals(taskManager.getPrioritizedTasks().size(), 1, "Эпик не удалился из listPriority");
        taskManager.deleteIdSimple(simpleTask.getIdTask());
        assertEquals(taskManager.getSimpleTask().size(), taskManager.getPrioritizedTasks().size(),
                "История не удалилась полностью");
    }
}
