package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private List<Integer> subtaskList = new ArrayList<>();
    private LocalDateTime endTime;

    public EpicTask(String nameTask, String descriptionTask, int idTask, StatusTask statusTask, Duration duration,
                    LocalDateTime localDateTime) {
        super(nameTask, descriptionTask, idTask, statusTask, duration, localDateTime);
    }

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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return super.toString() +
                " endTime=" + endTime +
                '}';
    }
}
