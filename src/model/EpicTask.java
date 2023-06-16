package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private List<Integer> subtaskList = new ArrayList<>();

    public EpicTask(String nameTask, String descriptionTask, int idTask, StatusTask statusTask, Duration duration,
                    LocalDateTime localDateTime) {
        super(nameTask, descriptionTask, idTask, statusTask, duration, localDateTime);//про поле endTime в задании
    }//говорилось,про удобство. На мой взгляд добавлять поле только в один класс наследник, хотя по сути оно есть
    // у всех, крайне неудобно.
    public List<Integer> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(List<Integer> subtaskList) {
        this.subtaskList = subtaskList;
    }

    public void addSubList(int subId) {
        subtaskList.add(subId);
    }

    public void removeSubtaskList(Integer subId) {
        subtaskList.remove(subId);
    }
    public void clearSubtaskList() {
        subtaskList.clear();
    }
}
