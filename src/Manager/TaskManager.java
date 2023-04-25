package Manager;

import Tasks.EpicTask;
import Tasks.SimpleTask;
import Tasks.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, SimpleTask> storingSimple = new HashMap<>();
    private HashMap<Integer, EpicTask> storingEpic = new HashMap<>();
    private HashMap<Integer, Subtask> storingSubtask = new HashMap<>();
    private int id = 1;

    public void addSimpleTask(SimpleTask simpleTask) {
        simpleTask.setIdTask(id);
        id++;
        storingSimple.put(simpleTask.getIdTask(), simpleTask);
    }

    public void addEpicTask(EpicTask epicTask) {
        epicTask.setIdTask(id);
        epicTask.setStatusTask("NEW");
        id++;
        storingEpic.put(epicTask.getIdTask(), epicTask);
    }

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

    public ArrayList<SimpleTask> getArrayListSimpleTask() {
        return new ArrayList<>(storingSimple.values());
    }

    public ArrayList<EpicTask> getArrayListEpicTask() {
        return new ArrayList<>(storingEpic.values());
    }

    public ArrayList<Subtask> getArrayListSubtask() {
        return new ArrayList<>(storingSubtask.values());
    }

    public void clearSimpleTask() {
        if (!storingSimple.isEmpty()) {
            storingSimple.clear();
        }
    }

    public void clearEpicTask() {
        if (!storingEpic.isEmpty()) {
            storingEpic.clear();
            storingSubtask.clear();
        }
    }

    public void clearSubtask() {
        if (!storingSubtask.isEmpty()) {
            storingSubtask.clear();
            for (EpicTask epicTask : storingEpic.values()) {
                epicTask.clearSubtaskList();
                fillEpicStatus(epicTask.getIdTask());
            }
        }
    }

    public SimpleTask getIdSimple(int idSimple) {
        return storingSimple.get(idSimple);
    }

    public EpicTask getIdEpic(int idEpic) {
        return storingEpic.get(idEpic);
    }

    public Subtask getIdSub(int idSub) {
        return storingSubtask.get(idSub);
    }

    public void deleteIdSimple(int simpleId) {
        if (storingSimple.containsKey(simpleId)) {
            storingSimple.remove(simpleId);
        }
    }

    public void deleteIdEpicTask(int epicId) {
        if (storingEpic.containsKey(epicId)) {
            EpicTask epicTask = storingEpic.get(epicId);
            for (Integer subId : epicTask.getSubtaskList()) {
                storingSubtask.remove(subId);
            }
            storingEpic.remove(epicId);
        }
    }

    public void deleteIdSubtask(int subId) {
        if (storingSubtask.containsKey(subId)) {
            Subtask subtask = storingSubtask.get(subId);
            EpicTask epicTask = getIdEpic(subtask.getEpicId());
            epicTask.removeSubtaskList(subtask.getIdTask());
            storingSubtask.remove(subId);
            fillEpicStatus(subtask.getEpicId());
        }
    }

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

    public void updateSimpleTask(SimpleTask simpleTask) {
        if (storingSimple.containsKey(simpleTask)) {
            storingSimple.put(simpleTask.getIdTask(), simpleTask);
        }
    }

    public void updateEpicTask(EpicTask epicTask) {
        if (storingEpic.containsKey(epicTask.getIdTask())) {
            EpicTask oldEpicTask = storingEpic.get(epicTask.getIdTask());
            oldEpicTask.setDescriptionTask(epicTask.getDescriptionTask());
            oldEpicTask.setNameTask(epicTask.getNameTask());
        }
    }

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
                        case "NEW":
                            countStatus += 10;
                            break;
                        case "IN_PROGRESS":
                            countStatus += 20;
                            break;
                        default:
                            countStatus += 30;
                    }
                }
            }
            if ((countStatus / epicTask.getSubtaskList().size()) <= 10) {
                epicTask.setStatusTask("NEW");
            } else if ((countStatus / epicTask.getSubtaskList().size()) >= 30) {
                epicTask.setStatusTask("DONE");
            } else {
                epicTask.setStatusTask("IN_PROGRESS");
            }
        } else {
            epicTask.setStatusTask("NEW");
        }
    }
}
