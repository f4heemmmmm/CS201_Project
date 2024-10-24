package edu.smu.smusql;

import java.util.List;
import java.util.Map;
import edu.smu.smusql.Deque;

public class Table {
    // Maybe try different data structures for the columns/rows
    private String name;
    private List<String> columns;
    private Deque<Map<String, Object>> rows;

    // Constructor
    public Table(String name, List<String> columns) {
        this.name = name;
        this.columns = columns;
        this.rows = new Deque<Map<String, Object>>();
    }

    // Method 1: Inserting a row
    public void insertRow(Map<String, Object> rowData) {
        if (rowData.keySet().containsAll(columns)) {
            rows.addRear(rowData);
        } else {
            throw new IllegalArgumentException("Row Data must contain all columns");
        }
    }

    // Method 2: Retrieve all rows
    public Deque<Map<String, Object>> getRows() {
        return rows;
    }

    // Method 3: Retrieve a row by its index (front or read)
    public Map<String, Object> getRow(boolean fromFront) {
        if (fromFront) {
            return rows.peekFront();
        } else {
            return rows.peekRear();
        }
    }

    // Method 4: Remove a row
    public void removeRow(boolean fromFront) {
        if (fromFront) {
            rows.removeFront();
        } else {
            rows.removeRear();
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getColumns() {
        return columns;
    }
}