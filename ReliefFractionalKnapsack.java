import java.util.*;

public class ReliefFractionalKnapsack {

    static class Item {
        String name;
        double weight;
        double value;
        boolean divisible; // true -> can take fraction

        Item(String name, double weight, double value, boolean divisible) {
            if (weight <= 0) throw new IllegalArgumentException("Weight must be > 0");
            this.name = name;
            this.weight = weight;
            this.value = value;
            this.divisible = divisible;
        }

        double density() {
            return value / weight;
        }
    }
    public static double fillBoat(List<Item> items, double capacityKg) {
        if (capacityKg <= 0) {
            System.out.println("Boat capacity should be > 0.");
            return 0.0;
        }
        // Sort by value-per-kg (density) descending
        items.sort((a, b) -> Double.compare(b.density(), a.density()));

        double remaining = capacityKg;
        double totalValue = 0.0;

        System.out.printf("Boat capacity: %.2f kg%n", capacityKg);
        System.out.println("Selected items (name : takenWeight kg -> gainedUtility)");

        for (Item it : items) {
            if (remaining <= 0) break;
            if (it.divisible) {
                // take fraction if needed
                double takeWeight = Math.min(it.weight, remaining);
                double gainedValue = it.density() * takeWeight;
                totalValue += gainedValue;
                remaining -= takeWeight;

                double percent = (takeWeight / it.weight) * 100.0;
                System.out.printf("%s : %.2f kg -> %.2f utility (%.1f%% of item)%n",
                        it.name, takeWeight, gainedValue, percent);
            } else {
                // indivisible: only take whole if it fits
                if (it.weight <= remaining) {
                    totalValue += it.value;
                    remaining -= it.weight;
                    System.out.printf("%s : %.2f kg -> %.2f utility (whole item)%n",
                            it.name, it.weight, it.value);
                } else {
                    System.out.printf("%s : SKIPPED (needs %.2f kg, only %.2f kg left)%n",
                            it.name, it.weight, remaining);
                }
            }
        }
        System.out.printf("Total utility value loaded: %.2f%n", totalValue);
        System.out.printf("Unused capacity remaining: %.2f kg%n", remaining);
        return totalValue;
    }

    // Simple user input demo
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter boat capacity W (kg): ");
            double W = sc.nextDouble();
            System.out.print("Enter number of different item types n: ");
            int n = sc.nextInt();
            sc.nextLine();

            List<Item> items = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                System.out.printf("Item %d name: ", i);
                String name = sc.nextLine().trim();
                System.out.printf("Item %d weight (kg): ", i);
                double w = sc.nextDouble();
                System.out.printf("Item %d utility value: ", i);
                double v = sc.nextDouble();
                sc.nextLine();
                System.out.printf("Is item %d divisible? (y/n): ", i);
                String d = sc.nextLine().trim().toLowerCase();
                boolean divisible = d.equals("y") || d.equals("yes");
                // Basic validation
                if (w <= 0) {
                    System.out.println("Weight must be > 0. Please re-enter this item.");
                    i--;
                    continue;
                }
                items.add(new Item(name, w, v, divisible));
            }
            System.out.println();
            fillBoat(items, W);
        } catch (InputMismatchException ime) {
            System.out.println("Invalid input. Please enter numbers where required.");
        } catch (IllegalArgumentException iae) {
            System.out.println("Error: " + iae.getMessage());
        } finally {
            sc.close();
        }
    }
}
