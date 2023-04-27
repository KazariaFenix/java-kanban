package manager;

import tasks.EpicTask;
import tasks.SimpleTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, SimpleTask> storingSimple = new HashMap<>();
    private HashMap<Integer, EpicTask> storingEpic = new HashMap<>();
    private HashMap<Integer, Subtask> storingSubtask = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private ArrayList<Task> history = historyManager.getHistory();
    private int id = 1;

    private int idHistory = 0;

    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void addSimpleTask(SimpleTask simpleTask) {
        simpleTask.setIdTask(id);
        id++;
        storingSimple.put(simpleTask.getIdTask(), simpleTask);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        epicTask.setIdTask(id);
        epicTask.setStatusTask(StatusTask.NEW);
        id++;
        storingEpic.put(epicTask.getIdTask(), epicTask);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (storingEpic.containsKey(subtask.getEpicId())) {
            subtask.setIdTask(id);
            id++;
            storingSubtask.put(subtask.getIdTask(), subtask);
            EpicTask epicTask = storingEpic.get(subtask.getEpicId());
            epicTask.addSubtuskList(subtask.getIdTask());
            fillEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public ArrayList<SimpleTask> getArrayListSimpleTask() {
        return new ArrayList<>(storingSimple.values());
    }

    @Override
    public ArrayList<EpicTask> getArrayListEpicTask() {
        return new ArrayList<>(storingEpic.values());
    }

    @Override
    public ArrayList<Subtask> getArrayListSubtask() {
        return new ArrayList<>(storingSubtask.values());
    }

    @Override
    public void clearSimpleTask() {
        if (!storingSimple.isEmpty()) {
            storingSimple.clear();
        }
    }

    @Override
    public void clearEpicTask() {
        if (!storingEpic.isEmpty()) {
            storingEpic.clear();
            storingSubtask.clear();
        }
    }

    @Override
    public void clearSubtask() {
        if (!storingSubtask.isEmpty()) {
            storingSubtask.clear();
            for (EpicTask epicTask : storingEpic.values()) {
                epicTask.clearSubtaskList();
                fillEpicStatus(epicTask.getIdTask());
            }
        }
    }

    @Override
    public SimpleTask getIdSimple(int idSimple) {
        if (history.size() < 10) {
            history.add(storingSimple.get(idSimple));
        } else {
            history.set(idHistory, storingSimple.get(idSimple));
            idHistory++;
            if (idHistory > 9) {
                idHistory = 0;
            }
        }
        return storingSimple.get(idSimple);
    }

    @Override
    public EpicTask getIdEpic(int idEpic) {
        if (history.size() < 10) {
            history.add(storingEpic.get(idEpic));
        } else {
            history.set(idHistory, storingEpic.get(idEpic));
            idHistory++;
            if (idHistory > 9) {
                idHistory = 0;
            }
        }
        return storingEpic.get(idEpic);
    }

    @Override
    public Subtask getIdSub(int idSub) {
        if (history.size() < 10) {
            history.add(storingSubtask.get(idSub));
        } else {
            history.set(idHistory, storingSubtask.get(idSub));
            idHistory++;
            if (idHistory > 9) {
                idHistory = 0;
            }
        }
        return storingSubtask.get(idSub);
    }

    @Override
    public void deleteIdSimple(int simpleId) {
        if (storingSimple.containsKey(simpleId)) {
            storingSimple.remove(simpleId);
        }
    }

    @Override
    public void deleteIdEpicTask(int epicId) {
        if (storingEpic.containsKey(epicId)) {
            EpicTask epicTask = storingEpic.get(epicId);
            for (Integer subId : epicTask.getSubtaskList()) {
                storingSubtask.remove(subId);
            }
            storingEpic.remove(epicId);
        }
    }

    @Override
    public void deleteIdSubtask(int subId) {
        if (storingSubtask.containsKey(subId)) {
            Subtask subtask = storingSubtask.get(subId);
            EpicTask epicTask = getIdEpic(subtask.getEpicId());
            epicTask.removeSubtaskList(subtask.getIdTask());
            storingSubtask.remove(subId);
            fillEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public ArrayList<Subtask> getArrayListSubOfEpicTask(int epicId) {
        ArrayList<Subtask> arrayListSubOfEpic = new ArrayList<>();
        if (storingEpic.containsKey(epicId)) {
            EpicTask epicTask = storingEpic.get(epicId);
            for (Integer subId : epicTask.getSubtaskList()) {
                arrayListSubOfEpic.add(storingSubtask.get(subId));
            }
        }
        return arrayListSubOfEpic;
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        if (storingSimple.containsKey(simpleTask)) {
            storingSimple.put(simpleTask.getIdTask(), simpleTask);
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (storingEpic.containsKey(epicTask.getIdTask())) {
            EpicTask oldEpicTask = storingEpic.get(epicTask.getIdTask());
            oldEpicTask.setDescriptionTask(epicTask.getDescriptionTask());
            oldEpicTask.setNameTask(epicTask.getNameTask());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (storingSubtask.containsKey(subtask.getIdTask())) {
            storingSubtask.put(subtask.getIdTask(), subtask);
            fillEpicStatus(subtask.getEpicId());
        }
    }

    private void fillEpicStatus(int epicId) {
        EpicTask epicTask = getIdEpic(epicId);
        double countStatus = 0;

        if (epicTask.getSubtaskList().size() > 0) {
            for (Integer subId : epicTask.getSubtaskList()) {
                Subtask subArray = storingSubtask.get(subId);
                if (subArray != null) {
                    switch (subArray.getStatusTask()) {
                        case NEW:
                            countStatus += 10;
                            break;
                        case IN_PROGRESS:
                            countStatus += 20;
                            break;
                        default:
                            countStatus += 30;
                    }
                }
            }
            if ((countStatus / epicTask.getSubtaskList().size()) <= 10) {
                epicTask.setStatusTask(StatusTask.NEW);
            } else if ((countStatus / epicTask.getSubtaskList().size()) >= 30) {
                epicTask.setStatusTask(StatusTask.DONE);
            } else {
                epicTask.setStatusTask(StatusTask.IN_PROGRESS);
            }
        } else {
            epicTask.setStatusTask(StatusTask.NEW);
        }
    }
}
