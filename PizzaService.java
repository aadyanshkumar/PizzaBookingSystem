import java.sql.*;
import java.util.Scanner;

public class PizzaService {

    public static Scanner sc = new Scanner(System.in);

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/pizzastore";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root2005";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Lightweight DTO without creating a new file
    public static class Pizza {
        public final int id;
        public final String name;
        public final String category;
        public final double price;

        public Pizza(int id, String name, String category, double price) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
        }
    }

    public static int printPizzaByName() {
        System.out.print("Enter the name of Pizza: ");
        String search = sc.next();
        String query = "SELECT * FROM all_pizza WHERE name LIKE ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, search + "%");
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("ID\tName\t\t\tCategory\tPrice");
                System.out.println("------------------------------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String cat = rs.getString("catogories");
                    double price = rs.getDouble("price");
                    System.out.printf("%d\t%-20s\t%-10s\t$%.2f%n", id, name, cat, price);
                }
                if (!found) {
                    System.out.println("No pizzas found with name starting with: " + search);
                    System.out.println("Press 1 for Search again\nPress 0 for back page");
                    System.out.print("Enter: ");
                    int back = sc.nextInt();
                    if (back == 1) {
                        return printPizzaByName();
                    } else {
                        return 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 1;
    }

    public static void printPizzasByCategory(String category) {
        String query = "SELECT * FROM all_pizza WHERE catogories = ? ORDER BY price ASC, name ASC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("ID\tName\t\t\tCategory\tPrice");
                System.out.println("------------------------------------------------");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String cat = rs.getString("catogories");
                    double price = rs.getDouble("price");
                    System.out.printf("%d\t%-20s\t%-10s\t$%.2f%n", id, name, cat, price);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return a Pizza object instead of a ResultSet for safer lifecycle
    public static Pizza findPizzaById(int pizzaId) {
        String query = "SELECT id, name, catogories, price FROM all_pizza WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, pizzaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pizza(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("catogories"),
                            rs.getDouble("price")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Save order into order_history
    public static void saveOrder(int userId, int pizzaId, int quantity, double unitPrice, double totalPrice) {
        String query = "INSERT INTO order_history (user_id, pizza_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, pizzaId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);
            ps.setDouble(5, totalPrice);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Print orders for the logged-in user
    public static void printOrderHistoryByUser(int userId) {
        String query =
                "SELECT oh.id AS order_id, p.name, p.catogories, oh.quantity, oh.unit_price, oh.total_price, oh.order_time " +
                        "FROM order_history oh " +
                        "JOIN all_pizza p ON p.id = oh.pizza_id " +
                        "WHERE oh.user_id = ? " +
                        "ORDER BY oh.order_time DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n---------------- Your Order History ----------------");
                System.out.println("OrderID  Pizza                Category   Qty  Unit     Total     Date");
                System.out.println("--------------------------------------------------------------------------");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.printf("%-8d %-20s %-10s %-4d $%-7.2f $%-8.2f %s%n",
                            rs.getInt("order_id"),
                            rs.getString("name"),
                            rs.getString("catogories"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("total_price"),
                            rs.getTimestamp("order_time").toString());
                }
                if (!any) {
                    System.out.println("No past orders yet.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
