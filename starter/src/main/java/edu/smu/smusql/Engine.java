package edu.smu.smusql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Engine {
    public Deque<Table> dataList = null;
    public Deque<Deque<Table>> databaseList = new Deque<>();

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
            case "SHOW":
                return show(tokens);
            case "USE":
                return useDatabase(tokens);
            case "DROP":
                return drop(tokens);
            default:
                return "ERROR: Unknown command";
        }
    }

    public String drop(String[] tokens) {
        if (dataList == null) {
            return "ERROR: No database selected";
        }

        
        // if (!tokens[1].equalsIgnoreCase("DATABASE")) {
        //     return "ERROR: Invalid DELETE DATABASE/TABLE syntax 48";
        // } else if (tokens.length < 3) {
        //     return "ERROR: Invalid DELETE DATABASE/TABLE syntax 52";
        // }

        String tableName = tokens[2];
        if (tokens[1].equalsIgnoreCase("DATABASE")) {
            Deque<Table> database = databaseList.search(tableName.hashCode());
            if (database == null) {
                return "ERROR: Table not found";
            }
    
            // Drop database with tableName
            databaseList.delete(tableName.hashCode());
            if (dataList.getName().equals(tableName)) {
                dataList = null;
            }
        } else if (tokens[1].equalsIgnoreCase("TABLE")) {
            Table table = dataList.search(tableName.hashCode());
            if (table == null) {
                return "ERROR: Table not found";
            }
    
            // Drop table with tableName
            dataList.delete(tableName.hashCode());
        } else {
            return "ERROR: Invalid DELETE DATABASE/TABLE syntax gg";
        }


        
        

        return tableName + " deleted successfully";
    }

    public String insert(String[] tokens) {

        //"INSERT INTO users VALUES (" + userId + ", '" + name + "', " + age + ", '" + city + "')";//

        if (!tokens[1].equalsIgnoreCase("INTO")) {
            return "ERROR: Invalid INSERT INTO syntax";
        }
        if (dataList == null) {
            return "ERROR: No database selected";
        }

        String tableName = tokens[2];

        // Search for the table in the RBT
        Table table = dataList.search(tableName.hashCode());
        if (table == null) {
            return "ERROR: Table not found";
        }

        // Extract column names and values
        // INSERT INTO table_name (column1, column2, column3, ...) VALUES (value1,
        // value2, value3, ...);
        // String columnList = queryBetweenParentheses(tokens, 4);
        // List<String> columns = Arrays.asList(columnList.split(","));
        // columns.replaceAll(String::trim);

        String valueList = queryBetweenParentheses(tokens, 3);
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
        int id = Integer.parseInt(newRow.get("id"));
        if (table.getDataList().search(id) != null) {
            return "ERROR: Duplicate id";
        }
        // Insert the new row into the table
        table.addRow(newRow);

        return "Row inserted successfully into " + tableName;
    }

    private String useDatabase(String[] tokens) {
        if (tokens.length < 2) {
            return "ERROR: Invalid USE syntax";
        }

        String databaseName = tokens[1];
        Deque<Table> database = databaseList.search(databaseName.hashCode());

        if (database == null) {
            return "ERROR: Database not found";
        }

        this.dataList = database;
        return "Using database " + databaseName;
    }

    public String delete(String[] tokens) {
        if (dataList == null) {
            return "ERROR: No database selected";
        }

        
        if (!tokens[1].equalsIgnoreCase("FROM")) {
            return "ERROR: Invalid DELETE FROM syntax";
        }

        String tableName = tokens[2];

        // Search for the table in the RBT
        Table table = dataList.search(tableName.hashCode());
        if (table == null) {
            return "ERROR: Table not found";
        }

        // Check for WHERE clause
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

        // Retrieve and delete the rows that match the conditions
        List<Map<String, String>> rows = table.getDataList().inorderTraversal();
        List<Map<String, String>> rowsToDelete = filterRows(rows, whereClause);

        for (Map<String, String> row : rowsToDelete) {
            int id = Integer.parseInt(row.get("id"));
            table.getDataList().delete(id);
        }

        return "Rows deleted successfully from " + tableName;
    }

    public String show(String[] tokens) {
        if (!tokens[0].equalsIgnoreCase("SHOW")) {
            return "ERROR: Invalid SHOW syntax";
        }
        if (tokens.length < 2) {
            return "ERROR: Invalid SHOW syntax";
        }
        if (tokens[1].equalsIgnoreCase("DATABASES")) {

            List<Deque<Table>> databases = databaseList.inorderTraversal();
            StringBuilder result = new StringBuilder();
            result.append("Databases:\n");
            for (Deque<Table> database : databases) {
                result.append(database.getName()).append("\n");
            }
            return result.toString();
        } else if (tokens[1].equalsIgnoreCase("TABLES")) {
            if (dataList == null) {
                return "ERROR: No database selected";
            }
            List<Table> tables = dataList.inorderTraversal();
            StringBuilder result = new StringBuilder();
            result.append("Tables:\n");
            for (Table table : tables) {
                result.append(table.getName()).append("\n");
            }
            return result.toString();

        }
        return "ERROR: Invalid SHOW syntax";
    }

    public String select(String[] tokens) {

        if (dataList == null) {
            return "ERROR: No database selected";
        }

    
        // jump to new method to handle math operations
        if (tokens[1].toUpperCase().contains("SUM") || tokens[1].toUpperCase().contains("AVG")
                || tokens[1].toUpperCase().contains("MAX") || tokens[1].toUpperCase().contains("MIN")
                || tokens[1].toUpperCase().contains("COUNT")) {
            return selectMath(tokens);
        }

        if (!tokens[1].equals("*") || !tokens[2].toUpperCase().equals("FROM")) {
            return "ERROR: Invalid SELECT syntax";
        }

        String tableName = tokens[3];

        Table table = dataList.search(tableName.hashCode());

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

        // Retrieve and format the data from the table
        StringBuilder result = new StringBuilder();
        result.append("Table: ").append(tableName).append("\n");
        result.append(String.join(", ", table.getColumns())).append("\n");

        List<Map<String, String>> rows = table.getDataList().inorderTraversal();

        if (rows.isEmpty() || rows.get(0) == null) {
            return result.toString(); // Return the table header only
        }

        if (whereClause != null) {

            rows = filterRows(rows, whereClause);
        }
        // Assuming the table has a method to get all rows as a list of maps
        for (Map<String, String> row : rows) {
            for (String column : table.getColumns()) {
                result.append(row.get(column)).append(", ");
            }
            result.setLength(result.length() - 2); // Remove trailing comma and space
            result.append("\n");
        }

        return result.toString();
    }

    public String update(String[] tokens) {

        if (dataList == null) {
            return "ERROR: No database selected";
        }

        
        if (!tokens[2].equalsIgnoreCase("SET")) {
            return "ERROR: Invalid UPDATE syntax";
        }

        String tableName = tokens[1];

        // Search for the table in the RBT
        Table table = dataList.search(tableName.hashCode());
        if (table == null) {
            return "ERROR: Table not found";
        }
        int whereIndex = -1;
        for (int i = 2; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("WHERE")) {
                whereIndex = i;
                break;
            }
        }
        if (whereIndex == -1) {
            return "ERROR: Missing WHERE clause";
        }

        // Extract column names and values to update
        String setClause = String.join(" ", Arrays.copyOfRange(tokens, 2, whereIndex));
        String[] setPairs = setClause.split(",");
        Map<String, String> updates = new HashMap<>();
        for (String pair : setPairs) {
            String[] keyValue = pair.split("=");
            updates.put(keyValue[0].trim(), keyValue[1].trim());
        }

        // Check for WHERE clause
        String whereClause = String.join(" ", Arrays.copyOfRange(tokens, whereIndex + 1, tokens.length));
        // Retrieve and update the rows that match the conditions
        List<Map<String, String>> rows = table.getDataList().inorderTraversal();
        List<Map<String, String>> rowsToUpdate = filterRows(rows, whereClause);

        if(rowsToUpdate.isEmpty()) {
            return "ERROR: No rows found to update";
        }

        for (Map<String, String> row : rowsToUpdate) {
            for (Map.Entry<String, String> entry : updates.entrySet()) {
                row.put(entry.getKey(), entry.getValue());
            }
            int id = Integer.parseInt(row.get("id"));
            table.getDataList().delete(id);
            table.addRow(row);
        }

        return "Rows updated successfully in " + tableName;
    }

    private String createDatabase(String[] tokens) {
        String databaseName = tokens[2];

        // Assuming you have a data structure to store databases
        if (databaseList.search(databaseList.hashCode()) != null) {
            return "ERROR: Database already exists";
        }

        Deque <Table> dataList = new Deque<>();
        databaseList.insert( databaseName.hashCode(), dataList);
        databaseList.search(databaseName.hashCode()).setName(databaseName);

        return "Database " + databaseName + " created successfully";
    }

    public String create(String[] tokens) {
        String tableName = tokens[2];

        if (tokens[1].equalsIgnoreCase("TABLE")) {
            if (databaseList.inorderTraversal().isEmpty() || dataList == null) {
                return "ERROR: No database selected";
            }
            if (dataList.search(tableName.hashCode()) != null) {
                return "ERROR: Table already exists";
            }
            String columnList = queryBetweenParentheses(tokens, 3);
            List<String> columns = Arrays.asList(columnList.split(","));
            columns.replaceAll(String::trim);
            Table newTable = new Table(tableName, columns);
            dataList.insert(tableName.hashCode(),newTable);

            return "Table " + tableName + " created successfully";
        } else if (tokens[1].equalsIgnoreCase("DATABASE")) {
            if (databaseList.search(tableName.hashCode()) != null) {
                return "ERROR: Database already exists";
            }
            return createDatabase(tokens);

        } else {
            return "ERROR: Invalid CREATE DATABASE syntax";
        }
    }

    private List<Map<String, String>> filterRows(List<Map<String, String>> rows, String whereClause) {

        Predicate<Map<String, String>> predicate = createPredicate(whereClause);
        return rows.stream().filter(predicate).collect(Collectors.toList());
    }

    private Predicate<Map<String, String>> createPredicate(String whereClause) {
        String[] conditions = whereClause.split(" AND ");
        return row -> {
            for (String condition : conditions) {
                String[] parts = condition.split(" ");
                String attribute = parts[0];
                String operator = parts[1];
                String value = parts[2];

                String rowValue = row.get(attribute);
                if (rowValue == null) {
                    return false;
                }
                double rowValueDouble = 0;
                double valueDouble = 0;


                switch (operator) {
                    case "=":
                        if (!rowValue.equals(value)) {
                            return false;
                        }
                        break;
                    case ">":
                    case "<":
                        try {
                            rowValueDouble = Double.parseDouble(rowValue);
                            valueDouble = Double.parseDouble(value);
                            // isNumericComparison = true;
                        } catch (NumberFormatException e) {
                            return false; // Handle invalid number format
                        }
                        if (operator.equals(">")) {
                            if (!(rowValueDouble > valueDouble)) {
                                return false;
                            }
                        } else if (operator.equals("<")) {
                            if (!(rowValueDouble < valueDouble)) {
                                return false;
                            }
                        }
                        break;
                    // Add more operators as needed
                    default:
                        return false;
                }
            }
            return true;
        };
    }

    private String queryBetweenParentheses(String[] tokens, int startIndex) {
        StringBuilder sb = new StringBuilder();
        boolean openParenthesisFound = false;
        for (int i = startIndex; i < tokens.length; i++) {
            if (tokens[i].contains("(") && tokens[i].contains(")")) {
                return tokens[i].substring(tokens[i].indexOf("(") + 1, tokens[i].indexOf(")"));
            } else if (tokens[i].contains("(")) {
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

    private String selectMath(String[] tokens) {
        // Find the table
        String tableName = null;
        int tableNameIdx = -1;
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("FROM")) {
                // Possibility for null pointer exception
                try {
                    tableName = tokens[i + 1];
                } catch (Exception e) {
                    return "ERROR: Invalid Input, no table name after FROM";
                }
                tableNameIdx = i + 1;
                break;
            }
        }
        if (tableNameIdx == -1) {
            return "ERROR: Invalid Input, no FROM clause found";
        }

        Table table = dataList.search(tableName.hashCode());
        if (table == null) {
            return "ERROR: Table not found";
        }
        String column = queryBetweenParentheses(tokens, 1);
        String mathOperation = tokens[1].toUpperCase();
        List<Map<String, String>> rows = table.getDataList().inorderTraversal();
        double result = 0;
        try {
            switch (mathOperation) {
                case "SUM(":
                    result = rows.stream().mapToDouble(row -> Double.parseDouble(row.get(column))).sum();
                    break;
                case "AVG(":
                    result = rows.stream().mapToDouble(row -> Double.parseDouble(row.get(column))).average().orElse(0);
                    break;
                case "MAX(":
                    result = rows.stream().mapToDouble(row -> Double.parseDouble(row.get(column))).max().orElse(0);
                    break;
                case "MIN(":
                    result = rows.stream().mapToDouble(row -> Double.parseDouble(row.get(column))).min().orElse(0);
                    break;
                case "COUNT(":
                    // Check for WHERE clause
                    if (tokens.length > tableNameIdx) {
                        String whereClause = null;
                        for (int i = tableNameIdx; i < tokens.length; i++) {
                            if (tokens[i].equalsIgnoreCase("WHERE")) {
                                whereClause = String.join(" ", Arrays.copyOfRange(tokens, i + 1, tokens.length));
                                break;
                            }
                        }
                        if (whereClause != null) {
                            rows = filterRows(rows, whereClause);
                        }
                    }
                    result = rows.stream().mapToDouble(row -> Double.parseDouble(row.get(column))).count();
                    break;
                default:
                    return "ERROR: Invalid math operation";
            }
        } catch (NumberFormatException e) {
            return "ERROR: Invalid column value";
        }
        return mathOperation + column + ") = " + result;
    }
}
