package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Task {
    private String nameTask;
    private String descriptionTask;
    private int idTask;
    private StatusTask statusTask;
    private Duration duration;
    private LocalDateTime startTime;



    public Task(String nameTask, String descriptionTask, int idTask, StatusTask statusTask, Duration duration,
                LocalDateTime startTime) { // была ошибка насчет форматирования, но это просто перенос строки
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.idTask = idTask;
        this.statusTask = statusTask;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public StatusTask getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(StatusTask statusTask) {
        this.statusTask = statusTask;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() == null) {
            return null;
        }
        return getStartTime().plus(getDuration());
    }

    public String toStringFile() {
        if (startTime == null) {
            return idTask + "," + TypesTasks.valueOf(this.getClass().getSimpleName().toUpperCase()).name()
                    + "," + nameTask + "," + statusTask + "," + descriptionTask + "," + duration.toMinutes() + ","
                    + "null";
        }
        return idTask + "," + TypesTasks.valueOf(this.getClass().getSimpleName().toUpperCase()).name()
                + "," + nameTask + "," + statusTask + "," + descriptionTask + "," + duration.toMinutes() + ","
                + startTime.toString();
    }

    @Override
    public String toString() {
        return "Task{" +
                "nameTask='" + nameTask + '\'' +
                ", descriptionTask='" + descriptionTask + '\'' +
                ", idTask=" + idTask +
                ", statusTask=" + statusTask +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask && Objects.equals(nameTask, task.nameTask)
                && Objects.equals(descriptionTask, task.descriptionTask) && statusTask == task.statusTask
                && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTask, descriptionTask, idTask, statusTask, duration, startTime);
    }
}
