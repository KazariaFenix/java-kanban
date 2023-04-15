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
        if (storingEpic.containsKey(subtask.epicId)) {
            subtask.setIdTask(id);
            id++;
            storingSubtask.put(subtask.getIdTask(), subtask);
            EpicTask epicTask = storingEpic.get(subtask.getEpicId());
            epicTask.addSubtuskList(subtask.getIdTask());
            for (Integer idSub : epicTask.getSubtaskList()) {
                Subtask sub = storingSubtask.get(idSub);
                if (epicTask.getSubtaskList().size() >= 2) {
                    if (!(epicTask.getStatusTask().equals(sub.getStatusTask()))) {
                        epicTask.setStatusTask("IN_PROGRESS");
                    }
                } else {
                    epicTask.setStatusTask(sub.getStatusTask());
                }
            }
        }
    }

    public String printListSimpleTask() {
        if (!(storingSimple.isEmpty())) {
            for (Integer idTask : storingSimple.keySet()) {
                return storingSimple.get(idTask).toString();
            }
        }
        return "Простых задач не найдено";
    }

    public String printListEpicTaskandSubtask() { // Подзадачи без епиков печатать будет странно, потому объединил
        String printTask = "";                    // два метода для печати в одном
        if (!(storingEpic.isEmpty())) {
            for (Integer idTask : storingEpic.keySet()) {
                EpicTask epicTask = storingEpic.get(idTask);
                printTask += epicTask.toString();
                for (Integer subId : epicTask.getSubtaskList()) {
                    if (storingSubtask.get(subId) != null) {
                        printTask += storingSubtask.get(subId).toString();
                    }
                }
            }
            return printTask;
        }
        return "Сложносоставных задач с подзадачами нет";
    }

    public void clearSimpleTask() {
        if (!(storingSimple.isEmpty())) {
            storingSimple.clear();
        }
    }

    public void clearEpicTask() {
        if (!(storingEpic.isEmpty())) {
            storingEpic.clear();
            storingSubtask.clear();
        }
    }

    public void clearSubtask() {
        if (!(storingSubtask.isEmpty())) {
            storingSubtask.clear();
        }
    }

    public SimpleTask getIdSimple(int idSimple) {
        if (storingSimple.containsKey(idSimple)) {
            return storingSimple.get(idSimple);
        }
        System.out.println("Задача не найдена");
        return null;
    }

    public EpicTask getIdEpic(int idEpic) {
        if (storingEpic.containsKey(idEpic)) {
            return storingEpic.get(idEpic);
        }
        System.out.println("Задача не найдена");
        return null;
    }

    public Subtask getIdSub(int idSub) {
        if (storingSubtask.containsKey(idSub)) {
            return storingSubtask.get(idSub);
        }
        System.out.println("Задача не найдена");
        return null;
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
            storingSubtask.remove(subId);
        }
    }

    public String printArrayListSubOfEpicTask(int epicId) {
        if (storingEpic.containsKey(epicId)) {
            String subtask = "Список подзадач, многосоставной задачи под номером id " + epicId + ":\n";
            EpicTask epicTask = storingEpic.get(epicId);
            for (Integer subId : epicTask.getSubtaskList()) {
                if (storingSubtask.get(subId) != null) {
                    subtask += storingSubtask.get(subId).toString();
                }
            }
            return subtask;
        }
        return "Многосостовная задача не найдена";
    }

    public void updateSimpleTask(SimpleTask simpleTask) {
        if (storingSimple.containsKey(simpleTask)) {
            storingSimple.put(simpleTask.getIdTask(), simpleTask);
        }
    }

    public void updateEpicTask(EpicTask epicTask) {
        if (storingEpic.containsKey(epicTask.getIdTask())) {
            EpicTask oldEpicTask = storingEpic.get(epicTask.getIdTask());
            epicTask.setStatusTask(oldEpicTask.getStatusTask());
            storingEpic.put(epicTask.getIdTask(), epicTask);
            epicTask.addCopyArrayList(oldEpicTask.getSubtaskList());
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (storingSubtask.containsKey(subtask.getIdTask())) {
            storingSubtask.put(subtask.getIdTask(), subtask);
            EpicTask epicTask = getIdEpic(subtask.getEpicId());
            double countStatus = 0;

            for (Integer subId : epicTask.getSubtaskList()) {
                Subtask subArray = storingSubtask.get(subId);
                switch (subArray.getStatusTask()) {
                    case "NEW":
                        countStatus += 1;
                        break;
                    case "IN_PROGRESS":
                        countStatus += 2;
                        break;
                    default:
                        countStatus += 3;
                }
            }
            if ((countStatus / epicTask.getSubtaskList().size()) <= 1) {
                epicTask.setStatusTask("NEW");
            } else if ((countStatus / epicTask.getSubtaskList().size()) >= 3) {
                epicTask.setStatusTask("DONE");
            } else {
                epicTask.setStatusTask("IN_PROGRESS");
            }
        }
    }
}
