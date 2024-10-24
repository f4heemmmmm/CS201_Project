package edu.smu.smusql;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Table2 {
    private String name;
    private List<String> columns;

    // Replace BinarySearchTree with Deque
    Deque<Map<String, String>> dataList = new ArrayDeque<>();

    public String getName() {
        return name;
    }

    public Deque<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(Deque<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    public List<String> getColumns() {
        return columns;
    }

    // Add a new row at the end of the deque
    public void addRow(Map<String, String> newRow) {
        dataList.addLast(newRow);
    }

    // Search for a row by id
    public Map<String, String> search(int id) {
        for (Map<String, String> row : dataList) {
            if (Integer.parseInt(row.get("id")) == id) {
                return row;
            }
        }
        return null;
    }

    // Delete a row by id
    public void delete(int id) {
        dataList.removeIf(row -> Integer.parseInt(row.get("id")) == id);
    }

    public Table(String name, List<String> columns) {
        this.name = name;
        this.columns = columns;
    }
}



