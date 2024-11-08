package edu.smu.smusql;

import java.util.List;
import java.util.Map;

public class Table {
    private String name;
    private List<String> columns;

    Deque<Map<String, String>> dataList = new Deque<>();

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
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

    public void addRow(Map<String, String> new_row){
        int id = Integer.parseInt(new_row.get("id"));
        dataList.addLast(id, new_row);
    }

    public Table(String name, List<String> columns) {
        this.dataList = new Deque<>();
        this.name = name;
        this.columns = columns;
    }
    

}
