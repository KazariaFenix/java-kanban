package manager;

import model.EpicTask;
import model.SimpleTask;
import model.Subtask;

import java.util.List;

public interface TaskManager {

    void addSimpleTask(SimpleTask simpleTask);

    void addEpicTask(EpicTask epicTask);

    void addSubtask(Subtask subtask);

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
}
