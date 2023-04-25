package tasks;

public class Subtask extends Task {
    private int epicId; // По условию нам известно к какому Tasks.EpicTask принадлежит Tasks.Subtask, то я решил связать их через epicId
                // переменную, которая указывается при создании объекта Tasks.Subtask и является idTask объекта Tasks.EpicTask
    public Subtask(String nameTask, String descriptionTask, int idTask, String statusTask, int epicId) {
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
