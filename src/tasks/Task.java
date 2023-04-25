package tasks;

public class Task {
    private String nameTask;
    private String descriptionTask;
    private int idTask;
    private String statusTask;

    public Task(String nameTask, String descriptionTask, int idTask, String statusTask) {
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

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }

    @Override
    public String toString() {
        return "\nНазвание задачи: " + nameTask + "\nОписание задачи: " + descriptionTask + "\nId задачи: " + idTask +
                "\nСтатус задачи: " + statusTask + "\n";
    }
}