import java.sql.*;
import java.util.*;

public class Inventory {
    private static final String URL = "jdbc:sqlite:inventory.db"; // SQLite URL
    // Remove MySQL credentials (no username/password for SQLite)

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL); // SQLite does not require username/password
    }

    // Item class to represent inventory items
    static class Item {
        private int id;
        private String name;
        private int quantity;
        private double price;

        public Item(int id, String name, int quantity, double price) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public Item(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    // Add a new item to the database
    public static void addItem(Item item) throws SQLException {
        String query = "INSERT INTO items (name, quantity, price) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getQuantity());
            stmt.setDouble(3, item.getPrice());
            stmt.executeUpdate();
        }
    }

    // Retrieve all items from the database
    public static List<Item> getAllItems() throws SQLException {
        String query = "SELECT * FROM items";
        List<Item> items = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        }
        return items;
    }

    // Update an existing item in the database
    public static void updateItem(Item item) throws SQLException {
        String query = "UPDATE items SET name = ?, quantity = ?, price = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getQuantity());
            stmt.setDouble(3, item.getPrice());
            stmt.setInt(4, item.getId());
            stmt.executeUpdate();
        }
    }

    // Delete an item from the database
    public static void deleteItem(int id) throws SQLException {
        String query = "DELETE FROM items WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Main method to test the functionality
    public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    while (true) {
        // Display the menu options
        System.out.println("Inventory Management System");
        System.out.println("1. Add Item");
        System.out.println("2. View All Items");
        System.out.println("3. Update Item");
        System.out.println("4. Delete Item");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline character

        switch (choice) {
            case 1:
                // Add item
                System.out.print("Enter item name: ");
                String name = scanner.nextLine();
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();
                System.out.print("Enter price: ");
                double price = scanner.nextDouble();
                scanner.nextLine();  // Consume newline character
                try {
                    addItem(new Item(name, quantity, price));
                    System.out.println("Item added successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                // View all items
                try {
                    List<Item> items = getAllItems();
                    System.out.println("Items in Inventory:");
                    for (Item item : items) {
                        System.out.println(item.getId() + " - " + item.getName() + " - " +
                                           item.getQuantity() + " - " + item.getPrice());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case 3:
                // Update item
                System.out.print("Enter item ID to update: ");
                int updateId = scanner.nextInt();
                scanner.nextLine();  // Consume newline character
                try {
                    List<Item> items = getAllItems();
                    Item itemToUpdate = null;
                    for (Item item : items) {
                        if (item.getId() == updateId) {
                            itemToUpdate = item;
                            break;
                        }
                    }
                    if (itemToUpdate != null) {
                        System.out.print("Enter new quantity: ");
                        itemToUpdate.setQuantity(scanner.nextInt());
                        System.out.print("Enter new price: ");
                        itemToUpdate.setPrice(scanner.nextDouble());
                        updateItem(itemToUpdate);
                        System.out.println("Item updated successfully.");
                    } else {
                        System.out.println("Item with ID " + updateId + " not found.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case 4:
                // Delete item
                System.out.print("Enter item ID to delete: ");
                int deleteId = scanner.nextInt();
                scanner.nextLine();  // Consume newline character
                try {
                    deleteItem(deleteId);
                    System.out.println("Item deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case 5:
                // Exit the program
                System.out.println("Exiting...");
                scanner.close();
                System.exit(0);
                break;

            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }
}

}
