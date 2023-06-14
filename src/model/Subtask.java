package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;
    public Subtask(String nameTask, String descriptionTask, int idTask, StatusTask statusTask, Duration duration,
                   LocalDateTime localDateTime, int epicId) {
        super(nameTask, descriptionTask, idTask, statusTask, duration, localDateTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "\nПодзадача:" + super.toString();
    }

    @Override
    public String toStringFile() { // решил чуть изменить название метода, что бы выводилось по разному в консоль
        return super.toStringFile() + "," + epicId; //понятно, а в файл по нужному лекалу
    }
}
