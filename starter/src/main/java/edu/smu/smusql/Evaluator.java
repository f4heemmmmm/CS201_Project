package edu.smu.smusql;

import java.util.Random;

/*
 * This class is holds the additional evaluation methods, on top of the original
 */
public class Evaluator {
    // Database engine instance
    private static final Engine dbEngine = new Engine();

    // Constants for the fixed id values
    private static int userId = 50;
    private static int productId = 50;
    private static int orderId = 50;

    /*
     * Below is our alternative method for auto-evaluating the sql engine.
     * It runs commands such that we do not get duplicate ids when inserting data.
     * This aims to reduce error when inserting data, and to reflect a more
     * realistic total runtime
     */
    public static void autoEvaluate2() {

        // Set the number of queries to execute
        int numberOfQueries = 100000;

        // Container to track the number of errors
        double errors = 0.0;

        // create database
        dbEngine.executeSQL("CREATE DATABASE EvaluationDB");

        // Use the database
        dbEngine.executeSQL("USE EvaluationDB");

        // Create tables
        dbEngine.executeSQL("CREATE TABLE users (id, name, age, city)");
        dbEngine.executeSQL("CREATE TABLE products (id, name, price, category)");
        dbEngine.executeSQL("CREATE TABLE orders (id, user_id, product_id, quantity)");

        // Random data generator
        Random random = new Random();

        // Prepopulate the tables in preparation for evaluation
        prepopulateTables(random);
        // Capture the start time
        long startTime = System.nanoTime();
        // Loop to simulate millions of queries
        for (int i = 0; i < numberOfQueries; i++) {
            int queryType = random.nextInt(6); // Randomly choose the type of query to execute
            String queryResponse = null;
            switch (queryType) {
                case 0: // INSERT query
                    /*
                     * We have changed from the original method insertRandomData(random) to
                     * insertRandomData2(random) to avoid duplicate ids when inserting data
                     */
                    queryResponse = insertRandomData2(random);
                    break;
                case 1: // SELECT query (simple)
                    queryResponse = selectRandomDataAlt(random);
                    break;
                case 2: // UPDATE query
                    queryResponse = updateRandomDataAlt(random);
                    break;
                case 3: // DELETE query
                    queryResponse = deleteRandomDataAlt(random);
                    break;
                case 4: // Complex SELECT query with WHERE, AND, OR, >, <, LIKE
                    queryResponse = complexSelectQueryAlt(random);
                    break;
                case 5: // Complex UPDATE query with WHERE
                    queryResponse = complexUpdateQueryAlt(random);
                    break;
            }

            // Check if the query response contains an error message
            if (queryResponse.contains("ERROR")) {
                errors++;
            }

            // Print progress every 100,000 queries
            if (i % 10000 == 0) {
                System.out.println("Processed " + i + " queries...");
            }
        }
        // Capture the end time
        long endTime = System.nanoTime();
        // Calculate the elapsed time in milliseconds
        long elapsedTime = (endTime - startTime) / 1_000_000;
        // Print the elapsed time
        System.out.println("Processed " + numberOfQueries + " queries in " + elapsedTime + " milliseconds.");
        System.out.println("Finished processing " + numberOfQueries + " queries.");
        // Calculate and print the error percentage
        double errorPercentage = (errors / numberOfQueries) * 100;
        System.out.println("Error percentage: " + errorPercentage + "%");
    }

    // Helper method to insert random data into users, products, or orders table
    // This method is modified to avoid duplicate ids when inserting data
    private static String insertRandomData2(Random random) {
        int tableChoice = random.nextInt(3);
        String queryResponse = null;
        switch (tableChoice) {
            case 0: // Insert into users table
                String name = "User" + userId;
                int age = 20 + (userId % 41); // Ages between 20 and 60
                String city = getRandomCity(random);
                String insertUserQuery = "INSERT INTO users VALUES (" + userId + ", '" + name + "', " + age + ", '" + city
                        + "')";
                queryResponse = dbEngine.executeSQL(insertUserQuery);
                userId++;
                break;
            case 1: // Insert into products table
                String productName = "Product" + productId;
                double price = 10 + (productId % 990); // Prices between $10 and $1000
                String category = getRandomCategory(random);
                String insertProductQuery = "INSERT INTO products VALUES (" + productId + ", '" + productName + "', "
                        + price + ", '" + category + "')";
                queryResponse = dbEngine.executeSQL(insertProductQuery);
                productId++;
                break;
            case 2: // Insert into orders table
                int user_id = random.nextInt(9999);
                int product_id = random.nextInt(9999);
                int quantity = random.nextInt(1, 100);
                String insertOrderQuery = "INSERT INTO orders VALUES (" + orderId + ", " + user_id + ", " + product_id
                        + ", " + quantity + ")";
                queryResponse = dbEngine.executeSQL(insertOrderQuery);
                orderId++;
                break;
        }
        return queryResponse;
    }

    private static void prepopulateTables(Random random) {
        System.out.println("Prepopulating users");
        // Insert initial users
        for (int i = 0; i < 50; i++) {
            String name = "User" + i;
            int age = 20 + (i % 41); // Ages between 20 and 60
            String city = getRandomCity(random);
            String insertCommand = String.format("INSERT INTO users VALUES (%d, '%s', %d, '%s')", i, name, age, city);
            dbEngine.executeSQL(insertCommand);
        }
        System.out.println("Prepopulating products");
        // Insert initial products
        for (int i = 0; i < 50; i++) {
            String productName = "Product" + i;
            double price = 10 + (i % 990); // Prices between $10 and $1000
            String category = getRandomCategory(random);
            String insertCommand = String.format("INSERT INTO products VALUES (%d, '%s', %.2f, '%s')", i, productName,
                    price, category);
            dbEngine.executeSQL(insertCommand);
        }
        System.out.println("Prepopulating orders");
        // Insert initial orders
        for (int i = 0; i < 50; i++) {
            int user_id = random.nextInt(9999);
            int product_id = random.nextInt(9999);
            int quantity = random.nextInt(1, 100);
            String category = getRandomCategory(random);
            String insertCommand = String.format("INSERT INTO orders VALUES (%d, %d, %d, %d)", i, user_id, product_id,
                    quantity);
            dbEngine.executeSQL(insertCommand);
        }
    }

    // Helper method to return a random city
    private static String getRandomCity(Random random) {
        String[] cities = { "New York", "Los Angeles", "Chicago", "Boston", "Miami", "Seattle", "Austin", "Dallas",
                "Atlanta", "Denver" };
        return cities[random.nextInt(cities.length)];
    }

    // Helper method to return a random category for products
    private static String getRandomCategory(Random random) {
        String[] categories = { "Electronics", "Appliances", "Clothing", "Furniture", "Toys", "Sports", "Books",
                "Beauty", "Garden" };
        return categories[random.nextInt(categories.length)];
    }


    // autoEvaluate3 is a copy of autoEvaluate with error percentage printed at the end
    // requires new versions of the other methods that return the string of the
    // executed query
    public static void autoEvaluate3() {

        // Set the number of queries to execute
        int numberOfQueries = 100000;

        // Container to track the number of errors
        double errors = 0.0;

        // create database
        dbEngine.executeSQL("CREATE DATABASE EvaluationDB");

        // Use the database
        dbEngine.executeSQL("USE EvaluationDB");

        // Create tables
        dbEngine.executeSQL("CREATE TABLE users (id, name, age, city)");
        dbEngine.executeSQL("CREATE TABLE products (id, name, price, category)");
        dbEngine.executeSQL("CREATE TABLE orders (id, user_id, product_id, quantity)");

        // Random data generator
        Random random = new Random();

        // Prepopulate the tables in preparation for evaluation
        prepopulateTables(random);
        // Capture the start time
        long startTime = System.nanoTime();
        // Loop to simulate millions of queries
        for (int i = 0; i < numberOfQueries; i++) {
            int queryType = random.nextInt(6); // Randomly choose the type of query to execute
            String queryResponse = null;

            switch (queryType) {
                case 0: // INSERT query
                    queryResponse = insertRandomDataAlt(random);
                    break;
                case 1: // SELECT query (simple)
                    queryResponse = selectRandomDataAlt(random);
                    break;
                case 2: // UPDATE query
                    queryResponse = updateRandomDataAlt(random);
                    break;
                case 3: // DELETE query
                    queryResponse = deleteRandomDataAlt(random);
                    break;
                case 4: // Complex SELECT query with WHERE, AND, OR, >, <, LIKE
                    queryResponse = complexSelectQueryAlt(random);
                    break;
                case 5: // Complex UPDATE query with WHERE
                    queryResponse = complexUpdateQueryAlt(random);
                    break;
            }

            // Check if the query response contains an error message
            if (queryResponse.contains("ERROR")) {
                errors++;
            }

            // Print progress every 100,000 queries
            if (i % 10000 == 0) {
                System.out.println("Processed " + i + " queries...");
            }
        }
        // Capture the end time
        long endTime = System.nanoTime();
        // Calculate the elapsed time in milliseconds
        long elapsedTime = (endTime - startTime) / 1_000_000;
        // Print the elapsed time
        System.out.println("Processed " + numberOfQueries + " queries in " + elapsedTime + " milliseconds.");
        System.out.println("Finished processing " + numberOfQueries + " queries.");
        // Calculate and print the error percentage
        double errorPercentage = (errors / numberOfQueries) * 100;
        System.out.println("Error percentage: " + errorPercentage + "%");
    }

    /*
     * Below are the alternative methods that return the string of the executed query
     */

    // Helper method to insert random data into users, products, or orders table
    private static String insertRandomDataAlt(Random random) {
        int tableChoice = random.nextInt(3);
        String queryResponse = null;
        switch (tableChoice) {
            case 0: // Insert into users table
                int id = random.nextInt(10000) + 10000;
                String name = "User" + id;
                int age = random.nextInt(60) + 20;
                String city = getRandomCity(random);
                String insertUserQuery = "INSERT INTO users VALUES (" + id + ", '" + name + "', " + age + ", '" + city
                        + "')";
                queryResponse = dbEngine.executeSQL(insertUserQuery);
                break;
            case 1: // Insert into products table
                int productId = random.nextInt(1000) + 10000;
                String productName = "Product" + productId;
                double price = 50 + (random.nextDouble() * 1000);
                String category = getRandomCategory(random);
                String insertProductQuery = "INSERT INTO products VALUES (" + productId + ", '" + productName + "', "
                        + price + ", '" + category + "')";
                queryResponse = dbEngine.executeSQL(insertProductQuery);
                break;
            case 2: // Insert into orders table
                int orderId = random.nextInt(10000) + 1;
                int userId = random.nextInt(10000) + 1;
                int productIdRef = random.nextInt(1000) + 1;
                int quantity = random.nextInt(10) + 1;
                String insertOrderQuery = "INSERT INTO orders VALUES (" + orderId + ", " + userId + ", " + productIdRef
                        + ", " + quantity + ")";
                queryResponse = dbEngine.executeSQL(insertOrderQuery);
                break;
        }
        return queryResponse;
    }

    // Helper method to randomly select data from tables
    private static String selectRandomDataAlt(Random random) {
        int tableChoice = random.nextInt(3);
        String selectQuery;
        switch (tableChoice) {
            case 0:
                selectQuery = "SELECT * FROM users";
                break;
            case 1:
                selectQuery = "SELECT * FROM products";
                break;
            case 2:
                selectQuery = "SELECT * FROM orders";
                break;
            default:
                selectQuery = "SELECT * FROM users";
        }
        return dbEngine.executeSQL(selectQuery);
    }

    // Helper method to update random data in the tables
    private static String updateRandomDataAlt(Random random) {
        int tableChoice = random.nextInt(3);
        String queryResponse = null;
        switch (tableChoice) {
            case 0: // Update users table
                int id = random.nextInt(10000) + 1;
                int newAge = random.nextInt(60) + 20;
                String updateUserQuery = "UPDATE users SET age = " + newAge + " WHERE id = " + id;
                queryResponse = dbEngine.executeSQL(updateUserQuery);
                break;
            case 1: // Update products table
                int productId = random.nextInt(1000) + 1;
                double newPrice = 50 + (random.nextDouble() * 1000);
                String updateProductQuery = "UPDATE products SET price = " + newPrice + " WHERE id = " + productId;
                queryResponse = dbEngine.executeSQL(updateProductQuery);
                break;
            case 2: // Update orders table
                int orderId = random.nextInt(10000) + 1;
                int newQuantity = random.nextInt(10) + 1;
                String updateOrderQuery = "UPDATE orders SET quantity = " + newQuantity + " WHERE id = " + orderId;
                queryResponse = dbEngine.executeSQL(updateOrderQuery);
                break;
        }
        return queryResponse;
    }

    // Helper method to delete random data from tables
    private static String deleteRandomDataAlt(Random random) {
        int tableChoice = random.nextInt(3);
        String queryResponse = null;
        switch (tableChoice) {
            case 0: // Delete from users table
                int userId = random.nextInt(10000) + 1;
                String deleteUserQuery = "DELETE FROM users WHERE id = " + userId;
                queryResponse = dbEngine.executeSQL(deleteUserQuery);
                break;
            case 1: // Delete from products table
                int productId = random.nextInt(1000) + 1;
                String deleteProductQuery = "DELETE FROM products WHERE id = " + productId;
                queryResponse = dbEngine.executeSQL(deleteProductQuery);
                break;
            case 2: // Delete from orders table
                int orderId = random.nextInt(10000) + 1;
                String deleteOrderQuery = "DELETE FROM orders WHERE id = " + orderId;
                queryResponse = dbEngine.executeSQL(deleteOrderQuery);
                break;
        }
        return queryResponse;
    }

    // Helper method to execute a complex SELECT query with WHERE, AND, OR, >, <,
    // LIKE
    private static String complexSelectQueryAlt(Random random) {
        int tableChoice = random.nextInt(2); // Complex queries only on users and products for now
        String complexSelectQuery;
        switch (tableChoice) {
            case 0: // Complex SELECT on users
                int minAge = random.nextInt(20) + 20;
                int maxAge = minAge + random.nextInt(30);
                String city = getRandomCity(random);
                complexSelectQuery = "SELECT * FROM users WHERE age > " + minAge + " AND age < " + maxAge;
                break;
            case 1: // Complex SELECT on products
                double minPrice = 50 + (random.nextDouble() * 200);
                double maxPrice = minPrice + random.nextDouble() * 500;
                complexSelectQuery = "SELECT * FROM products WHERE price > " + minPrice + " AND price < " + maxPrice;
                break;
            case 2: // Complex SELECT on products
                double minPrice2 = 50 + (random.nextDouble() * 200);
                String category = getRandomCategory(random);
                complexSelectQuery = "SELECT * FROM products WHERE price > " + minPrice2 + " AND category = "
                        + category;
                break;
            default:
                complexSelectQuery = "SELECT * FROM users";
        }
        return dbEngine.executeSQL(complexSelectQuery);
    }

    // Helper method to execute a complex UPDATE query with WHERE
    private static String complexUpdateQueryAlt(Random random) {
        int tableChoice = random.nextInt(2); // Complex updates only on users and products for now
        String queryResponse = null;
        switch (tableChoice) {
            case 0: // Complex UPDATE on users
                int newAge = random.nextInt(60) + 20;
                String city = getRandomCity(random);
                String updateUserQuery = "UPDATE users SET age = " + newAge + " WHERE city = '" + city + "'";
                queryResponse = dbEngine.executeSQL(updateUserQuery);
                break;
            case 1: // Complex UPDATE on products
                double newPrice = 50 + (random.nextDouble() * 1000);
                String category = getRandomCategory(random);
                String updateProductQuery = "UPDATE products SET price = " + newPrice + " WHERE category = '" + category
                        + "'";
                queryResponse = dbEngine.executeSQL(updateProductQuery);
                break;
        }
        return queryResponse;
    }
}
