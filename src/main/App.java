package main;
import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import entity.Buyer;
import entity.Product;
import entity.Purchase;

import java.util.List;


import tools.IDGenerator;
import tools.Input;
public class App {
    private List<Product> productList = new ArrayList<>();
    private List<Buyer> buyerList = new ArrayList<>();
    private List<Purchase> purchaseList = new ArrayList<>();

    private Connection connection;

    public App() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/musicalShop";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);
            createTablesIfNotExist();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed. Check URL, username, and password.");
            e.printStackTrace();
        }
    }
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearDatabase() {
        try {
            Statement statement = connection.createStatement();

            // Truncate or delete all records from your tables
            String clearProductsQuery = "DELETE FROM Product";
            statement.executeUpdate(clearProductsQuery);

            String clearBuyersQuery = "DELETE FROM Buyer";
            statement.executeUpdate(clearBuyersQuery);

            String clearPurchaseQuery = "DELETE FROM Purchase";
            statement.executeUpdate(clearPurchaseQuery);

            // Similarly, clear other tables if needed

            System.out.println("All records deleted from the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void listProductsFromDatabase() {
        productList.clear(); // Clear the list before fetching data from the database

        try {
            Statement statement = connection.createStatement();
            String selectProductsQuery = "SELECT * FROM Product";
            ResultSet resultSet = statement.executeQuery(selectProductsQuery);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");

                Product product = new Product(type, name, price, stock);
                product.setId(id); // Set the ID obtained from the database
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void createTablesIfNotExist() {
        try {
            Statement statement = connection.createStatement();
            String createProductTableQuery = "CREATE TABLE IF NOT EXISTS Product (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "type VARCHAR(255)," +
                    "name VARCHAR(255)," +
                    "price DOUBLE," +
                    "stock INT)";
            statement.executeUpdate(createProductTableQuery);

            // Similarly, create other tables (Buyer, Purchase) if needed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void listBuyersFromDatabase() {
        try {
            Statement statement = connection.createStatement();
            String selectBuyersQuery = "SELECT * FROM Buyer";
            ResultSet resultSet = statement.executeQuery(selectBuyersQuery);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String secondName = resultSet.getString("second_name");
                String phoneNumber = resultSet.getString("phone_number");

                Buyer buyer = new Buyer(name, secondName, phoneNumber);
                buyer.setId(id); // Set the ID obtained from the database
                buyerList.add(buyer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    void run() {

        boolean repeat = true;
        System.out.println("------ Musical Instruments Shop ------");

        do {
            System.out.println("List tasks:");
            System.out.println("0. Exit\n" +
                    "1. Add a product\n" +
                    "2. Add a buyer\n" +
                    "3. List of buyers\n" +
                    "4. Product list\n" +
                    "5. Purchase the product by the user\n" +
                    "6. List of purchased products for the selected user\n" +
                    "7. Add money to the user\n" +
                    "8. Display the cost of sold items\n" +
                    "9. Display top buyers\n" +
                    "10. Display top sold items\n" +
                    "11. Edit product\n" +
                    "12. Edit buyer\n" +
                    "13. Clear database\n" +
                    "14. Time until next discount");
            System.out.println("Task number: ");
            int task = Input.inputNumberFromRange(0, 14);

            switch (task) {
                case 0:
                    repeat = false;
                    break;
                case 1:
                    addProduct();
                    break;
                case 2:
                    addBuyer();
                    break;
                case 3:
                    listBuyers();
                    listBuyersFromDatabase();
                    break;
                case 4:
                    listProducts();
                    listProductsFromDatabase();
                    break;
                case 5:
                    purchaseProduct();
                    break;
                case 6:
                    listPurchasedProducts();
                    break;
                case 7:
                    addMoneyToBuyer();
                    break;
                case 8:
                    displayTotalSales();
                    break;
                case 9:
                    topBuyers();
                    break;
                case 10:
                    topSoldItems();
                    break;
                case 11:
                    editProduct();
                    break;
                case 12:
                    editBuyer();
                    break;
                case 13:
                    clearDatabase();
                    break;
                case 14:
                    timeUntilNextDiscount();
                    break;
                default:
                    System.out.println("no such option!");
            }
        } while (repeat);

        saveData();
        closeConnection();


    }
    private void timeUntilNextDiscount() {
        LocalDateTime discountStartTime = LocalDateTime.of(2023, Month.DECEMBER, 19, 0, 0, 0);
        LocalDateTime discountEndTime = LocalDateTime.of(2023, Month.DECEMBER, 24, 23, 59, 59);
        LocalDateTime now = LocalDateTime.now();

        String timeLeft;

        if (now.isBefore(discountStartTime)) {
            Duration duration = Duration.between(now, discountStartTime);
            timeLeft = formatDuration(duration, "until the discount starts");
        } else if (now.isAfter(discountEndTime)) {
            timeLeft = "Discount has ended";
        } else {
            Duration duration = Duration.between(now, discountEndTime);
            timeLeft = formatDuration(duration, "until the discount ends");
        }

        System.out.println("Time remaining: " + timeLeft);
    }

    private String formatDuration(Duration duration, String status) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%d days, %d hours, %d minutes, %d seconds %s",
                days, hours, minutes, seconds, status);
    }


    private void addProduct() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter product type or enter [MENU] to return back: ");
        String type = scanner.nextLine();
        if (type.equalsIgnoreCase("menu")) {
            return;
        }

        System.out.println("Enter product name: ");
        String name = scanner.nextLine();

        System.out.println("Enter product price: ");
        double price = scanner.nextDouble();

        System.out.println("Enter product stock: ");
        int stock = scanner.nextInt();

        Product product = new Product(type, name, price, stock);
        productList.add(product);

        int productId = IDGenerator.generateRandomID(); // Generate a random ID
        String insertProductQuery = "INSERT INTO Product (id, type, name, price, stock) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertProductQuery);
            preparedStatement.setInt(1, productId); // Set the product ID
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, name);
            preparedStatement.setDouble(4, price);
            preparedStatement.setInt(5, stock);
            preparedStatement.executeUpdate();
            System.out.println("Product added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void addBuyer() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter buyer name or enter [MENU] to return back: ");
        String name = scanner.nextLine();
        if (name.equalsIgnoreCase("menu")) {
            return;
        }

        System.out.println("Enter buyer second name: ");
        String secondName = scanner.nextLine();

        System.out.println("Enter buyer phone number: ");
        String phoneNumber = scanner.nextLine();

        Buyer buyer = new Buyer(name, secondName, phoneNumber);
        buyerList.add(buyer);

        int buyerId = IDGenerator.generateRandomID(); // Generate a random ID
        double money = 0.0; // Set a default value for 'money'

        String insertBuyerQuery = "INSERT INTO Buyer (id, name, second_name, phone_number, money) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertBuyerQuery);
            preparedStatement.setInt(1, buyerId); // Set the buyer ID
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, secondName);
            preparedStatement.setString(4, phoneNumber);
            preparedStatement.setDouble(5, money); // Set the 'money' value
            preparedStatement.executeUpdate();
            System.out.println("Buyer added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void listProducts() {
        try {
            Statement statement = connection.createStatement();
            String selectProductsQuery = "SELECT * FROM Product";
            ResultSet resultSet = statement.executeQuery(selectProductsQuery);

            System.out.println("Products list:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");

                System.out.println("[ID " + id + "] - " + type + " - " + name + " - $" + price + " - Stock: " + stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void listBuyers() {
        try {
            Statement statement = connection.createStatement();
            String selectBuyersQuery = "SELECT * FROM Buyer";
            ResultSet resultSet = statement.executeQuery(selectBuyersQuery);

            System.out.println("Buyers list:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String secondName = resultSet.getString("second_name");
                String phoneNumber = resultSet.getString("phone_number");

                System.out.println("[ID " + id + "] - " + name + " - " + secondName + " - " + phoneNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Buyer findBuyerById(int buyerId) {
        // Populate buyerList from the database
        listBuyersFromDatabase();

        for (Buyer b : buyerList) {
            if (b.getId() == buyerId) {
                return b;
            }
        }
        return null; // If the buyer is not found
    }

    private Product findProductById(int productId) {
        listProductsFromDatabase(); // Populate productList from the database

        for (Product p : productList) {
            if (p.getId() == productId) {
                return p;
            }
        }
        return null; // If the product is not found
    }

    private void purchaseProduct() {
        System.out.println("0. Return to menu\n1. Continue");
        Scanner scanner = new Scanner(System.in);
        int purchaseOptions = scanner.nextInt();
        if (purchaseOptions == 0) {
            return;
        }

        // Display buyers with their IDs
        System.out.println("Select a buyer ID:");
        listBuyers();
        int buyerId = scanner.nextInt();

        Buyer buyer = findBuyerById(buyerId);

        if (buyer != null) {
            // Proceed with the purchase
        } else {
            System.out.println("Buyer not found with that ID!");
        }

        if (buyer == null) {
            System.out.println("Buyer not found with that ID!");
            return;
        }

        // Display products with their IDs
        System.out.println("Select a product ID:");
        listProducts();
        int productId = scanner.nextInt();

        Product product = findProductById(productId);

        if (product != null) {
            // Proceed with the purchase
        } else {
            System.out.println("Product not found with that ID!");
        }

        if (product == null) {
            System.out.println("Product not found with that ID!");
            return;
        }

        if (buyerHasEnoughMoney(buyer, product) && product.getStock() > 0) {
            double productPrice = product.getPrice();
            double buyerFunds = getBuyerFundsFromDatabase(buyer.getId());

            if (buyerFunds >= productPrice) {
                int currentStock = product.getStock();
                if (currentStock > 0) {
                    // Deduct stock and funds, perform purchase
                    currentStock--;
                    buyerFunds -= productPrice;

                    // Update product stock in the database
                    updateProductStockInDatabase(product.getId(), currentStock);

                    // Update buyer's funds in the database
                    updateBuyerMoneyInDatabase(buyer.getId(), buyerFunds);

                    try {
                        PreparedStatement purchaseStatement = connection.prepareStatement(
                                "INSERT INTO Purchase (buyer_id, product_id) VALUES (?, ?)");
                        purchaseStatement.setInt(1, buyer.getId());
                        purchaseStatement.setInt(2, product.getId());
                        purchaseStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // ... rest of the purchase process
                } else {
                    System.out.println("Out of stock!");
                }
            } else {
                System.out.println("Not enough money!");
            }
        } else {
            System.out.println("Product not found or not enough stock!");
        }
    }

    private void updateProductStockInDatabase(int productId, int newStock) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Product SET stock = ? WHERE id = ?");
            preparedStatement.setInt(1, newStock);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBuyerMoneyInDatabase(int buyerId, double newFunds) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Buyer SET money = ? WHERE id = ?");
            preparedStatement.setDouble(1, newFunds);
            preparedStatement.setInt(2, buyerId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void listPurchasedProducts() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Buyers list:");
        listBuyers();

        System.out.println("Enter the buyer ID to list purchased products: ");
        int buyerId = scanner.nextInt();

        System.out.println("Choose time interval:");
        System.out.println("1. In a day");
        System.out.println("2. In a week");
        System.out.println("3. In a month");
        System.out.println("4. In a year");
        System.out.println("5. All time");
        int intervalChoice = scanner.nextInt();

        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (intervalChoice) {
            case 1:
                startDate = endDate.minusDays(1);
                break;
            case 2:
                startDate = endDate.minusWeeks(1);
                break;
            case 3:
                startDate = endDate.minusMonths(1);
                break;
            case 4:
                startDate = endDate.minusYears(1);
                break;
            case 5:
            default:
                startDate = LocalDateTime.MIN;
                break;
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT Product.type, Product.name, Product.price, Product.stock FROM Purchase " +
                            "INNER JOIN Product ON Purchase.product_id = Product.id " +
                            "WHERE Purchase.buyer_id = ? AND Purchase.purchase_date BETWEEN ? AND ?");
            preparedStatement.setInt(1, buyerId);
            preparedStatement.setObject(2, startDate);
            preparedStatement.setObject(3, endDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Purchased products for Buyer ID " + buyerId + " within the selected interval:");
            while (resultSet.next()) {
                String productType = resultSet.getString("type");
                String productName = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");

                System.out.println(productType + " - " + productName + " - $" + price + " - Stock: " + stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void displayTotalSales() {
        System.out.println("Choose time interval:");
        System.out.println("1. In a day");
        System.out.println("2. In a week");
        System.out.println("3. In a month");
        System.out.println("4. In a year");
        System.out.println("5. All time");

        Scanner scanner = new Scanner(System.in);
        int intervalChoice = scanner.nextInt();

        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (intervalChoice) {
            case 1:
                startDate = endDate.minusDays(1);
                break;
            case 2:
                startDate = endDate.minusWeeks(1);
                break;
            case 3:
                startDate = endDate.minusMonths(1);
                break;
            case 4:
                startDate = endDate.minusYears(1);
                break;
            case 5:
            default:
                startDate = LocalDateTime.MIN;
                break;
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT SUM(Product.price) AS total_sales FROM Purchase " +
                            "INNER JOIN Product ON Purchase.product_id = Product.id " +
                            "WHERE Purchase.purchase_date BETWEEN ? AND ?");
            preparedStatement.setObject(1, startDate);
            preparedStatement.setObject(2, endDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double totalSales = resultSet.getDouble("total_sales");
                System.out.println("Total sales amount within the selected interval: $" + totalSales);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean buyerHasEnoughMoney(Buyer buyer, Product product) {
        // Fetch the buyer's available funds from the database
        double buyerFunds = getBuyerFundsFromDatabase(buyer.getId());
        return buyerFunds >= product.getPrice();
    }

    private double getBuyerFundsFromDatabase(int buyerId) {
        // Retrieve the buyer's funds from the database
        double funds = 0.0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT money FROM Buyer WHERE id = ?");
            preparedStatement.setInt(1, buyerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                funds = resultSet.getDouble("money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funds;
    }


    private void addMoneyToBuyer() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("0. Return to menu\n" + "1. Continue");
        int purchaseOptions = scanner.nextInt();
        if (purchaseOptions == 0) {
            return;
        }

        System.out.println("Select a buyer to add money (by ID):");
        listBuyers(); // List buyers with IDs

        System.out.println("Enter the ID of the buyer:");
        int buyerId = scanner.nextInt(); // Read the buyer's ID

        boolean found = false;

        // Find the buyer in the database by ID
        String selectBuyerQuery = "SELECT * FROM Buyer WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectBuyerQuery);
            preparedStatement.setInt(1, buyerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                found = true;
                System.out.println("Enter the amount of money to add: ");
                double amount = scanner.nextDouble();

                // Update the buyer's money locally
                for (Buyer buyer : buyerList) {
                    if (buyer.getId() == buyerId) {
                        buyer.addMoney(amount);
                        System.out.println("Money added successfully to " + buyer.getName());
                        break;
                    }
                }

                // Update the database with the added money for the buyer
                String updateMoneyQuery = "UPDATE Buyer SET money = money + ? WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateMoneyQuery);
                updateStatement.setDouble(1, amount);
                updateStatement.setInt(2, buyerId);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!found) {
            System.out.println("Buyer not found with that ID!");
        }
    }

    private void topBuyers() {
        try {
            Statement statement = connection.createStatement();
            String topBuyersQuery = "SELECT Buyer.id, Buyer.name, COUNT(*) AS items_bought FROM Purchase " +
                    "INNER JOIN Buyer ON Purchase.buyer_id = Buyer.id " +
                    "GROUP BY Buyer.id ORDER BY items_bought DESC";
            ResultSet resultSet = statement.executeQuery(topBuyersQuery);

            int rank = 1;
            System.out.println("Top Buyers based on items bought:");
            while (resultSet.next()) {
                int buyerId = resultSet.getInt("id");
                String buyerName = resultSet.getString("name");
                int itemsBought = resultSet.getInt("items_bought");

                System.out.println(rank + ". " + buyerName + " - Items Bought: " + itemsBought);
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void topSoldItems() {
        System.out.println("Choose time interval:");
        System.out.println("1. In a day");
        System.out.println("2. In a week");
        System.out.println("3. In a month");
        System.out.println("4. In a year");
        System.out.println("5. All time");

        Scanner scanner = new Scanner(System.in);
        int intervalChoice = scanner.nextInt();

        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (intervalChoice) {
            case 1:
                startDate = endDate.minusDays(1);
                break;
            case 2:
                startDate = endDate.minusWeeks(1);
                break;
            case 3:
                startDate = endDate.minusMonths(1);
                break;
            case 4:
                startDate = endDate.minusYears(1);
                break;
            case 5:
            default:
                startDate = LocalDateTime.MIN;
                break;
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT Product.name, COUNT(*) AS quantity_sold FROM Purchase " +
                            "INNER JOIN Product ON Purchase.product_id = Product.id " +
                            "WHERE Purchase.purchase_date BETWEEN ? AND ? " +
                            "GROUP BY Product.name ORDER BY quantity_sold DESC LIMIT 5");
            preparedStatement.setObject(1, startDate);
            preparedStatement.setObject(2, endDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            int rank = 1;
            System.out.println("Top Sold Items within the selected interval:");
            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                int quantitySold = resultSet.getInt("quantity_sold");

                System.out.println(rank + ". " + productName + " - Quantity Sold: " + quantitySold);
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a product to edit (by ID):");
        listProducts();
        int productId = scanner.nextInt(); // Read the product's ID

        Product product = findProductById(productId);

        if (product != null) {


            // Clear the input buffer
            scanner.nextLine();

            System.out.println("Enter new product type: ");
            String type = scanner.nextLine();

            System.out.println("Enter new product name: ");
            String name = scanner.nextLine();

            System.out.println("Enter new product price: ");
            double price = scanner.nextDouble();

            System.out.println("Enter new product stock: ");
            int stock = scanner.nextInt();

            // Update the product information except for the ID
            product.setType(type);
            product.setName(name);
            product.setPrice(price);
            product.setStock(stock);

            // Update the product in the database
            updateProductInDatabase(product);
            System.out.println("Product details updated successfully!");
        } else {
            System.out.println("Product not found with that ID!");
        }
    }

    private void updateProductInDatabase(Product product) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Product SET type = ?, name = ?, price = ?, stock = ? WHERE id = ?");
            preparedStatement.setString(1, product.getType());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getStock());
            preparedStatement.setInt(5, product.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void editBuyer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a buyer to edit (by ID):");
        listBuyers();
        int buyerId = scanner.nextInt(); // Read the buyer's ID

        Buyer buyer = findBuyerById(buyerId);

        if (buyer != null) {

            // Clear the input buffer
            scanner.nextLine();

            System.out.println("Enter new buyer name: ");
            String name = scanner.nextLine();

            System.out.println("Enter new buyer second name: ");
            String secondName = scanner.nextLine();

            System.out.println("Enter new buyer phone number: ");
            String phoneNumber = scanner.nextLine();

            // Update the buyer information except for the ID
            buyer.setName(name);
            buyer.setSecondName(secondName);
            buyer.setPhoneNumber(phoneNumber);

            // Update the buyer in the database
            updateBuyerInDatabase(buyer);
            System.out.println("Buyer details updated successfully!");
        } else {
            System.out.println("Buyer not found with that ID!");
        }
    }

    private void updateBuyerInDatabase(Buyer buyer) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Buyer SET name = ?, second_name = ?, phone_number = ? WHERE id = ?");
            preparedStatement.setString(1, buyer.getName());
            preparedStatement.setString(2, buyer.getSecondName());
            preparedStatement.setString(3, buyer.getPhoneNumber());
            preparedStatement.setInt(4, buyer.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data.txt"))) {
            // Save products
            for (Product product : productList) {
                writer.println("Product," + product.getType() + "," + product.getName() + "," + product.getPrice() + "," + product.getStock());
            }
            // Save buyers
            for (Buyer buyer : buyerList) {
                writer.println("Buyer," + buyer.getName() + "," + buyer.getSecondName() + "," + buyer.getPhoneNumber() + "," + buyer.getMoney());
                // Save purchased products for each buyer
                for (Purchase purchase : purchaseList) {
                    if (purchase.getBuyer().equals(buyer)) {
                        writer.println("Purchase," + productList.indexOf(purchase.getProduct()));
                    }
                }
            }
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    switch (parts[0]) {
                        case "Product":
                            productList.add(new Product(parts[1], parts[2], Double.parseDouble(parts[3]), Integer.parseInt(parts[4])));
                            break;
                        case "Buyer":
                            Buyer buyer = new Buyer(parts[1], parts[2], parts[3]);
                            buyer.setMoney(Double.parseDouble(parts[4]));
                            buyerList.add(buyer);
                            break;
                        case "Purchase":
                            int productIndex = Integer.parseInt(parts[1]);
                            if (!buyerList.isEmpty()) {
                                Purchase purchase = new Purchase(buyerList.get(buyerList.size() - 1), productList.get(productIndex));
                                purchaseList.add(purchase);
                            }
                            break;
                        default:
                            System.out.println("Unknown entry in file");
                            break;
                    }
                }
            }
            System.out.println("Data loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }


}
