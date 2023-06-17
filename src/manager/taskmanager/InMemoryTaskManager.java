package manager.taskmanager;

import manager.exception.FreeTimeException;
import manager.historymanagers.HistoryManager;
import manager.Managers;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, SimpleTask> storingSimple = new HashMap<>();
    protected Map<Integer, EpicTask> storingEpic = new HashMap<>();
    protected Map<Integer, Subtask> storingSubtask = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> listPriority = new TreeSet<>
            (Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparing(Task::getNameTask));
    protected int id = 1;

    @Override
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    @Override
    public void createSimpleTask(SimpleTask simpleTask) {
        simpleTask.setIdTask(id);
        id++;
        if (checkingFreeTime(simpleTask)) {
            listPriority.add(simpleTask);
            storingSimple.put(simpleTask.getIdTask(), simpleTask);
        } else {
            id--;
            try {
                throw new FreeTimeException("Задача пересекается по времени с другими и не может быть добавлена");
            } catch (FreeTimeException e) {
                System.out.println(e.getMessage() + ". Название задачи: " + simpleTask.getNameTask());
            }
        }
    }

    @Override
    public void createEpicTask(EpicTask epicTask) {
        epicTask.setIdTask(id);
        epicTask.setStatusTask(StatusTask.NEW);
        id++;
        storingEpic.put(epicTask.getIdTask(), epicTask);
        fillEndTimeOfEpic(epicTask.getIdTask());
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (storingEpic.containsKey(subtask.getEpicId())) {
            subtask.setIdTask(id);
            id++;
            if (checkingFreeTime(subtask)) {
                storingSubtask.put(subtask.getIdTask(), subtask);
                EpicTask epicTask = storingEpic.get(subtask.getEpicId());
                epicTask.addSubList(subtask.getIdTask());
                fillEpicStatus(subtask.getEpicId());
                fillEndTimeOfEpic(subtask.getEpicId());
                listPriority.add(subtask);
            } else {
                id--;
                try {
                    throw new FreeTimeException("Задача пересекается по времени с другими и не может быть добавлена");
                } catch (FreeTimeException e) {
                    System.out.println(e.getMessage() + ". Название задачи: " + subtask.getNameTask());
                }
            }
        }
    }

    @Override
    public List<SimpleTask> getSimpleTask() {
        return new ArrayList<>(storingSimple.values());
    }

    @Override
    public List<EpicTask> getEpicTask() {
        return new ArrayList<>(storingEpic.values());
    }

    @Override
    public List<Subtask> getSubtask() {
        return new ArrayList<>(storingSubtask.values());
    }

    @Override
    public void clearSimpleTask() {
        if (!storingSimple.isEmpty()) {
            for (Integer idSimple : storingSimple.keySet()) {
                historyManager.remove(idSimple);
                listPriority.remove(storingSimple.get(idSimple));
            }
            storingSimple.clear();
        }
    }

    @Override
    public void clearEpicTask() {
        if (!storingEpic.isEmpty()) {
            for (Integer idEpic : storingEpic.keySet()) {
                historyManager.remove(idEpic);
            }
            for (Integer idSub : storingSubtask.keySet()) {
                historyManager.remove(idSub);
                listPriority.remove(storingSubtask.get(idSub));
            }
            storingEpic.clear();
            storingSubtask.clear();
        }
    }

    @Override
    public void clearSubtask() {
        if (!storingSubtask.isEmpty()) {
            for (Integer idSub : storingSubtask.keySet()) {
                listPriority.remove(storingSubtask.get(idSub));
                historyManager.remove(idSub);
            }
            storingSubtask.clear();
            for (EpicTask epicTask : storingEpic.values()) {
                epicTask.clearSubtaskList();
                fillEpicStatus(epicTask.getIdTask());
                fillEndTimeOfEpic(epicTask.getIdTask());
            }
        }
    }

    @Override
    public SimpleTask getIdSimple(int idSimple) {
        if (storingSimple.get(idSimple) != null) {
            historyManager.add(storingSimple.get(idSimple));
        }
        return storingSimple.get(idSimple);
    }

    @Override
    public EpicTask getIdEpic(int idEpic) {
        if (storingEpic.containsKey(idEpic)) {
            historyManager.add(storingEpic.get(idEpic));
        }
        return storingEpic.get(idEpic);
    }

    @Override
    public Subtask getIdSub(int idSub) {
        if (storingSubtask.containsKey(idSub)) {
            historyManager.add(storingSubtask.get(idSub));
        }
        return storingSubtask.get(idSub);
    }

    @Override
    public void deleteIdSimple(int simpleId) {
        if (storingSimple.containsKey(simpleId)) {
            listPriority.remove(storingSimple.get(simpleId));
            storingSimple.remove(simpleId);
        }
        historyManager.remove(simpleId);
    }

    @Override
    public void deleteIdEpicTask(int epicId) {
        if (storingEpic.containsKey(epicId)) {
            EpicTask epicTask = storingEpic.get(epicId);
            for (Integer subId : epicTask.getSubtaskList()) {
                listPriority.remove(storingSubtask.get(subId));
                storingSubtask.remove(subId);
                historyManager.remove(subId);
            }
            epicTask.clearSubtaskList();
            storingEpic.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    @Override
    public void deleteIdSubtask(int subId) {
        if (storingSubtask.containsKey(subId)) {
            Subtask subtask = storingSubtask.get(subId);
            EpicTask epicTask = storingEpic.get(subtask.getEpicId());
            epicTask.removeSubtaskList(subtask.getIdTask());
            listPriority.remove(subtask);
            storingSubtask.remove(subId);
            fillEpicStatus(subtask.getEpicId());
            fillEndTimeOfEpic(epicTask.getIdTask());
            historyManager.remove(subId);
        }
    }

    @Override
    public List<Subtask> getSubOfEpicTask(int epicId) {
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
        if (storingSimple.containsKey(simpleTask.getIdTask())) {
            if (checkingFreeTime(simpleTask)) {
                SimpleTask oldSimple = storingSimple.get(simpleTask.getIdTask());
                listPriority.remove(oldSimple);
                storingSimple.put(simpleTask.getIdTask(), simpleTask);
                listPriority.add(simpleTask);
            } else {
                try {
                    throw new FreeTimeException("Задача пересекается по времени с другими и " +
                            "не может быть модифицирована");
                } catch (FreeTimeException e) {
                    System.out.println(e.getMessage() + ". Название задачи: " + simpleTask.getNameTask());
                }
            }
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (storingEpic.containsKey(epicTask.getIdTask())) {
            EpicTask oldEpicTask = storingEpic.get(epicTask.getIdTask());
            epicTask.setStatusTask(oldEpicTask.getStatusTask());
            epicTask.setSubtaskList(oldEpicTask.getSubtaskList());
            epicTask.setStartTime(oldEpicTask.getStartTime());
            epicTask.setDuration(oldEpicTask.getDuration());
            storingEpic.put(epicTask.getIdTask(), epicTask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (storingSubtask.containsKey(subtask.getIdTask())) {
            if (checkingFreeTime(subtask)) {
                Subtask oldSubtask = storingSubtask.get(subtask.getIdTask());
                subtask.setEpicId(oldSubtask.getEpicId());
                listPriority.remove(oldSubtask);
                storingSubtask.put(oldSubtask.getIdTask(), subtask);
                fillEpicStatus(subtask.getEpicId());
                fillEpicStatus(subtask.getEpicId());
                listPriority.add(subtask);
            } else {
                try {
                    throw new FreeTimeException("Задача пересекается по времени с другими и " +
                            "не может быть модифицирована");
                } catch (FreeTimeException e) {
                    System.out.println(e.getMessage() + ". Название задачи: " + subtask.getNameTask());
                }
            }
        }
    }

    private void fillEpicStatus(int epicId) {
        EpicTask epicTask = storingEpic.get(epicId);
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

    private void fillEndTimeOfEpic(int epicId) {
        EpicTask epicTask = storingEpic.get(epicId);
        if (epicTask.getSubtaskList().size() < 1) {
            epicTask.setStartTime(null);
            epicTask.setDuration(Duration.ZERO);
            epicTask.setEndTime(epicTask.getEndTime());
            return;
        }
        for (Integer subId : epicTask.getSubtaskList()) {
            Subtask subtask = storingSubtask.get(subId);
            if (epicTask.getSubtaskList().size() == 1) {
                if (subtask.getStartTime() == null) {
                    epicTask.setStartTime(null);
                    epicTask.setDuration(Duration.ZERO);
                    epicTask.setEndTime(epicTask.getEndTime());
                    return;
                }
                epicTask.setDuration(subtask.getDuration());
                epicTask.setStartTime(subtask.getStartTime());
                epicTask.setEndTime(epicTask.getEndTime());
            } else {
                if (subtask.getStartTime() == null) {
                    return;
                }
                if (subtask.getStartTime().isBefore(epicTask.getStartTime())) {
                    epicTask.setStartTime(subtask.getStartTime());
                }
                if (subtask.getEndTime().isAfter(epicTask.getStartTime().plus(epicTask.getDuration()))) {
                    epicTask.setDuration(Duration.between(epicTask.getStartTime(), subtask.getEndTime()));
                }
                epicTask.setEndTime(epicTask.getEndTime());
            }
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(listPriority);
    }

    @Override
    public boolean checkingFreeTime(Task task) {
        int count = 0;
        if (task.getStartTime() == null) {
            return true;
        }
        for (Task prioritizedTask : getPrioritizedTasks()) {
            if (getPrioritizedTasks().size() > 0) {
                if (prioritizedTask.getStartTime() == null) {
                    continue;
                }
                if (!(task.getStartTime().isAfter(prioritizedTask.getEndTime())
                        || task.getEndTime().isBefore(prioritizedTask.getStartTime()))) {
                    count++;
                    if (task.getIdTask() == prioritizedTask.getIdTask()) {
                        count--;
                    }
                }
            } else {
                return true;
            }
        }
        if (count == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return id == that.id && Objects.equals(storingSimple, that.storingSimple)
                && Objects.equals(storingEpic, that.storingEpic) && Objects.equals(storingSubtask, that.storingSubtask)
                && Objects.equals(historyManager.getHistory(), that.historyManager.getHistory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(storingSimple, storingEpic, storingSubtask, historyManager.getHistory(), id);
    }
}
