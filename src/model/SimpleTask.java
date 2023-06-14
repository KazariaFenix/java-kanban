package model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class SimpleTask extends Task {

    public SimpleTask(String nameTask, String descriptionTask, int idTask, StatusTask statusTask, Duration duration,
                      LocalDateTime localDateTime) {
        super(nameTask, descriptionTask, idTask, statusTask, duration, localDateTime);
    }
}
