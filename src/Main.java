public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        SimpleTask simpleTask = new SimpleTask("Стать лучше", "По немногу добиваться своего",
                 0,"IN_PROGRESS");
        EpicTask epicTask = new EpicTask("Задача", "Работа", 0,"IN_PROGRESS");
        EpicTask epicTask1 = new EpicTask("Спорт", "Улучшить здоровье", 0,
                "IN_PROGRESS");
        EpicTask epicTask2 = new EpicTask("Спорт", "Улучшить здоровье", 0,
                "IN_PROGRESS");
        Subtask subtask1 = new Subtask("Пробежка", "5 км",0,"NEW",2);
        Subtask subtask3 = new Subtask("Повышать квалификацию", "Больше читать и работать",
                0,"IN_PROGRESS",1);
        Subtask subtask4 = new Subtask("Занятие с гирей", "Тренировка на верхние группы мышц",
                0,"DONE",2);
        taskManager.addEpicTask(epicTask);
        taskManager.addEpicTask(epicTask1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.addEpicTask(epicTask2);
        taskManager.addSimpleTask(simpleTask);
        System.out.println(taskManager.printListSimpleTask());
        System.out.println(taskManager.printListEpicTaskandSubtask());
        taskManager.deleteIdEpicTask(6);

    }
}
