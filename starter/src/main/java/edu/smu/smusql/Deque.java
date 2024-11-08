package edu.smu.smusql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Deque<E> implements Iterable<E> {

    // ------------------------------------------------------- NODE CLASS ---------------------------------------------------------
    private static class Node<E> {
        private int id;
        E element;
        Node<E> next;
        Node<E> previous;

        // Node: Constructor
        public Node(int id, E element) {
            this.id = id;
            this.element = element;
            this.next = null;
            this.previous = null;
        }
    }

    // ------------------------------------------------------- ITERATOR CLASS -------------------------------------------------------
    public class DequeIterator implements Iterator<E> {
        private Node<E> current = head;

        // DequeIterator: next()
        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("There are no more elements in the deque! \n");
            }
            E element = current.element;
            current = current.next;
            return element;
        }

        // DequeIterator: hasNext()
        @Override
        public boolean hasNext() {
            return current != null;
        }
    }

    // ------------------------------------------------------- DEQUE CLASS METHODS -------------------------------------------------------
    private Node<E> head;
    private Node<E> tail;
    private int size = 0;   // Chose to have an attribute that keeps track of the size, instead of a method to calculate the size due to the time complexity always being O(1)
    private String name;

    // Deque: Constructor
    public Deque() {
        head = null;
        tail = null;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Deque: isEmpty() -> Checks if the deque is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Deque: getSize() -> Retrieves the size of the deque
    public int getSize() {
        return size;
    }

    // Deque: iterator() -> Instantiates new DequeIterator
    @Override
    public Iterator<E> iterator() {
        return new DequeIterator();
    }

    // Deque: addFirst -> Insert an element from the front
    public void addFirst(int id, E element) {
        Node<E> newNode = new Node<E>(id, element);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.previous = newNode;
            head = newNode;
        }
        size++;
    }

    // Deque: addLast() -> Insert a nelement from the back
    public void addLast(int id, E element) {
        Node<E> newNode = new Node<E>(id, element);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.previous = tail;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }


    // Deque: addAtIndex() -> Insert an element at a specific index
    public void insert(int id, E element) {
        if (head == null) {
            addFirst(id, element);
        } else 
            addLast(id, element);
    }

    // Deque: removeFirst() -> Removes and returns the element at the front of the deque
    public E removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("The deque is empty. \n");
        } else {
            E element = head.element;
            head = head.next;
            if (head == null) {
                tail = null;
            } else {
                head.previous = null;
            }
            size--;
            return element;
        }
    }

    // Deque: removeLast() -> Removes and returns the element at the back of the deque
    public E removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("The deque is empty. \n");
        } else {
            E element = tail.element;
            tail = tail.previous;
            if (tail == null) {
                head = null;
            } else {
                tail.next = null;
            }
            size--;
            return element;
        }
    }

    // Deque: search() -> Search for an element and returns its index
    public E search(int key) {
        Node<E> current = head;
        while (current != null) {
            if (current.id == key) {
                return current.element;
            }
            current = current.next;
        }
        return null;
    }

    // Deque: delete() -> Deletes an element
    public void delete(int key) {
        Node<E> current = head;
        while (current != null) {
            if (current.id == (key)) {
                if (current == head) {
                    removeFirst();
                } else if (current == tail) {
                    removeLast();
                } else {
                    current.previous.next = current.next;
                    current.next.previous = current.previous;
                    size--;
                }
                return;
            }
            current = current.next;
        }
        return;
    }

        public List<E> inorderTraversal() {
        List<E> result = new ArrayList<>();
        Iterator<E> iterator = this.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
}

// Changes for faster and optimized performance
//  -> for insertion, we changed the code such that for insertion at index, instead from always traversing from the head, we traverse from both head and tail
//     depending if the index to insert is closer to start or the end of the deque
