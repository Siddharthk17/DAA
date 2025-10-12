import java.util.*;

class DisasterRelief {
    static class Item {
        String name;
        int weight;
        int utility;
        boolean isCritical; // true for high-priority items (e.g., medicine, food)
        Item(String name, int weight, int utility, boolean isCritical) {
            this.name = name;
            this.weight = weight;
            this.utility = utility;
            this.isCritical = isCritical;
        }
    }
    static List<Item> knapsack(Item[] items, int W) {
        int n = items.length;
        int[][] dp = new int[n + 1][W + 1];

        // build DP table
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= W; w++) {
                if (items[i - 1].weight <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w],
                            dp[i - 1][w - items[i - 1].weight] + items[i - 1].utility);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // backtrack to find selected items
        List<Item> selected = new ArrayList<>();
        int w = W;
        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                selected.add(items[i - 1]);
                w -= items[i - 1].weight;
            }
        }
        Collections.reverse(selected); // optional: original order
        return selected;
    }

    static List<List<Item>> allocateMultipleTrucks(Item[] items, int[] truckCapacities) {
        List<List<Item>> allocations = new ArrayList<>();
        Set<Integer> usedItems = new HashSet<>(); // track items already loaded

        for (int W : truckCapacities) {
            // Filter out items already used
            List<Item> remaining = new ArrayList<>();
            for (int i = 0; i < items.length; i++) {
                if (!usedItems.contains(i)) remaining.add(items[i]);
            }
            Item[] remArray = remaining.toArray(new Item[0]);
            List<Item> selected = knapsack(remArray, W);
            // Mark items as used
            for (Item item : selected) {
                for (int i = 0; i < items.length; i++) {
                    if (!usedItems.contains(i) && items[i] == item) {
                        usedItems.add(i);
                        break;
                    }
                }
            }

            allocations.add(selected);
        }
        return allocations;
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter truck capacity W (kg): ");
        int W = sc.nextInt();
        System.out.print("Enter number of different items N: ");
        int N = sc.nextInt();
        sc.nextLine();
        Item[] items = new Item[N];
        for (int i = 0; i < N; i++) {
            System.out.print("Item " + (i + 1) + " name: ");
            String name = sc.nextLine();
            System.out.print("Item " + (i + 1) + " weight (kg): ");
            int weight = sc.nextInt();
            System.out.print("Item " + (i + 1) + " utility value: ");
            int utility = sc.nextInt();
            System.out.print("Is it critical? (y/n): ");
            boolean critical = sc.next().trim().equalsIgnoreCase("y");
            sc.nextLine();
            // For critical items, optionally increase utility to prioritize them
            if (critical) utility += 50; // simple priority
            items[i] = new Item(name, weight, utility, critical);
        }
        List<Item> selected = knapsack(items, W);
        double totalUtility = 0;
        int totalWeight = 0;
        System.out.println("\nSelected items for truck (max weight " + W + " kg):");
        for (Item it : selected) {
            System.out.println(it.name + " - weight: " + it.weight + " kg, utility: " + it.utility);
            totalUtility += it.utility;
            totalWeight += it.weight;
        }
        System.out.println("Total utility: " + totalUtility);
        System.out.println("Total weight: " + totalWeight + " kg");
    }
}
