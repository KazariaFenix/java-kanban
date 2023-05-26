package manager;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private List<String> lines;
    private Path path;
    FileBackedTasksManager (List<String> lines, Path path) {
        this.lines = lines;
        this.path = path;
    }

    public static void main(String[] args) {
        FileBackedTasksManager file = loadFromFile("resources" +
                "\\SaveData.csv");
        file.load();
        System.out.println(file.storingEpic);
        System.out.println(file.historyManager.getHistory());

    }

    public static FileBackedTasksManager  loadFromFile(String address) { // Так как при сохранении, я не использовал
        Path pathOfFile = Path.of(address);//класс Fail, то и при загрузки, мне достаточно строки, потому чуть изменил
        try (Reader reader = Files.newBufferedReader(pathOfFile)){//параметры метода
            List<String> linesLoad = Files.readAllLines(pathOfFile);
            return new FileBackedTasksManager(linesLoad, pathOfFile);
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public void load() {
        List<Integer> saveHistory = historyFromString(lines.get(lines.size() - 1));
        for (int k = 0; k < saveHistory.size(); k++) {
            for (int i = 1; i < lines.size() - 2; i++) {
                Task task = fromString(lines.get(i));
                if (task != null && saveHistory.get(k) == task.getIdTask()) {
                    historyManager.add(task);
                }
            }
        }
    }

    private void save() {
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8); ) {
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.writeString(path,"id,type,name,status,description,epic\n", APPEND);
            for (SimpleTask simpleTask : storingSimple.values()) {
                Files.writeString(path,simpleTask.toStringFile() + "\n", APPEND);
            }
            for (EpicTask epicTask : storingEpic.values()) {
                Files.writeString(path,epicTask.toStringFile() + "\n", APPEND);
            }
            for (Subtask subtask : storingSubtask.values()) {
                Files.writeString(path, subtask.toStringFile() + "\n", APPEND);
            }
            Files.writeString(path, "\n" + historyToString(historyManager), APPEND);
        }catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public Task fromString(String value) {
        String[] values = value.split(",");
        switch (TypesTasks.valueOf(values[1])) {
            case SIMPLETASK:
                SimpleTask simpleTask = new SimpleTask(values[2], values[4], Integer.parseInt(values[0]),
                        StatusTask.valueOf(values[3]));
                storingSimple.put(simpleTask.getIdTask(),simpleTask);
                return simpleTask;
            case EPICTASK:
                EpicTask epicTask = new EpicTask (values[2], values[4], Integer.parseInt(values[0]),
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



    public static String historyToString(HistoryManager manager) {
        List<String> stringId = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            stringId.add(Integer.toString(task.getIdTask()));
        }
        String listId = String.join(",", stringId);
        return listId;
    }

    public static List<Integer> historyFromString(String value) {
        String[] arrayId = value.split(",");
        ArrayList<Integer> saveHistory = new ArrayList<>();
        for (String s : arrayId) {
            saveHistory.add(Integer.parseInt(s));
        }
        return saveHistory;
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

    public List<String> getLines() {
        return lines;
    }

    public Path getPath() {
        return path;
    }
}
