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
    private LocalDateTime localDateTime;



    public Task(String nameTask, String descriptionTask, int idTask, StatusTask statusTask, Duration duration,
                LocalDateTime localDateTime) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.idTask = idTask;
        this.statusTask = statusTask;
        this.duration = duration;
        this.localDateTime = localDateTime;
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

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "\nНазвание задачи: " + nameTask + "\nОписание задачи: " + descriptionTask + "\nId задачи: " + idTask +
                "\nСтатус задачи: " + statusTask.name() + "\nВремя выполнения: " + duration.toMinutes()
                + "\nДата создание: " + localDateTime.toString();
    }

    public String toStringFile() {
        return idTask + "," + TypesTasks.valueOf(this.getClass().getSimpleName().toUpperCase()).name()
                + "," + nameTask + "," + statusTask + "," + descriptionTask + "," + duration.toMinutes() + ","
                + localDateTime.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask && Objects.equals(nameTask, task.nameTask)
                && Objects.equals(descriptionTask, task.descriptionTask) && statusTask == task.statusTask
                && Objects.equals(duration, task.duration) && Objects.equals(localDateTime, task.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTask, descriptionTask, idTask, statusTask, duration, localDateTime);
    }
}
