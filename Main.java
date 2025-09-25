import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static void confirmOrder() {
        // Ask for pizza id
        System.out.print("\nEnter Pizza ID to order: ");
        int pizzaId = readPositiveInt();

        PizzaService.Pizza pizza = PizzaService.findPizzaById(pizzaId);
        if (pizza == null) {
            System.out.println("❌ Pizza not found!");
            return;
        }

        System.out.printf("You selected: %s (%s) - $%.2f%n", pizza.name, pizza.category, pizza.price);

        // Confirm booking
        System.out.print("Are you sure you want to book this pizza? (y/n): ");
        String confirm = sc.next();
        if (!(confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes"))) {
            System.out.println("Order cancelled.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = readPositiveInt();

        if (UserAccount.currentUserId <= 0) {
            System.out.println("⚠ Please login first!");
            return;
        }

        BillService.generateBill(pizza, quantity, UserAccount.currentUserId);
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

    public static void main(String[] args) {

        System.out.println("***************[ Welcome to Pizza Hut ]***************\n");

        // Auth loop
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.println("Press 1 for LogIn");
            System.out.println("Press 2 for SignUp (If you are a new customer)");
            System.out.println("-----------------------------------------------------------------");
            System.out.print("Enter: ");
            int userChoice = readPositiveInt();

            if (userChoice == 1) {
                loggedIn = UserAccount.login();
            } else if (userChoice == 2) {
                loggedIn = UserAccount.signUp();
            } else {
                System.out.println("You entered a wrong input, Please re-enter.");
            }
        }

        // Home menu loop
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("*************[ HOME PAGE ]*************");
            System.out.println("1. Veg");
            System.out.println("2. Non-Veg");
            System.out.println("3. Search Pizza by Name");
            System.out.println("4. Order History");
            System.out.println("5. Exit");
            System.out.println("----------------------------------------");
            System.out.print("Enter your choice: ");
            int choice = readPositiveInt();

            switch (choice) {
                case 1: {
                    String category = "Veg";
                    System.out.println("\nAll " + category + " Pizzas:");
                    PizzaService.printPizzasByCategory(category);
                    confirmOrder();
                    break;
                }
                case 2: {
                    String category = "Non-Veg";
                    System.out.println("\nAll " + category + " Pizzas:");
                    PizzaService.printPizzasByCategory(category);
                    confirmOrder();
                    break;
                }
                case 3: {
                    int back = PizzaService.printPizzaByName(); // prints matches
                    if (back != 0) {
                        confirmOrder();
                    }
                    break;
                }
                case 4: {
                    UserAccount.orderHistory();
                    break;
                }
                case 5: {
                    System.out.println("✅ Exit successfully!");
                    running = false;
                    break;
                }
                default:
                    System.out.println("❌ Invalid input, try again.");
            }
        }
    }
}
