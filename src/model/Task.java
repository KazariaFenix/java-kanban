package model;

public class Task {
    private String nameTask;
    private String descriptionTask;
    private int idTask;
    private StatusTask statusTask;

    public Task(String nameTask, String descriptionTask, int idTask, StatusTask statusTask) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.idTask = idTask;
        this.statusTask = statusTask;
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

    @Override
    public String toString() {
        return "\nНазвание задачи: " + nameTask + "\nОписание задачи: " + descriptionTask + "\nId задачи: " + idTask +
                "\nСтатус задачи: " + statusTask.name() + "\n";
    }

    public String toStringFile() {
        return idTask + "," + TypesTasks.valueOf(this.getClass().getSimpleName().toUpperCase()).name()
                + "," + nameTask + "," + statusTask + "," + descriptionTask;
    }
}
