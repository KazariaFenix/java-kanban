package manager;

import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private Path path;

    FileBackedTasksManager(Path path) {
        this.path = path;
    }

    public static void main(String[] args) {
        FileBackedTasksManager file = loadFromFile("resources" +
                "\\SaveData.csv");

        System.out.println(file.storingEpic);
        System.out.println(file.historyManager.getHistory());

    }

    public static FileBackedTasksManager loadFromFile(String address) {
        Path pathOfFile = Path.of(address);
        FileBackedTasksManager file = new FileBackedTasksManager(pathOfFile);
        try {
            List<String> lines = Files.readAllLines(pathOfFile);
            file.load(lines);//дополнительно теперь ничего вызывать не надо, просто декомпозировал задачу на 2 метода
            return file;
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private void load(List<String> lines) {
        for (int i = 1; i < lines.size() - 2; i++) {
            Task task = fromString(lines.get(i));
            if (id <= task.getIdTask()) {
                id = task.getIdTask() + 1;
            }
        }
        List<Integer> saveHistory = historyFromString(lines.get(lines.size() - 1));
        for (int k = 0; k < saveHistory.size(); k++) {
            if (storingSimple.containsKey(saveHistory.get(k))) {
                historyManager.add(storingSubtask.get(saveHistory.get(k)));
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
            Files.writeString(path, "id,type,name,status,description,epic\n", APPEND);
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
            saveHistory.add(Integer.parseInt(s));
        }
        return saveHistory;
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        switch (TypesTasks.valueOf(values[1])) {
            case SIMPLETASK:
                SimpleTask simpleTask = new SimpleTask(values[2], values[4], Integer.parseInt(values[0]),
                        StatusTask.valueOf(values[3]));
                storingSimple.put(simpleTask.getIdTask(), simpleTask);
                return simpleTask;
            case EPICTASK:
                EpicTask epicTask = new EpicTask(values[2], values[4], Integer.parseInt(values[0]),
                        StatusTask.valueOf(values[3]));
                storingEpic.put(epicTask.getIdTask(), epicTask);
                return epicTask;
            default:
                if (values.length > 5) {
                    Subtask subtask = new Subtask(values[2], values[4], Integer.parseInt(values[0]),
                            StatusTask.valueOf(values[3]), Integer.parseInt(values[5]));
                    storingSubtask.put(subtask.getIdTask(), subtask);
                    storingEpic.get(subtask.getEpicId()).addSubList(subtask.getIdTask());
                    return subtask;
                } else {
                    return null;
                }
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
