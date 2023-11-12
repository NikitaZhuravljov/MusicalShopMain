package main;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import entity.Buyer;
import entity.Product;
import entity.Purchase;

import tools.Input;
public class App {
    private List<Product> productList = new ArrayList<>();
    private List<Buyer> buyerList = new ArrayList<>();
    private List<Purchase> purchaseList = new ArrayList<>();


    void run() {
        boolean repeat = true;
        System.out.println("------ Musical Instruments Shop ------");
        do {
            System.out.println("List tasks:");
            System.out.println("0. Exit\n" +
                    "1. Add a product\n" +
                    "2. Add a buyer\n" +
                    "3. List of buyers\n" +
                    "4. entity.Product list\n" +
                    "5. entity.Purchase the product by the user\n" +
                    "6. List of purchased products for the selected user\n" +
                    "7. Add money to the user\n" +
                    "8. Display the cost of all items sold at all times");
            System.out.println("Task number: ");
            int task = Input.inputNumberFromRange(0, 8);

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
                    break;
                case 4:
                    listProducts();
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
            }
        } while (repeat);
    }

    private void addProduct() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter product type: ");
        String type = scanner.nextLine();

        System.out.println("Enter product name: ");
        String name = scanner.nextLine();

        System.out.println("Enter product price: ");
        double price = scanner.nextDouble();

        System.out.println("Enter product stock: ");
        int stock = scanner.nextInt();

        Product product = new Product(type, name, price, stock);
        productList.add(product);

        System.out.println("entity.Product added successfully!");
    }

    private void addBuyer() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter buyer name: ");
        String name = scanner.nextLine();

        System.out.println("Enter buyer email: ");
        String email = scanner.nextLine();

        System.out.println("Enter buyer address: ");
        String address = scanner.nextLine();

        Buyer buyer = new Buyer(name, email, address);
        buyerList.add(buyer);

        System.out.println("entity.Buyer added successfully!");
    }

    private void listBuyers() {
        System.out.println("List of Buyers:");
        for (int i = 0; i < buyerList.size(); i++) {
            System.out.println(i + 1 + ". " + buyerList.get(i));
        }
    }

    private void listProducts() {
        System.out.println("List of Products:");
        for (int i = 0; i < productList.size(); i++) {
            System.out.println(i + 1 + ". " + productList.get(i));
        }
    }

    private void purchaseProduct() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a buyer (enter buyer index):");
        listBuyers();
        int buyerIndex = Input.inputNumberFromRange(1, buyerList.size()) - 1;

        System.out.println("Select a product (enter product index):");
        listProducts();
        int productIndex = Input.inputNumberFromRange(1, productList.size()) - 1;

        Buyer buyer = buyerList.get(buyerIndex);
        Product product = productList.get(productIndex);

        if (buyerHasEnoughMoney(buyer, product) && product.getStock() > 0) {
            buyer.deductMoney(product.getPrice());
            product.setStock(product.getStock() - 1); // Decrease stock by 1
            Purchase purchase = new Purchase(buyer, product);
            purchaseList.add(purchase);
            System.out.println("entity.Purchase successful! Updated stock: " + product.getStock());
        } else if (product.getStock() == 0) {
            System.out.println("Sorry, this product is out of stock.");
        } else {
            System.out.println("Not enough money. Please add money to the buyer.");
        }
    }

    private void listPurchasedProducts() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a buyer to list purchased products (enter buyer index):");
        listBuyers();
        int buyerIndex = Input.inputNumberFromRange(1, buyerList.size()) - 1;

        Buyer buyer = buyerList.get(buyerIndex);

        System.out.println("List of purchased products for " + buyer.getName() + ":");
        for (Purchase purchase : purchaseList) {
            if (purchase.getBuyer().equals(buyer)) {
                System.out.println(purchase.getProduct());
            }
        }
    }

    private void displayTotalSales() {
        double totalSales = 0.0;
        for (Purchase purchase : purchaseList) {
            totalSales += purchase.getProduct().getPrice();
        }
        System.out.println("Total sales amount: $" + totalSales);
    }

    private boolean buyerHasEnoughMoney(Buyer buyer, Product product) {
        return buyer != null && product != null && buyer.getMoney() >= product.getPrice();
    }


    private void addMoneyToBuyer() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a buyer to add money (enter buyer index):");
        listBuyers();
        int buyerIndex = Input.inputNumberFromRange(1, buyerList.size()) - 1;

        Buyer buyer = buyerList.get(buyerIndex);

        System.out.println("Enter the amount of money to add: ");
        double amount = scanner.nextDouble();

        buyer.addMoney(amount);
        System.out.println("Money added successfully to " + buyer.getName() + ". Updated balance: $" + buyer.getMoney());
    }

}