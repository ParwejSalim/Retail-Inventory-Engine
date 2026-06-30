import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// 1. ABSTRACT CLASS (Abstraction)
abstract class Product {
    int id;
    String name;
    double price;
    int quantity;

    public Product(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // method that is defined by supermarket class
    abstract void display();
}

// 2. CONCRETE CLASS (Inheritance)
class SupermarketProduct extends Product {
    // We add a field to track how many of this specific item have been sold
    int soldCount = 0; 

    public SupermarketProduct(int id, String name, double price, int quantity) {
        super(id, name, price, quantity);
    }

    @Override
    void display() {
        System.out.printf("ID: %-5d | Name: %-15s | Price: Rs.%-8.2f | Stock: %-5d\n", 
                          id, name, price, quantity);
    }
}

// 3. BILLING CLASS (Encapsulation of Billing Logic)
class Billing {
    double total = 0;
    StringBuilder billDetails = new StringBuilder(); 

    void addToBill(SupermarketProduct p, int qty) {
        if (qty <= p.quantity) {
            double cost = qty * p.price;
            total += cost;
            
            // Update stock and sales history
            p.quantity -= qty;
            p.soldCount += qty;

            // Append this transaction to the bill string
            billDetails.append(String.format("%-15s x %-3d   Rs.%-8.2f\n", p.name, qty, cost));
            System.out.println("Added " + qty + " " + p.name + "(s) to bill.");
        } else {
            System.out.println("Sorry! Not enough stock for " + p.name + ". Available: " + p.quantity);
        }
    }

    void printBill(String shopName, String shopPhone) {
        System.out.println("\n==========================================");
        System.out.println("           " + shopName);
        System.out.println("         Phone: " + shopPhone);
        System.out.println("==========================================");
        System.out.println("Item            Qty     Price");
        System.out.println("------------------------------------------");
        System.out.print(billDetails);
        System.out.println("------------------------------------------");
        System.out.printf("TOTAL AMOUNT:           Rs.%.2f\n", total);
        System.out.println("==========================================\n");
        
        // Reset for the next customer
        total = 0;
        billDetails.setLength(0); 
    }
}

// 4. MAIN SYSTEM (The Controller)
public class SupermarketSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Used HashMap for O(1) access
        // Key = Product ID (Integer), Value = Product Object
        Map<Integer, SupermarketProduct> inventory = new HashMap<>();

        Billing bill = new Billing();
        String shopName = "SuperMart Patna";
        String shopPhone = "9876XX3210";

        // Pre-populating the store with some items
        inventory.put(101, new SupermarketProduct(101, "Rice", 50.0, 100));
        inventory.put(102, new SupermarketProduct(102, "Sugar", 40.0, 80));
        inventory.put(103, new SupermarketProduct(103, "Milk", 25.0, 50));
        inventory.put(104, new SupermarketProduct(104, "Bread", 30.0, 60));

        while (true) {
            System.out.println("\n--- SUPERMARKET MENU ---");
            System.out.println("1. Stock Management");
            System.out.println("2. Billing (Sell Items)");
            System.out.println("3. View Sales Report");
            System.out.println("4. Settings");
            System.out.println("5. Exit");
            System.out.print("Enter Choice: ");
            
            int choice = sc.nextInt();
            
            switch (choice) {
                case 1: // STOCK MANAGEMENT
                    System.out.println("\n--- Stock Management ---");
                    System.out.println("1. Update Existing Stock");
                    System.out.println("2. Add New Product");
                    System.out.println("3. View All Products");
                    System.out.print("Choice: ");
                    int stockChoice = sc.nextInt();

                    if (stockChoice == 1) {
                        System.out.print("Enter Product ID: ");
                        int id = sc.nextInt();
                        
                        // O(1) CHECK:
                        if (inventory.containsKey(id)) {
                            System.out.print("Enter Quantity to Add: ");
                            int qty = sc.nextInt();
                            SupermarketProduct p = inventory.get(id);
                            p.quantity += qty;
                            System.out.println("Stock updated successfully.");
                        } else {
                            System.out.println("Product ID not found!");
                        }

                    } else if (stockChoice == 2) {
                        System.out.print("Enter New ID: ");
                        int id = sc.nextInt();
                        if (inventory.containsKey(id)) {
                            System.out.println("Error: Product ID already exists.");
                        } else {
                            sc.nextLine(); // consume newline
                            System.out.print("Enter Name: ");
                            String name = sc.nextLine();
                            System.out.print("Enter Price: ");
                            double price = sc.nextDouble();
                            System.out.print("Enter Quantity: ");
                            int qty = sc.nextInt();
                            
                            // Add to HashMap
                            inventory.put(id, new SupermarketProduct(id, name, price, qty));
                            System.out.println("New product added!");
                        }

                    } else if (stockChoice == 3) {
                        System.out.println("\n--- Current Inventory ---");
                        // Iterate over HashMap Values
                        for (SupermarketProduct p : inventory.values()) {
                            p.display();
                        }
                    }
                    break;

                case 2: // BILLING
                    boolean billingActive = true;
                    while (billingActive) {
                        System.out.print("\nEnter Product ID to sell: ");
                        int id = sc.nextInt();

                        if (inventory.containsKey(id)) {
                            System.out.print("Enter Quantity: ");
                            int qty = sc.nextInt();
                            bill.addToBill(inventory.get(id), qty);
                        } else {
                            System.out.println("Invalid Product ID.");
                        }

                        System.out.print("Add more items? (1=Yes, 2=No/Print Bill): ");
                        int more = sc.nextInt();
                        if (more == 2) {
                            bill.printBill(shopName, shopPhone);
                            billingActive = false;
                        }
                    }
                    break;

                case 3: // SALES REPORT
                    System.out.println("\n--- Daily Sales Report ---");
                    double totalRevenue = 0;
                    System.out.printf("%-15s %-10s %-10s\n", "Product", "Sold Qty", "Revenue");
                    System.out.println("---------------------------------------");
                    
                    for (SupermarketProduct p : inventory.values()) {
                        if (p.soldCount > 0) {
                            double revenue = p.soldCount * p.price;
                            totalRevenue += revenue;
                            System.out.printf("%-15s %-10d Rs.%-8.2f\n", p.name, p.soldCount, revenue);
                        }
                    }
                    System.out.println("---------------------------------------");
                    System.out.printf("Total Store Revenue: Rs.%.2f\n", totalRevenue);
                    break;

                case 4: // SETTINGS
                    sc.nextLine(); // consume newline
                    System.out.print("Enter New Shop Name: ");
                    shopName = sc.nextLine();
                    System.out.print("Enter New Phone: ");
                    shopPhone = sc.nextLine();
                    System.out.println("Settings Updated!");
                    break;

                case 5: // EXIT
                    System.out.println("Exiting System. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
