package manager;

import model.EpicTask;
import model.SimpleTask;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    List<Task> getHistoryManager();

    void createSimpleTask(SimpleTask simpleTask);

    void createEpicTask(EpicTask epicTask);

    void createSubtask(Subtask subtask);

    List<SimpleTask> getSimpleTask();

    List<EpicTask> getEpicTask();

    List<Subtask> getSubtask();

    void clearSimpleTask();

    void clearEpicTask();

    void clearSubtask();

    SimpleTask getIdSimple(int idSimple);

    EpicTask getIdEpic(int idEpic);

    Subtask getIdSub(int idSub);

    void deleteIdSimple(int simpleId);

    void deleteIdEpicTask(int epicId);

    void deleteIdSubtask(int subId);

    List<Subtask> getSubOfEpicTask(int epicId);

    void updateSimpleTask(SimpleTask simpleTask);

    void updateEpicTask(EpicTask epicTask);

    void updateSubtask(Subtask subtask);

    LocalDateTime getEndTime(Task task);

    LocalDateTime getEndTimeOfEpic(int idEpic);

    void clearTimeMap(Task task);

    boolean checkingFreeTime(Task task);

    LinkedHashMap<LocalDateTime, Boolean> createTimeMap(LocalDateTime start);

    TreeSet<Task> getPrioritizedTasks();
}
