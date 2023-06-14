package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, SimpleTask> storingSimple = new HashMap<>();
    protected Map<Integer, EpicTask> storingEpic = new HashMap<>();
    protected Map<Integer, Subtask> storingSubtask = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected LinkedHashMap<LocalDateTime, Boolean> timeMap;

    protected TreeSet<Task> listPriority = new TreeSet<>(Comparator.comparing(Task::getLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder())));
    protected int id = 1;

    InMemoryTaskManager() {
        timeMap = createTimeMap(LocalDateTime.MIN);
    }

    @Override
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    @Override
    public void createSimpleTask(SimpleTask simpleTask) {
        simpleTask.setIdTask(id);
        id++;
        storingSimple.put(simpleTask.getIdTask(), simpleTask);
        if (simpleTask.getLocalDateTime() == null || simpleTask.getLocalDateTime()
                .isAfter(LocalDateTime.now().plusYears(1))) {
            if (listPriority.first().getLocalDateTime() == null) {
                simpleTask.setLocalDateTime(LocalDateTime.now().plusYears(2));
            } else {
                simpleTask.setLocalDateTime(listPriority.first().getLocalDateTime().plusYears(2));
            }
            listPriority.add(simpleTask);
            return;
        }
        listPriority.add(simpleTask);
        if (!checkingFreeTime(simpleTask)) {
            deleteIdSimple(simpleTask.getIdTask());
            id--;
        }
    }

    @Override
    public void createEpicTask(EpicTask epicTask) {
        epicTask.setIdTask(id);
        epicTask.setStatusTask(StatusTask.NEW);
        id++;
        storingEpic.put(epicTask.getIdTask(), epicTask);
        getEndTimeOfEpic(epicTask.getIdTask());
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (storingEpic.containsKey(subtask.getEpicId())) {
            subtask.setIdTask(id);
            id++;
            storingSubtask.put(subtask.getIdTask(), subtask);
            EpicTask epicTask = storingEpic.get(subtask.getEpicId());
            epicTask.addSubList(subtask.getIdTask());
            if (subtask.getLocalDateTime() == null || subtask.getLocalDateTime()
                    .isAfter(LocalDateTime.now().plusYears(1))) {
                if (listPriority.first().getLocalDateTime() == null) {
                    subtask.setLocalDateTime(LocalDateTime.now().plusYears(2));
                } else {
                    subtask.setLocalDateTime(listPriority.first().getLocalDateTime().plusYears(2));
                }
                listPriority.add(subtask);
                fillEpicStatus(subtask.getEpicId());
                return;
            }
            clearTimeMap(epicTask);
            listPriority.add(subtask);
            getEndTime(storingEpic.get(subtask.getEpicId()));
            fillEpicStatus(subtask.getEpicId());
            if (!checkingFreeTime(storingEpic.get(subtask.getEpicId()))) {
                deleteIdSubtask(subtask.getIdTask());
                id--;
                listPriority.remove(subtask);
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
                clearTimeMap(storingSimple.get(idSimple));
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
                clearTimeMap(storingEpic.get(idEpic));
                listPriority.remove(storingEpic.get(idEpic));
            }
            for (Integer idSub : storingSubtask.keySet()) {
                historyManager.remove(idSub);
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
                clearTimeMap(epicTask);
                getEndTime(epicTask);
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
            clearTimeMap(storingSimple.get(simpleId));
            listPriority.remove(storingSimple.get(simpleId));
            storingSimple.remove(simpleId);
        }
        historyManager.remove(simpleId);
    }

    @Override
    public void deleteIdEpicTask(int epicId) {
        if (storingEpic.containsKey(epicId)) {
            EpicTask epicTask = storingEpic.get(epicId);
            clearTimeMap(epicTask);
            for (Integer subId : epicTask.getSubtaskList()) {
                listPriority.remove(storingSubtask.get(subId));
                storingSubtask.remove(subId);
                historyManager.remove(subId);
            }
            epicTask.clearSubtaskList();
            storingEpic.remove(epicId);

        }
        historyManager.remove(epicId);
    }

    @Override
    public void deleteIdSubtask(int subId) {
        if (storingSubtask.containsKey(subId)) {
            Subtask subtask = storingSubtask.get(subId);
            EpicTask epicTask = storingEpic.get(subtask.getEpicId());
            epicTask.removeSubtaskList(subtask.getIdTask());
            clearTimeMap(epicTask);
            listPriority.remove(storingSubtask.get(subId));
            storingSubtask.remove(subId);
            fillEpicStatus(subtask.getEpicId());
            getEndTime(epicTask);
            if (epicTask.getSubtaskList().size() > 0) {
                checkingFreeTime(epicTask);
            }
        }
        historyManager.remove(subId);
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
            SimpleTask oldSimple = storingSimple.get(simpleTask.getIdTask());
            clearTimeMap(storingSimple.get(simpleTask.getIdTask()));
            if (simpleTask.getLocalDateTime() == null || simpleTask.getLocalDateTime()
                    .isAfter(LocalDateTime.now().plusYears(1))) {
                if (listPriority.first().getLocalDateTime() == null) {
                    simpleTask.setLocalDateTime(LocalDateTime.now().plusYears(2));
                } else {
                    simpleTask.setLocalDateTime(listPriority.first().getLocalDateTime().plusYears(2));
                }
                listPriority.add(simpleTask);
                return;
            }
            if (checkingFreeTime(simpleTask)) {
                listPriority.remove(storingSimple.get(simpleTask.getIdTask()));
                storingSimple.put(simpleTask.getIdTask(), simpleTask);
                listPriority.add(simpleTask);
            } else {
                listPriority.remove(storingSimple.get(simpleTask.getIdTask()));
                storingSimple.put(simpleTask.getIdTask(), oldSimple);
                listPriority.add(oldSimple);
            }
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (storingEpic.containsKey(epicTask.getIdTask())) {
            EpicTask oldEpicTask = storingEpic.get(epicTask.getIdTask());
            epicTask.setStatusTask(oldEpicTask.getStatusTask());
            epicTask.setSubtaskList(oldEpicTask.getSubtaskList());
            epicTask.setLocalDateTime(oldEpicTask.getLocalDateTime());
            epicTask.setDuration(oldEpicTask.getDuration());
            storingEpic.put(epicTask.getIdTask(), epicTask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (storingSubtask.containsKey(subtask.getIdTask())) {
            Subtask oldSubtask = storingSubtask.get(subtask.getIdTask());
            subtask.setEpicId(oldSubtask.getEpicId());
            clearTimeMap(storingEpic.get(subtask.getEpicId()));
            listPriority.remove(storingSubtask.get(subtask.getIdTask()));
            storingSubtask.put(oldSubtask.getIdTask(), subtask);
            fillEpicStatus(subtask.getEpicId());
            if (subtask.getLocalDateTime() == null || subtask.getLocalDateTime()
                    .isAfter(LocalDateTime.now().plusYears(1))) {
                if (listPriority.first().getLocalDateTime() == null) {
                    subtask.setLocalDateTime(LocalDateTime.now().plusYears(2));
                } else {
                    subtask.setLocalDateTime(listPriority.first().getLocalDateTime().plusYears(2));
                }
                listPriority.add(subtask);
                return;
            }
            getEndTime(storingEpic.get(subtask.getEpicId()));
            if (checkingFreeTime(storingEpic.get(subtask.getEpicId()))) {
                listPriority.add(subtask);
            } else {
                storingSubtask.put(oldSubtask.getIdTask(), oldSubtask);
                fillEpicStatus(oldSubtask.getEpicId());
                getEndTime((storingEpic.get(oldSubtask.getEpicId())));
                checkingFreeTime(storingEpic.get(oldSubtask.getEpicId()));
                listPriority.add(oldSubtask);
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

    @Override
    public LocalDateTime getEndTime(Task task) {
        LocalDateTime endTime;
        if (!(task instanceof EpicTask)) {
            endTime = task.getLocalDateTime().plus(task.getDuration());
        } else {
            return getEndTimeOfEpic(task.getIdTask());
        }
        return endTime;
    }

    @Override
    public LocalDateTime getEndTimeOfEpic(int epicId) {
        EpicTask epicTask = storingEpic.get(epicId);
        LocalDateTime endTimeEpic = LocalDateTime.now().plusYears(1);
        if (epicTask.getSubtaskList().size() < 1) {
            epicTask.setLocalDateTime(LocalDateTime.now().plusYears(1));
            epicTask.setDuration(Duration.ZERO);
        }
        for (Integer subId : epicTask.getSubtaskList()) {
            Subtask subtask = storingSubtask.get(subId);
            if (epicTask.getSubtaskList().size() == 1) {
                epicTask.setDuration(subtask.getDuration());
                epicTask.setLocalDateTime(subtask.getLocalDateTime());
                endTimeEpic = epicTask.getLocalDateTime().plus(epicTask.getDuration());
                return endTimeEpic;
            } else {
                if (subtask.getLocalDateTime().isBefore(epicTask.getLocalDateTime())) {
                    epicTask.setLocalDateTime(subtask.getLocalDateTime());
                    endTimeEpic = epicTask.getLocalDateTime().plus(epicTask.getDuration());
                }
                if (subtask.getLocalDateTime().plus(subtask.getDuration())
                        .isAfter(epicTask.getLocalDateTime().plus(epicTask.getDuration()))) {
                    epicTask.setDuration(Duration.between(epicTask.getLocalDateTime(),
                            subtask.getLocalDateTime().plus(subtask.getDuration())));
                    endTimeEpic = epicTask.getLocalDateTime().plus(epicTask.getDuration());
                    return endTimeEpic;
                }
            }
        }
        return endTimeEpic;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return listPriority;
    }

    @Override
    public LinkedHashMap<LocalDateTime, Boolean> createTimeMap(LocalDateTime start) {
        LocalDateTime startingPoint;
        if (start.isAfter(LocalDateTime.MIN)) {
            startingPoint = start.minusMinutes(5);
        } else {
            startingPoint = LocalDateTime.now();
        }
        final int cellsInYear = 4 * 24 * 365;
        timeMap = new LinkedHashMap<>();
        for (int i = 0; i < cellsInYear; i++) {
            timeMap.put(startingPoint, true);
            startingPoint = startingPoint.plusMinutes(15);
        }
        return timeMap;
    }

    @Override
    public boolean checkingFreeTime(Task task) {
        LocalDateTime taskTime = task.getLocalDateTime().plusNanos(0);
        try {
            for (LocalDateTime localDateTime : timeMap.keySet()) {
                if (task.getLocalDateTime().plus(task.getDuration()).isAfter(localDateTime.plusDays(367))) {
                    throw new OverTimeMapException("Время выполнения задачи превышает горизонт планирования");
                }
                break;
            }
            for (LocalDateTime localDateTime : timeMap.keySet()) {
                if ((taskTime.isAfter(localDateTime) || taskTime.isEqual(localDateTime))
                        && Duration.between(localDateTime, taskTime).toMinutes() < Duration.ofMinutes(15).toMinutes()) {
                    taskTime = taskTime.plusMinutes(15);
                    if (timeMap.get(localDateTime) == false) {
                        throw new FreeTimeException("Время для выполнения задачи занято");
                    }
                }
                if (taskTime.isAfter(task.getLocalDateTime().plus(task.getDuration()))
                        && Duration.between(localDateTime, taskTime).toMinutes() < Duration.ofMinutes(15).toMinutes()) {
                    break;
                }
            }
        } catch (FreeTimeException e) {
            System.out.println(e.getMessage() + " " + task.getNameTask());
            return false;
        } catch (OverTimeMapException e) {
            System.out.println(e.getMessage() + " " + task.getNameTask());
            return false;
        }
        taskTime = task.getLocalDateTime();
        for (Map.Entry<LocalDateTime, Boolean> entry : timeMap.entrySet()) {
            if (taskTime.isAfter(entry.getKey()) || taskTime == entry.getKey()
                    && Duration.between(entry.getKey(), taskTime).toMinutes() < Duration.ofMinutes(15).toMinutes()) {
                entry.setValue(false);
                taskTime = taskTime.plusMinutes(15);
            }
            if (taskTime.isAfter(task.getLocalDateTime().plus(task.getDuration()))) {
                break;
            }
        }
        return true;
    }

    @Override
    public void clearTimeMap(Task task) {
        LocalDateTime taskTime = task.getLocalDateTime();
        for (Map.Entry<LocalDateTime, Boolean> entry : timeMap.entrySet()) {
            if (taskTime.isAfter(entry.getKey()) &&
                    Duration.between(entry.getKey(), taskTime).toMinutes() <= Duration.ofMinutes(15).toMinutes()) {
                entry.setValue(true);
                taskTime = taskTime.plusMinutes(15);
            }
            if (taskTime.isAfter(getEndTime(task))) {
                break;
            }
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
