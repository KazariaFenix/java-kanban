package model;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Integer> subtaskList = new ArrayList<>();

    public EpicTask(String nameTask, String descriptionTask, int idTask, StatusTask statusTask) {
        super(nameTask, descriptionTask, idTask, statusTask);
    }

    public ArrayList<Integer> getSubtaskList() {
        return subtaskList;
    }

    public void addSubtuskList(int subId) {
        subtaskList.add(subId);
    }

    public void removeSubtaskList(Integer subId) {
        subtaskList.remove(subId);
    }
    public void clearSubtaskList() {
        subtaskList.clear();
    }
}
