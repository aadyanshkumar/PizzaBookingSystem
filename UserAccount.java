import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserAccount {

    public static Scanner sc = new Scanner(System.in);

    public static int currentUserId = -1;
    public static String currentUserName = "";

    public static void orderHistory() {
        if (currentUserId <= 0) {
            System.out.println("⚠ Please login first!");
            return;
        }
        PizzaService.printOrderHistoryByUser(currentUserId);
    }

    public static String setPassword() {
        while (true) {
            System.out.print("Set a password: ");
            String password = sc.next();
            System.out.print("Confirm password: ");
            String confPass = sc.next();
            if (confPass.equals(password)) {
                return password;
            } else {
                System.out.println("Password not matched: re-set password..");
            }
        }
    }

    public static boolean signUp() {
        System.out.println();
        System.out.println("---------- SIGN-UP PAGE ----------");
        System.out.print("Enter your name: ");
        String newName = sc.next();
        System.out.print("Enter your email address: ");
        String newEmail = sc.next();
        System.out.print("Enter your age: ");
        int newAge = readPositiveInt();
        System.out.print("Enter your phone no: ");
        String newPhone = sc.next();
        String newPass = setPassword();

        String query = "INSERT INTO userdata (name, email, password, phone, age) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = PizzaService.getConnection();
             PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newName);
            ps.setString(2, newEmail);
            ps.setString(3, newPass);
            ps.setString(4, newPhone);
            ps.setInt(5, newAge);

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        currentUserId = keys.getInt(1);
                        currentUserName = newName;
                    }
                }
                System.out.println("Welcome " + newName + "! your account has been created successfully.");
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static boolean login() {
        System.out.println();
        System.out.println("---------- LOGIN PAGE ----------");
        System.out.print("Enter email : ");
        String loginEmail = sc.next();
        System.out.print("Enter password: ");
        String pass = sc.next();

        String query = "SELECT id, name FROM userdata WHERE email = ? AND password = ?";
        try (Connection con = PizzaService.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, loginEmail);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentUserId = rs.getInt("id");
                    currentUserName = rs.getString("name");
                    System.out.println("Welcome! " + currentUserName);
                    return true;
                } else {
                    System.out.println("User not match. Please re-enter your information");
                    System.out.println("Press 1 for back\nPress 2 for re-login");
                    System.out.print("Enter: ");
                    int back = readPositiveInt();
                    if (back == 1) {
                        return false;
                    } else if (back == 2) {
                        return login();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static int readPositiveInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.next();
        }
        int n = sc.nextInt();
        if (n <= 0) {
            System.out.print("Enter a positive number: ");
            return readPositiveInt();
        }
        return n;
    }
}
