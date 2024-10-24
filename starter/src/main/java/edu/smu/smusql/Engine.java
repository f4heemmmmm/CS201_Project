package edu.smu.smusql;

import java.util.*;

public class Engine {

    // A map to store tables by their name
    private Map<String, Table> tables = new HashMap<>();

    public String executeSQL(String query) {
        String[] tokens = query.trim().split("\\s+");
        String command = tokens[0].toUpperCase();

        switch (command) {
            case "CREATE":
                return create(tokens);
            case "INSERT":
                return insert(tokens);
            case "SELECT":
                return select(tokens);
            case "UPDATE":
                return update(tokens);
            case "DELETE":
                return delete(tokens);
            default:
                return "ERROR: Unknown command";
        }
    }

    // CREATE TABLE table_name (column1, column2, column3, ...)
    public String create(String[] tokens) {
        if (!tokens[1].equalsIgnoreCase("TABLE")) {
            return "ERROR: Invalid CREATE TABLE syntax";
        }

        String tableName = tokens[2];

        if (tables.containsKey(tableName)) {
            return "ERROR: Table already exists";
        }

        // Extract column names from the query
        String columnList = queryBetweenParentheses(tokens, 3);
        List<String> columns = Arrays.asList(columnList.split(","));
        columns.replaceAll(String::trim);

        // Create a new table
        Table newTable = new Table(tableName, columns);
        tables.put(tableName, newTable);

        return "Table " + tableName + " created successfully";
    }

    // INSERT INTO table_name (column1, column2, column3, ...) VALUES (value1, value2, value3, ...)
    public String insert(String[] tokens) {
        if (!tokens[1].equalsIgnoreCase("INTO")) {
            return "ERROR: Invalid INSERT INTO syntax";
        }

        String tableName = tokens[2];
        Table table = tables.get(tableName);

        if (table == null) {
            return "ERROR: Table not found";
        }

        // Extract values from the query
        String valueList = queryBetweenParentheses(tokens, 4);
        List<String> values = Arrays.asList(valueList.split(","));
        values.replaceAll(String::trim);

        List<String> columns = table.getColumns();
        if (columns.size() != values.size()) {
            return "ERROR: Column count does not match value count";
        }

        // Create a new row as a map
        Map<String, String> newRow = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            newRow.put(columns.get(i), values.get(i));
        }

        // Insert the new row into the table
        table.addRow(newRow);

        return "Row inserted successfully into " + tableName;
    }

    // SELECT * FROM table_name WHERE condition
    public String select(String[] tokens) {
        if (!tokens[1].equals("*") || !tokens[2].equalsIgnoreCase("FROM")) {
            return "ERROR: Invalid SELECT syntax";
        }

        String tableName = tokens[3];
        Table table = tables.get(tableName);

        if (table == null) {
            return "ERROR: Table not found";
        }

        String whereClause = null;
        for (int i = 4; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("WHERE")) {
                whereClause = String.join(" ", Arrays.copyOfRange(tokens, i + 1, tokens.length));
                break;
            }
        }

        // Retrieve rows from the table
        Deque<Map<String, String>> rows = table.getDataList();
        List<Map<String, String>> resultRows = new ArrayList<>(rows);

        if (whereClause != null) {
            resultRows = filterRows(resultRows, whereClause);
        }

        StringBuilder result = new StringBuilder();
        result.append("Table: ").append(tableName).append("\n");
        result.append(String.join(", ", table.getColumns())).append("\n");

        for (Map<String, String> row : resultRows) {
            for (String column : table.getColumns()) {
                result.append(row.get(column)).append(", ");
            }
            result.setLength(result.length() - 2); // Remove trailing comma and space
            result.append("\n");
        }

        return result.toString();
    }

    // UPDATE table_name SET column1=value1, column2=value2, ... WHERE condition
    public String update(String[] tokens) {
        if (!tokens[2].equalsIgnoreCase("SET")) {
            return "ERROR: Invalid UPDATE syntax";
        }

        String tableName = tokens[1];
        Table table = tables.get(tableName);

        if (table == null) {
            return "ERROR: Table not found";
        }

        int whereIndex = -1;
        for (int i = 3; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("WHERE")) {
                whereIndex = i;
                break;
            }
        }

        if (whereIndex == -1) {
            return "ERROR: Missing WHERE clause";
        }

        // Extract column names and values to update
        String setClause = String.join(" ", Arrays.copyOfRange(tokens, 3, whereIndex));
        String[] setPairs = setClause.split(",");
        Map<String, String> updates = new HashMap<>();
        for (String pair : setPairs) {
            String[] keyValue = pair.split("=");
            updates.put(keyValue[0].trim(), keyValue[1].trim());
        }

        // Extract the WHERE clause
        String whereClause = String.join(" ", Arrays.copyOfRange(tokens, whereIndex + 1, tokens.length));

        // Retrieve rows from the table and update
        List<Map<String, String>> rows = new ArrayList<>(table.getDataList());
        List<Map<String, String>> rowsToUpdate = filterRows(rows, whereClause);

        for (Map<String, String> row : rowsToUpdate) {
            for (Map.Entry<String, String> entry : updates.entrySet()) {
                row.put(entry.getKey(), entry.getValue());
            }
        }

        return "Rows updated successfully in " + tableName;
    }

    // DELETE FROM table_name WHERE condition
    public String delete(String[] tokens) {
        if (!tokens[1].equalsIgnoreCase("FROM")) {
            return "ERROR: Invalid DELETE FROM syntax";
        }

        String tableName = tokens[2];
        Table table = tables.get(tableName);

        if (table == null) {
            return "ERROR: Table not found";
        }

        // Extract the WHERE clause
        String whereClause = null;
        for (int i = 3; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("WHERE")) {
                whereClause = String.join(" ", Arrays.copyOfRange(tokens, i + 1, tokens.length));
                break;
            }
        }

        if (whereClause == null) {
            return "ERROR: Missing WHERE clause";
        }

        // Retrieve rows from the table and delete
        List<Map<String, String>> rows = new ArrayList<>(table.getDataList());
        List<Map<String, String>> rowsToDelete = filterRows(rows, whereClause);

        for (Map<String, String> row : rowsToDelete) {
            int id = Integer.parseInt(row.get("id"));
            table.delete(id);
        }

        return "Rows deleted successfully from " + tableName;
    }

    // Helper method to extract text between parentheses
    private String queryBetweenParentheses(String[] tokens, int startIndex) {
        StringBuilder sb = new StringBuilder();
        boolean openParenthesisFound = false;
        for (int i = startIndex; i < tokens.length; i++) {
            if (tokens[i].contains("(")) {
                openParenthesisFound = true;
                sb.append(tokens[i].substring(tokens[i].indexOf("(") + 1));
            } else if (tokens[i].contains(")")) {
                sb.append(" ").append(tokens[i], 0, tokens[i].indexOf(")"));
                break;
            } else if (openParenthesisFound) {
                sb.append(" ").append(tokens[i]);
            }
        }
        return sb.toString().trim();
    }

    // Helper method to filter rows based on a WHERE clause
    private List<Map<String, String>> filterRows(List<Map<String, String>> rows, String whereClause) {
        // For simplicity, we assume only a single condition for now
        String[] condition = whereClause.split(" ");
        String column = condition[0];
        String operator = condition[1];
        String value = condition[2];

        return rows.stream().filter(row -> {
            String rowValue = row.get(column);
            if (rowValue == null) return false;

            switch (operator) {
                case "=":
                    return rowValue.equals(value);
                case ">":
                    return Double.parseDouble(rowValue) > Double.parseDouble(value);
                case "<":
                    return Double.parseDouble(rowValue) < Double.parseDouble(value);
                default:
                    return false;
            }
        }).collect(Collectors.toList());
    }
}
