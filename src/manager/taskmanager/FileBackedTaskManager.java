package manager.taskmanager;

import manager.historymanagers.HistoryManager;
import manager.exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public static void main(String[] args) {
        FileBackedTaskManager file = loadFromFile("resources" +
                "\\SaveData.csv");

        //System.out.println(file.storingEpic);
        //System.out.println(file.historyManager.getHistory());
        System.out.println(file.getPrioritizedTasks());
        //System.out.println(file.timeMap);

    }

    public static FileBackedTaskManager loadFromFile(String address) {
        Path pathOfFile = Path.of(address);
        FileBackedTaskManager file = new FileBackedTaskManager(pathOfFile);
        try {
            List<String> lines = Files.readAllLines(pathOfFile);
            file.load(lines);
            return file;
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void load(List<String> lines) {
        for (int i = 1; i < lines.size() - 1; i++) {
                Task task = fromString(lines.get(i));
                if (task != null && id <= task.getIdTask()) {
                    id = task.getIdTask();
                }
        }
        List<Integer> saveHistory = historyFromString(lines.get(lines.size() - 1));
        for (int k = 0; k < saveHistory.size(); k++) {
            if (storingSimple.containsKey(saveHistory.get(k))) {
                historyManager.add(storingSimple.get(saveHistory.get(k)));
            } else if (storingEpic.containsKey(saveHistory.get(k))) {
                historyManager.add(storingEpic.get(saveHistory.get(k)));
            } else if (storingSubtask.containsKey(saveHistory.get(k))) {
                historyManager.add(storingSubtask.get(saveHistory.get(k)));
            }
        }
    }

    private void save() {
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.writeString(path, "id,type,name,status,description,duration,localdatetime,epic\n", APPEND);
            for (SimpleTask simpleTask : storingSimple.values()) {
                Files.writeString(path, simpleTask.toStringFile() + "\n", APPEND);
            }
            for (EpicTask epicTask : storingEpic.values()) {
                Files.writeString(path, epicTask.toStringFile() + "\n", APPEND);
            }
            for (Subtask subtask : storingSubtask.values()) {
                Files.writeString(path, subtask.toStringFile() + "\n", APPEND);
            }
            Files.writeString(path, "\n" + historyToString(historyManager), APPEND);
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<String> stringId = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            stringId.add(Integer.toString(task.getIdTask()));
        }
        String listId = String.join(",", stringId);
        return listId;
    }

    private static List<Integer> historyFromString(String value) {
        String[] arrayId = value.split(",");
        List<Integer> saveHistory = new ArrayList<>();
        for (String s : arrayId) {
            if (s.isBlank()) {
                return saveHistory;
            }
            saveHistory.add(Integer.parseInt(s));
        }
        return saveHistory;
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        if (values.length >= 7) {
            if (values[6].equals("null")) {
                values[6] = LocalDateTime.MIN.toString();
            }
            switch (TypesTasks.valueOf(values[1])) {
                case SIMPLETASK:
                    SimpleTask simpleTask = new SimpleTask(values[2], values[4], Integer.parseInt(values[0]),
                            StatusTask.valueOf(values[3]), Duration.ofMinutes(Long.parseLong(values[5])),
                            LocalDateTime.parse(values[6]));
                    if (simpleTask.getStartTime().isEqual(LocalDateTime.MIN)) {
                        simpleTask.setStartTime(null);
                    }
                    createSimpleTask(simpleTask);
                    return simpleTask;
                case EPICTASK:
                    EpicTask epicTask = new EpicTask(values[2], values[4], Integer.parseInt(values[0]),
                            StatusTask.valueOf(values[3]), Duration.ofMinutes(Long.parseLong(values[5])),
                            LocalDateTime.parse(values[6]));
                    if (epicTask.getStartTime().isEqual(LocalDateTime.MIN)) {
                        epicTask.setStartTime(null);
                    }
                    createEpicTask(epicTask);
                    return epicTask;
                default:
                    if (values.length > 7) {
                        Subtask subtask = new Subtask(values[2], values[4], Integer.parseInt(values[0]),
                                StatusTask.valueOf(values[3]), Duration.ofMinutes(Long.parseLong(values[5])),
                                LocalDateTime.parse(values[6]), Integer.parseInt(values[7]));
                        if (subtask.getStartTime().isEqual(LocalDateTime.MIN)) {
                            subtask.setStartTime(null);
                        }
                        createSubtask(subtask);
                        storingEpic.get(subtask.getEpicId()).addSubList(subtask.getIdTask());
                        return subtask;
                    } else {
                        return null;
                    }
            }
        } else {
            return null;
        }
    }

    @Override
    public void createSimpleTask(SimpleTask simpleTask) {
        super.createSimpleTask(simpleTask);
        save();
    }

    @Override
    public void createEpicTask(EpicTask epicTask) {
        super.createEpicTask(epicTask);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void clearSimpleTask() {
        super.clearSimpleTask();
        save();
    }

    @Override
    public void clearEpicTask() {
        super.clearEpicTask();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void deleteIdSimple(int simpleId) {
        super.deleteIdSimple(simpleId);
        save();
    }

    @Override
    public void deleteIdEpicTask(int epicId) {
        super.deleteIdEpicTask(epicId);
        save();
    }

    @Override
    public void deleteIdSubtask(int subId) {
        super.deleteIdSubtask(subId);
        save();
    }

    @Override
    public void updateSimpleTask(SimpleTask simpleTask) {
        super.updateSimpleTask(simpleTask);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public EpicTask getIdEpic(int idEpicTask) {
        EpicTask epicTask = super.getIdEpic(idEpicTask);
        save();
        return epicTask;
    }

    public Subtask getIdSub(int idSub) {
        Subtask subtask = super.getIdSub(idSub);
        save();
        return subtask;
    }

    @Override
    public SimpleTask getIdSimple(int idSimple) {
        SimpleTask simpleTask = super.getIdSimple(idSimple);
        save();
        return simpleTask;
    }

    public Path getPath() {
        return path;
    }
}
