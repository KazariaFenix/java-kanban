package model;

public class Subtask extends Task {
    private int epicId;
    public Subtask(String nameTask, String descriptionTask, int idTask, StatusTask statusTask, int epicId) {
        super(nameTask, descriptionTask, idTask, statusTask);
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
}
