package manager;

import tasks.EpicTask;
import tasks.SimpleTask;
import tasks.Subtask;

import java.util.ArrayList;

public interface TaskManager {

    void addSimpleTask(SimpleTask simpleTask);

    void addEpicTask(EpicTask epicTask);

    void addSubtask(Subtask subtask);

    ArrayList<SimpleTask> getArrayListSimpleTask();

    ArrayList<EpicTask> getArrayListEpicTask();

    ArrayList<Subtask> getArrayListSubtask();

    void clearSimpleTask();

    void clearEpicTask();

    void clearSubtask();

    SimpleTask getIdSimple(int idSimple);

    EpicTask getIdEpic(int idEpic);

    Subtask getIdSub(int idSub);

    void deleteIdSimple(int simpleId);

    void deleteIdEpicTask(int epicId);

    void deleteIdSubtask(int subId);

    ArrayList<Subtask> getArrayListSubOfEpicTask(int epicId);

    void updateSimpleTask(SimpleTask simpleTask);

    void updateEpicTask(EpicTask epicTask);

    void updateSubtask(Subtask subtask);
}
