import manager.InMemoryTaskManager;
import manager.Managers;
import manager.StatusTask;
import tasks.EpicTask;
import tasks.SimpleTask;
import tasks.Subtask;

public class Main {

    public static void main(String[] args) {
        SimpleTask simpleTask1 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask2 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask3 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask4 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask5 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask6 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask7 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask8 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask9 = new SimpleTask("Learn", "Learning", 0, StatusTask.NEW);
        SimpleTask simpleTask10 = new SimpleTask("Learn", "Learning",06, StatusTask.NEW);
        SimpleTask simpleTask11 = new SimpleTask(")#(#($(#$)", "Learn", 0, StatusTask.NEW);
        SimpleTask simpleTask12 = new SimpleTask("Learn", "Learn", 0, StatusTask.NEW);
        SimpleTask simpleTask13 = new SimpleTask("!!!!!!!!", "Learn", 0, StatusTask.NEW);
        InMemoryTaskManager in = new InMemoryTaskManager();
        in.addSimpleTask(simpleTask1);
        in.addSimpleTask(simpleTask2);
        in.addSimpleTask(simpleTask3);
        in.addSimpleTask(simpleTask4);
        in.addSimpleTask(simpleTask5);
        in.addSimpleTask(simpleTask6);
        in.addSimpleTask(simpleTask7);
        in.addSimpleTask(simpleTask8);
        in.addSimpleTask(simpleTask9);
        in.addSimpleTask(simpleTask10);
        in.addSimpleTask(simpleTask11);
        in.addSimpleTask(simpleTask12);
        in.addSimpleTask(simpleTask13);
        in.getIdSimple(1);
        in.getIdSimple(2);
        in.getIdSimple(3);
        in.getIdSimple(4);
        in.getIdSimple(5);
        in.getIdSimple(6);
        in.getIdSimple(7);
        in.getIdSimple(8);
        in.getIdSimple(9);
        in.getIdSimple(10);
        in.getIdSimple(11);
        in.getIdSimple(12);
        in.getIdSimple(13);
        System.out.println(Managers.getDefaultHistory().getHistory());
    }
}
