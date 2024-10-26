package edu.smu.smusql;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<E> implements Iterable<E> {

    // ------------------------------------------------------- NODE CLASS -------------------------------------------------------
    private static class Node<E> {
        E element;
        Node<E> next;
        Node<E> previous;

        // Node: Constructor
        public Node(E element) {
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

        // DequeIterator: remove()
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported. \n");
        }
    }

    // ------------------------------------------------------- DEQUE CLASS METHODS -------------------------------------------------------
    private Node<E> head;
    private Node<E> tail;
    private int size;   // Chose to have an attribute that keeps track of the size, instead of a method to calculate the size due to the time complexity always being O(1)


    // Deque: Constructor
    public Deque() {
        head = null;
        tail = null;
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

    // Deque: peekFirst() -> Retrieves the first element in the deque
    public E peekFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("The deque is empty. \n");
        } else {
            return head.element;
        }
    }
    
    // Deque: peekLast() -> Retrieves the last element in the deque
    public E peekLast() {
        if (isEmpty()) {
            throw new IllegalStateException("The deque is empty. \n");
        }
        else {
            return tail.element;
        }
    }

    // Deque: addFirst -> Insert an element from the front
    public void addFirst(E element) {
        Node<E> newNode = new Node<E>(element);
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
    public void addLast(E element) {
        Node<E> newNode = new Node<E>(element);
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
    public void addAtIndex(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index is not within the correct range of the deque. \n");
        }
        if (index == 0) {
            addFirst(element);
        } else if (index == size) {
            addLast(element);
        } else {
            Node<E> newNode = new Node<E>(element);
            Node<E> current;
            if (index <= size / 2) {
                current = head;
                for (int i = 0; i < index; i++) {
                    current = current.next;
                }
            } else {
                current = tail;
                for (int i = size - 1; i > index; i--) {
                    current = current.previous;
                }
            }
            newNode.next = current;
            newNode.previous = current.previous;
            current.previous.next = newNode;
            current.previous = newNode;
            size++;
        }
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
        return -1;
    }

    // Deque: delete() -> Deletes an element
    public boolean delete(E element) {
        Node<E> current = head;
        while (current != null) {
            if (current.element.equals(element)) {
                if (current == head) {
                    removeFirst();
                } else if (current == tail) {
                    removeLast();
                } else {
                    current.previous.next = current.next;
                    current.next.previous = current.previous;
                    size--;
                }
                return true;
            }
            current = current.next;
        }
        return false;
    }
}

// Changes for faster and optimized performance
//  -> for insertion, we changed the code such that for insertion at index, instead from always traversing from the head, we traverse from both head and tail
//     depending if the index to insert is closer to start or the end of the deque
