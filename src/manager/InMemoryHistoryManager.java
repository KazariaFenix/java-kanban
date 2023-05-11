package manager;

import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> history = new CustomLinkedList<>();
    private final Map<Integer, Node> tasks = new HashMap<>();
    private final ArrayList<Task> taskList = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void add(Task task) {
        if (tasks.containsKey(task.getIdTask())) {
            history.removeNode(tasks.get(task.getIdTask()));
            tasks.remove(task.getIdTask());
        }
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (tasks.containsKey(id)) {
            history.removeNode(tasks.get(id));
            tasks.remove(id);
        }
    }

    class CustomLinkedList<T> {

        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;

        public void linkLast(Task task) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node(oldTail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.setNext(newNode);
            }
            size++;
            tasks.put(task.getIdTask(), newNode);
        }

        public List<Task> getTasks() {
            for (Integer taskId : tasks.keySet()) {
                Node<Task> node = tasks.get(taskId);
                taskList.add(node.getTask());
            }
            return taskList;
        }

        public int getSize() {
            return size;
        }

        public void removeNode(Node<Task> node) {
            final Node<Task> prevNode = node.getPrev();
            final Node<Task> nextNode = node.getNext();
            final Task task = node.getTask();

            if (prevNode == null) {
                head = nextNode;
            } else {
                prevNode.setNext(nextNode);
                node.setPrev(null);
            }

            if (nextNode == null) {
                tail = prevNode;
            } else {
                nextNode.setPrev(prevNode);
                node.setPrev(null);
            }

            node.setTask(null);
            size--;
        }
    }
}
