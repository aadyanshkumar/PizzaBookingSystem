import java.text.DecimalFormat;

public class BillService {

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static void generateBill(PizzaService.Pizza pizza, int quantity, int currentUserId) {
        if (pizza == null) {
            System.out.println("❌ Pizza not found!");
            return;
        }
        if (quantity <= 0) {
            System.out.println("❌ Quantity must be positive!");
            return;
        }
        double unitPrice = pizza.price;
        double totalPrice = quantity * unitPrice;

        // Print bill
        System.out.println("\n--------- Final Bill ---------");
        System.out.println("Pizza       : " + pizza.name);
        System.out.println("Category    : " + pizza.category);
        System.out.println("Unit Price  : $" + DF.format(unitPrice));
        System.out.println("Quantity    : " + quantity);
        System.out.println("------------------------------");
        System.out.println("Total Price : $" + DF.format(totalPrice));
        System.out.println("------------------------------");
        System.out.println("Your order has been booked! Thank you so much, have a nice day!");

        // Persist order
        PizzaService.saveOrder(currentUserId, pizza.id, quantity, unitPrice, totalPrice);
        System.out.println("✅ Order saved to history.");
    }
}
