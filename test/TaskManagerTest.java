import manager.taskmanager.TaskManager;
import model.EpicTask;
import model.SimpleTask;
import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {

    protected T taskManager;

    @Test
    public void checkingStatusEpic() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.DONE,
                Duration.ofMinutes(3), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        assertEquals(epicTask.getStatusTask(), StatusTask.NEW, "Статус задачи отличается от NEW");
        Subtask subtask = new Subtask("SubTest0", "SubDescTest0", 0, StatusTask.NEW,
                Duration.ofMinutes(3), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        assertEquals(epicTask.getStatusTask(), subtask.getStatusTask(),
                "Статус Эпика отличается от статуса подзадачи");
        Subtask subtask1 = new Subtask("SubTest1", "SubDescTest1", 0, StatusTask.NEW,
                Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(15), epicTask.getIdTask());

        taskManager.createSubtask(subtask1);
        assertEquals(epicTask.getStatusTask(), StatusTask.NEW, "Статус Эпика отличается от NEW");
        taskManager.updateSubtask(new Subtask("SubTest0", "SubDescTest0", subtask.getIdTask(),
                StatusTask.DONE, Duration.ofMinutes(3), LocalDateTime.now(), epicTask.getIdTask()));
        taskManager.updateSubtask(new Subtask("SubTest1", "SubDescTest1", subtask1.getIdTask(),
                StatusTask.DONE, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(15), epicTask.getIdTask()));
        assertEquals(epicTask.getStatusTask(), StatusTask.DONE, "Статус Эпика отличается от DONE");
        taskManager.updateSubtask(new Subtask("SubTest0", "SubDescTest0", subtask.getIdTask(),
                StatusTask.NEW, Duration.ofMinutes(3), LocalDateTime.now(), epicTask.getIdTask()));
        assertEquals(epicTask.getStatusTask(), StatusTask.IN_PROGRESS,
                "Статус задачи отличается от IN_PROGRESS");
        taskManager.updateSubtask(new Subtask("SubTest1", "SubDescTest1", subtask1.getIdTask(),
                StatusTask.IN_PROGRESS, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(40),
                epicTask.getIdTask()));
        taskManager.updateSubtask(new Subtask("SubTest0", "SubDescTest0", subtask.getIdTask(),
                StatusTask.IN_PROGRESS, Duration.ofMinutes(3), LocalDateTime.now().plusMinutes(5),
                epicTask.getIdTask()));
        assertEquals(epicTask.getStatusTask(), StatusTask.IN_PROGRESS,
                "Статус Эпика отличается от IN_PROGRESS");
    }

    @Test
    public void addNewSimpleTask() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        final SimpleTask taskTest = taskManager.getIdSimple(simpleTask.getIdTask());

        assertNotNull(taskTest, "Задача не найдена");
        assertEquals(simpleTask, taskTest, "Задачи не совпадают");
        final List<SimpleTask> simpleTaskList = taskManager.getSimpleTask();

        assertNotNull(simpleTaskList, "Список простых задач не найден");
        assertEquals(1, simpleTaskList.size(), "Неверное количество задач");
        assertEquals(simpleTask, simpleTaskList.get(0), "Задачи не совпадают");
    }

    @Test
    public void addNewEpicTask() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        final EpicTask taskTest = taskManager.getIdEpic(epicTask.getIdTask());

        assertNotNull(taskTest, "Задача не найдена");
        assertEquals(epicTask, taskTest, "Задачи не совпадают");
        final List<EpicTask> epicTaskList = taskManager.getEpicTask();

        assertNotNull(epicTaskList, "Список Эпик задач не найден");
        assertEquals(1, epicTaskList.size(), "Неверное количество задач");
        assertEquals(epicTask, epicTaskList.get(0), "Задачи не совпадают");
        assertEquals(epicTask.getStatusTask(), StatusTask.NEW, "Статус пустого эпика не совпадает с NEW");
    }

    @Test
    public void addNewSubTask() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        final Subtask subTest = taskManager.getIdSub(subtask.getIdTask());

        assertEquals(subtask.getIdTask(), epicTask.getSubtaskList().get(0), "Задача не в списке Эпика");
        assertNotNull(subTest, "Задача не найдена");
        assertEquals(subtask, subTest, "Задачи не совпадают");
        final List<Subtask> subTaskList = taskManager.getSubtask();

        assertNotNull(subTaskList, "Список подзадач не найден");
        assertEquals(1, subTaskList.size(), "Неверное количество задач");
        assertEquals(subtask, subTaskList.get(0), "Задачи не совпадают");
        assertEquals(epicTask.getStatusTask(), StatusTask.IN_PROGRESS, "Неверно рассчитан статус Эпика");
        assertEquals(epicTask.getIdTask(), subtask.getEpicId(), "Неверный epicID у Сабтаски");
    }

    @Test
    public void whenGetListWithSimpleTask() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        final SimpleTask taskTest = taskManager.getIdSimple(simpleTask.getIdTask());

        assertNotNull(taskTest, "Задача не найдена");
        assertEquals(simpleTask, taskTest, "Задачи не совпадают");
        final List<SimpleTask> listSimple = taskManager.getSimpleTask();

        assertNotNull(listSimple, "Список простых задач не найден");
        assertEquals(1 ,listSimple.size(), "Неверное количество задач");
        assertEquals(simpleTask, listSimple.get(0), "Задачи не совпадают");
    }

    @Test
    public void whenGetListWithEpicTask() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        final EpicTask taskTest = taskManager.getIdEpic(epicTask.getIdTask());

        assertNotNull(taskTest, "Задача не найдена");
        assertEquals(epicTask, taskTest, "Задачи не совпадают");
        final List<EpicTask> listEpic = taskManager.getEpicTask();

        assertNotNull(listEpic, "Список эпик задач не найден");
        assertEquals(1 ,listEpic.size(), "Неверное количество задач");
        assertEquals(epicTask, listEpic.get(0), "Задачи не совпадают");
    }

    @Test
    public void whenGetListWithSubTask() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        final Subtask newSub = taskManager.getIdSub(subtask.getIdTask());

        assertNotNull(newSub, "Задача не найдена");
        assertEquals(newSub, subtask, "Задачи не совпадают");
        assertEquals(subtask.getIdTask(), epicTask.getSubtaskList().get(0), "Задача не в списке Эпика");
        assertEquals(epicTask.getStatusTask(), StatusTask.IN_PROGRESS, "Неверно рассчитан статус Эпика");
        List<Subtask> listSub = taskManager.getSubtask();
        assertNotNull(listSub, "Список подзадач не найден");
        assertEquals(1 ,listSub.size(), "Неверное количество задач");
        assertEquals(subtask, listSub.get(0), "Задачи не совпадают");
    }

    @Test
    public void whenGetSimpleTaskById() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        final SimpleTask newSimple = taskManager.getIdSimple(simpleTask.getIdTask());

        taskManager.getIdSimple(simpleTask.getIdTask());
        assertNotNull(newSimple, "Задача не найдена");
        assertEquals(newSimple, simpleTask, "Задачи не совпадают");
        final List<SimpleTask> listSimple = taskManager.getSimpleTask();

        assertNotNull(listSimple, "Список простых задач не найден");
        assertEquals(1 ,listSimple.size(), "Неверное количество задач");
        assertEquals(simpleTask, listSimple.get(0), "Задачи не совпадают");
        assertEquals(taskManager.getHistoryManager().size(), 1, "Неверное сохранение истории");
    }

    @Test
    public void whenGetEpicTaskById() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        final EpicTask newEpic = taskManager.getIdEpic(epicTask.getIdTask());

        taskManager.getIdEpic(epicTask.getIdTask());
        assertNotNull(newEpic, "Задача не найдена");
        assertEquals(epicTask, newEpic, "Задачи не совпадают");
        final List<EpicTask> listEpic = taskManager.getEpicTask();

        assertNotNull(listEpic, "Список эпик задач не найден");
        assertEquals(1 ,listEpic.size(), "Неверное количество задач");
        assertEquals(epicTask, listEpic.get(0), "Задачи не совпадают");
        assertEquals(taskManager.getHistoryManager().size(), 1, "Неверное сохранение истории");
    }

    @Test
    public void whenGetSubtaskById() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        taskManager.getIdSub(subtask.getIdTask());
        assertEquals(subtask.getIdTask(), epicTask.getSubtaskList().get(0), "Задача не в списке Эпика");
        assertEquals(epicTask.getStatusTask(), StatusTask.IN_PROGRESS, "Неверно рассчитан статус Эпика");
        final Subtask newSub = taskManager.getIdSub(subtask.getIdTask());

        assertNotNull(newSub, "Задача не найдена");
        assertEquals(newSub, subtask, "Задачи не совпадают");
        final List<Subtask> listSub = taskManager.getSubtask();

        assertNotNull(listSub, "Список подзадач не найден");
        assertEquals(1 ,listSub.size(), "Неверное количество задач");
        assertEquals(subtask, listSub.get(0), "Задачи не совпадают");
        assertEquals(taskManager.getHistoryManager().size(), 1, "Неверное сохранение истории");
    }

    @Test
    public void whenMapSimpleTaskClear() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        taskManager.clearSimpleTask();
        assertNull(taskManager.getIdSimple(simpleTask.getIdTask()), "Мапа простых задач не была очищена");
        assertNotEquals(taskManager.getIdSimple(simpleTask.getIdTask()), simpleTask,
                "Задача не была очищена");
        assertEquals(taskManager.getSimpleTask().size(), 0, "Мапа простых задач не очищен");
    }

    @Test
    public void whenMapEpicTaskClear() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        taskManager.clearEpicTask();
        assertNull(taskManager.getIdEpic(epicTask.getIdTask()), "Мапа эпик не очищена");
        assertNull(taskManager.getIdSub(subtask.getIdTask()), "Мапа подзадач не очищена");
        assertNotEquals(taskManager.getIdEpic(epicTask.getIdTask()), epicTask, "Задача эпик не была очищена");
        assertNotEquals(taskManager.getIdSub(subtask.getIdTask()), subtask, "Подзадача не была очищена");
        assertEquals(taskManager.getEpicTask().size(), 0, "Список эпик не был очищена");
        assertEquals(taskManager.getSubtask().size(), 0, "Список подзадач не был очищена");
    }

    @Test
    public void whenMapSubTaskClear() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        taskManager.clearSubtask();
        assertNull(taskManager.getIdSub(subtask.getIdTask()), "Мапа подзадач не была очищена");
        assertNotEquals(taskManager.getIdSub(subtask.getIdTask()), subtask,
                "Подзадача не была очищена");
        assertEquals(epicTask.getSubtaskList().size(), 0, "Список подзадач в эпике не очищен");
        assertEquals(epicTask.getStatusTask(), StatusTask.NEW, "Неверно рассчитан статус эпика");
        assertEquals(taskManager.getSubtask().size(), 0, "Список подзадач не был очищена");
    }

    @Test
    public void whenSimpleTaskDelete() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        taskManager.deleteIdSimple(simpleTask.getIdTask());
        assertNull(taskManager.getIdSimple(simpleTask.getIdTask()), "Мапа простых задач не была очищена");
        assertNotEquals(taskManager.getIdSimple(simpleTask.getIdTask()), simpleTask,
                "Задача не была очищена");
        assertEquals(taskManager.getSimpleTask().size(), 0, "Мапа простых задач не очищен");
    }

    @Test
    public void whenEpicTaskDelete() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        taskManager.deleteIdEpicTask(epicTask.getIdTask());
        assertNull(taskManager.getIdEpic(epicTask.getIdTask()), "Мапа эпик не очищена");
        assertNull(taskManager.getIdSub(subtask.getIdTask()), "Мапа подзадач не очищена");
        assertEquals(epicTask.getSubtaskList().size(), 0,  "Список подзадач в эпике не очищен");
        assertNotEquals(taskManager.getIdEpic(epicTask.getIdTask()), epicTask, "Задача эпик не была очищена");
        assertNotEquals(taskManager.getIdSub(subtask.getIdTask()), subtask, "Подзадача не была очищена");
        assertEquals(taskManager.getEpicTask().size(), 0, "Список эпик не был очищена");
        assertEquals(taskManager.getSubtask().size(), 0, "Список подзадач не был очищена");
    }

    @Test
    public void whenSubTaskDelete() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        taskManager.deleteIdSubtask(subtask.getIdTask());
        assertNull(taskManager.getIdSub(subtask.getIdTask()), "Мапа подзадач не была очищена");
        assertNotEquals(taskManager.getIdSub(subtask.getIdTask()), subtask,
                "Подзадача не была очищена");
        assertEquals(epicTask.getSubtaskList().size(), 0,  "Список подзадач в эпике не очищен");
        assertEquals(epicTask.getStatusTask(), StatusTask.NEW, "Неверно рассчитан статус эпика");
        assertEquals(taskManager.getSubtask().size(), 0, "Мапа подзадач не был очищена");
    }

    @Test
    public void whenSimpleTaskUpdate() {
        SimpleTask oldSimpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(oldSimpleTask);
        SimpleTask simpleTask = new SimpleTask("New1", "NewDesc0", oldSimpleTask.getIdTask(),
                StatusTask.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.updateSimpleTask(simpleTask);
        assertEquals(simpleTask.getIdTask(), oldSimpleTask.getIdTask(), "Id модификаторы не совпадают");
        assertNotEquals(simpleTask, oldSimpleTask, "Обновление задачи не произошло");
        final SimpleTask taskTest = taskManager.getIdSimple(simpleTask.getIdTask());

        assertNotNull(taskTest, "Задача не найдена");
        assertEquals(simpleTask, taskTest, "Задачи не совпадают");
        final List<SimpleTask> simpleTaskList = taskManager.getSimpleTask();

        assertNotNull(simpleTaskList, "Список простых задач не найден");
        assertEquals(1, simpleTaskList.size(), "Неверное количество задач");
        assertEquals(simpleTask, simpleTaskList.get(0), "Задачи не совпадают");
    }

    @Test
    public void whenEpicTaskUpdate() {
        EpicTask oldEpicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(oldEpicTask);
        EpicTask epicTask = new EpicTask("New1", "NewDesc1", oldEpicTask.getIdTask(),
                StatusTask.IN_PROGRESS, Duration.ofMinutes(8), LocalDateTime.now());

        taskManager.updateEpicTask(epicTask);
        assertEquals(epicTask.getIdTask(), oldEpicTask.getIdTask(), "Id модификаторы не совпадают");
        assertEquals(epicTask.getStatusTask(), oldEpicTask.getStatusTask(), "Статус Эпика изменился");
        assertNotEquals(epicTask, oldEpicTask, "Обновление эпика не произошло");
        final EpicTask taskTest = taskManager.getIdEpic(epicTask.getIdTask());

        assertNotNull(taskTest, "Задача не найдена");
        assertEquals(epicTask, taskTest, "Задачи не совпадают");
        final List<EpicTask> epicTaskList = taskManager.getEpicTask();

        assertNotNull(epicTaskList, "Список Эпик задач не найден");
        assertEquals(1, epicTaskList.size(), "Неверное количество задач");
        assertEquals(epicTask, epicTaskList.get(0), "Задачи не совпадают");
        assertEquals(epicTask.getStatusTask(), StatusTask.NEW, "Статус пустого эпика не совпадает с NEW");
    }

    @Test
    public void whenSubtaskUpdate() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask oldSubtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(oldSubtask);
        Subtask subtask = new Subtask("New1", "NewDesc1", oldSubtask.getIdTask(),
                StatusTask.DONE, Duration.ofMinutes(7), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.updateSubtask(subtask);
        assertEquals(subtask.getIdTask(), oldSubtask.getIdTask(), "Id модификаторы не совпадают");
        assertNotEquals(subtask, oldSubtask, "Обновление подзадач не произошло");
        assertEquals(subtask.getEpicId(), oldSubtask.getEpicId(), "Задачи принадлежат разным эпикам");
        final Subtask subTest = taskManager.getIdSub(subtask.getIdTask());

        assertEquals(subtask.getIdTask(), epicTask.getSubtaskList().get(0), "Задача не в списке Эпика");
        assertNotNull(subTest, "Задача не найдена");
        assertEquals(subtask, subTest, "Задачи не совпадают");
        final List<Subtask> subTaskList = taskManager.getSubtask();

        assertNotNull(subTaskList, "Список подзадач не найден");
        assertEquals(1, subTaskList.size(), "Неверное количество задач");
        assertEquals(subtask, subTaskList.get(0), "Задачи не совпадают");
        assertEquals(epicTask.getStatusTask(), StatusTask.DONE, "Неверно рассчитан статус Эпика");
        assertEquals(epicTask.getIdTask(), subtask.getEpicId(), "Неверный epicID у Сабтаски");
    }

    @Test
    public void whenGetSubtaskListByEpicTask() {
        EpicTask epicTask = new EpicTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Test0", "TestDesc0", 0, StatusTask.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now(), epicTask.getIdTask());

        taskManager.createSubtask(subtask);
        final List<Subtask> getterList = taskManager.getSubOfEpicTask(epicTask.getIdTask());

        assertNotNull(getterList, "Список подзадач метода getSubOfEpicTask(int epicId) не найден");
        assertEquals(1, getterList.size(), "Неверное количество задач");
        assertEquals(subtask, getterList.get(0), "Задачи не совпадают");
        final Subtask subTest = taskManager.getIdSub(subtask.getIdTask());

        assertEquals(subtask.getIdTask(), epicTask.getSubtaskList().get(0), "Задача не в списке Эпика");
        assertNotNull(subTest, "Задача не найдена");
        assertEquals(subtask, subTest, "Задачи не совпадают");
        final List<Subtask> subTaskList = taskManager.getSubtask();

        assertNotNull(subTaskList, "Список подзадач метода getSubtask() не найден");
        assertEquals(1, subTaskList.size(), "Неверное количество задач");
        assertEquals(subtask, subTaskList.get(0), "Задачи не совпадают");
        assertEquals(getterList, subTaskList, "Списки полученные из разных методов не равны");
        assertEquals(epicTask.getStatusTask(), StatusTask.IN_PROGRESS, "Неверно рассчитан статус Эпика");
        assertEquals(epicTask.getIdTask(), subtask.getEpicId(), "Неверный epicID у Сабтаски");
    }

    @Test
    public void whenFreeTimeTask() {
        SimpleTask oldSimpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(oldSimpleTask);
        SimpleTask simpleTask = new SimpleTask("New1", "NewDesc0", 0,
                StatusTask.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.createSimpleTask(simpleTask);
        assertEquals(taskManager.getSimpleTask().size(), 1, "Добавлена задача, нарушающая пересечение");
    }

    @Test
    public void getPriority() {
        SimpleTask simpleTask = new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), null);

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
        assertEquals(taskManager.getPrioritizedTasks().get(0), subtask, "Неверная сортировка");
        taskManager.deleteIdEpicTask(epicTask.getIdTask());
        assertEquals(taskManager.getPrioritizedTasks().size(), 1, "Эпик не удалился из listPriority");
        taskManager.deleteIdSimple(simpleTask.getIdTask());
        assertEquals(taskManager.getSimpleTask().size(), taskManager.getPrioritizedTasks().size(),
                "История не удалилась полностью");
        assertEquals(taskManager.getHistoryManager().size(), 0, "Неверное сохранение истории");
    }
}
