package edu.smu.smusql;

import java.util.List;
import java.util.Map;

public class Table {
    private String name;
    private List<String> columns;
    private Deque<Map<String, String>> dataList = new Deque<>();

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
        for (Map<String, String> row : dataList) {
            if (Integer.parseInt(row.get("id")) == id) {
                dataList.delete(row); // Use your custom delete method
                break; // Exit the loop once the row is found and deleted
            }
        }
    }

    public Table(String name, List<String> columns) {
        this.name = name;
        this.columns = columns;
    }
}
