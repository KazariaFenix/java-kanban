package manager.historymanagers;

public class Node<E> {
    private E task;
    private Node<E> next;
    private Node<E> prev;

    public Node(Node<E> prev, E task, Node<E> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    public E getTask() {
        return task;
    }

    public void setTask(E task) {
        this.task = task;
    }

    public Node<E> getNext() {
        return next;
    }

    public Node<E> getPrev() {
        return prev;
    }

    public void setPrev(Node<E> prev) {
        this.prev = prev;
    }
}
