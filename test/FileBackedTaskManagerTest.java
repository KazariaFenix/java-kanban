import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import model.EpicTask;
import model.SimpleTask;
import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    public void createObjectFileBacked() {
        taskManager = new FileBackedTaskManager(Paths.get("resources\\SaveData.csv"));
    }


    /*@Test
    public void whenSaveAndLoadClearTaskList() {
        IndexOutOfBoundsException e = assertThrows(
                IndexOutOfBoundsException.class,
                (() -> FileBackedTaskManager.loadFromFile("resources\\SaveData.csv"))
        );
    } // для этого метода, необходимо очистить файл SaveData от любых данных*/

    @Test
    public void whenSaveAndLoadClearTaskListAddDelete() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        taskManager.deleteIdSimple(simpleTask.getIdTask());
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile("resources\\SaveData.csv");
        assertNotNull(newManager, "Менеджер не создался");
        assertEquals(newManager.getEpicTask().size(), 0, "Неизвестные задачи загрузились");
        assertEquals(newManager.getSubtask().size(), 0, "Неизвестные задачи загрузились");
        assertEquals(newManager.getSimpleTask().size(), 0, "Неизвестные задачи загрузились");
    }


    @Test
    public void whenSaveAndLoadNormal() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        EpicTask epicTask = new EpicTask("EpicTest0", "EpicTestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("SubTest0", "SubDescTest0", 0, StatusTask.NEW,
                Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(15), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        taskManager.getIdEpic(epicTask.getIdTask());
        taskManager.getIdSimple(simpleTask.getIdTask());
        taskManager.getIdSub(subtask.getIdTask());
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile("resources\\SaveData.csv");

        assertNotNull(newTaskManager, "Файлы не сохранились или не загрузились");
        assertEquals(newTaskManager, taskManager, "Файлы сохранились или загрузились не верно");
    }

    @Test
    public void whenSaveAndLoadEpicWithoutSubtask() {
        EpicTask epicTask = new EpicTask("EpicTest0", "EpicTestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        taskManager.getIdEpic(epicTask.getIdTask());
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile("resources\\SaveData.csv");

        assertNotNull(newTaskManager, "Файлы не сохранились или не загрузились");
        assertNotEquals(newTaskManager, taskManager, "Файлы сохранились или загрузились не верно");
    }

    @Test
    public void whenSaveAndLoadWithoutHistory() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        EpicTask epicTask = new EpicTask("EpicTest0", "EpicTestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("SubTest0", "SubDescTest0", 0, StatusTask.NEW,
                Duration.ofMinutes(16), LocalDateTime.now().plusMinutes(15), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile("resources\\SaveData.csv");

        assertNotNull(newTaskManager, "Файлы не сохранились или не загрузились");
        assertEquals(newTaskManager, taskManager, "Файлы сохранились или загрузились не верно");
    }

    @Test
    public void whenSaveAndLoadWrongWay() {
        ManagerSaveException exp = assertThrows(
                ManagerSaveException.class,
                (() -> FileBackedTaskManager.loadFromFile("wrongway"))
        );
    }
}