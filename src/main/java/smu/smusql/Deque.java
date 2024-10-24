public class Deque<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T data) {
            this.data = data;
        }
    }

    public Deque() {
        front = rear = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFront(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            newNode.next = front;
            front.prev = newNode;
            front = newNode;
        }
        size++;
    }

    public void addRear(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            newNode.prev = rear;
            rear = newNode;
        }
        size++;
    }

    public T removeFront() {
        if (isEmpty()) {
            throw new RuntimeException("Deque is empty");
        }
        T data = front.data;
        front = front.next;
        if (front != null) {
            front.prev = null;
        } else {
            rear = null; // If deque becomes empty
        }
        size--;
        return data;
    }

    public T removeRear() {
        if (isEmpty()) {
            throw new RuntimeException("Deque is empty");
        }
        T data = rear.data;
        rear = rear.prev;
        if (rear != null) {
            rear.next = null;
        } else {
            front = null; // If deque becomes empty
        }
        size--;
        return data;
    }

    public T peekFront() {
        if (isEmpty()) {
            throw new RuntimeException("Deque is empty");
        }
        return front.data;
    }

    public T peekRear() {
        if (isEmpty()) {
            throw new RuntimeException("Deque is empty");
        }
        return rear.data;
    }
}
