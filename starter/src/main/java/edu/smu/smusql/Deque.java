package edu.smu.smusql;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<E> implements Iterable<E> {

    private static class Node<E> {
        E element;
        Node<E> next;
        Node<E> prev;

        // Node Constructor
        public Node(E element) {
            this.element = element;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    // Constructor
    public Deque() {
        head = null;
        tail = null;
        size = 0;
    }

    // Add an element to the front
    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    // Add an element to the back
    public void addLast(E element) {
        Node<E> newNode = new Node<>(element);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // Remove and return the element from the front
    public E removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        E element = head.element;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }
        size--;
        return element;
    }

    // Remove and return the element from the back
    public E removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        E element = tail.element;
        tail = tail.prev;
        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }
        size--;
        return element;
    }

    // Insert an element at a specific index
    public void insert(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (index == 0) {
            addFirst(element);
        } else if (index == size) {
            addLast(element);
        } else {
            Node<E> newNode = new Node<>(element);
            Node<E> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            newNode.prev = current;
            current.next.prev = newNode;
            current.next = newNode;
            size++;
        }
    }

    // Search for an element and return its index (or -1 if not found)
    public int search(E element) {
        Node<E> current = head;
        int index = 0;
        while (current != null) {
            if (current.element.equals(element)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;  // Element not found
    }

    // Delete an element by value
    public boolean delete(E element) {
        Node<E> current = head;

        // Traverse the deque to find the element
        while (current != null) {
            if (current.element.equals(element)) {
                if (current == head) {
                    removeFirst();
                } else if (current == tail) {
                    removeLast();
                } else {
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                    size--;
                }
                return true;  // Element found and deleted
            }
            current = current.next;
        }
        return false;  // Element not found
    }

    // Peek at the front element
    public E peekFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return head.element;
    }

    // Peek at the back element
    public E peekLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return tail.element;
    }

    // Check if the deque is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Return the number of elements in the deque
    public int size() {
        return size;
    }

    // Implement the Iterable interface
    @Override
    public Iterator<E> iterator() {
        return new DequeIterator();
    }

    // Iterator implementation for the Deque
    private class DequeIterator implements Iterator<E> {
        private Node<E> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the deque");
            }
            E element = current.element;
            current = current.next;
            return element;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported");
        }
    }
}
