import java.util.ArrayList;
import java.util.Scanner;

class Item {
    int weight;
    int utility;
    
    public Item(int weight, int utility) {
        this.weight = weight;
        this.utility = utility;
    }
}

public class Knapsack {
    
    public static int knapsack(ArrayList<Item> items, int W, ArrayList<Integer> selectedItems) {
        int N = items.size();
        int[][] dp = new int[N + 1][W + 1];
        
        for (int i = 1; i <= N; i++) {
            for (int w = 0; w <= W; w++) {
                if (items.get(i-1).weight <= w) {
                    int includeItem = dp[i-1][w - items.get(i-1).weight] + items.get(i-1).utility;
                    if (includeItem > dp[i-1][w]) {
                        dp[i][w] = includeItem;
                    } else {
                        dp[i][w] = dp[i-1][w];
                    }
                } else {
                    dp[i][w] = dp[i-1][w];
                }
            }
        }
        
        int remainingWeight = W;
        for (int i = N; i > 0; i--) {
            if (dp[i][remainingWeight] != dp[i-1][remainingWeight]) {
                selectedItems.add(i-1);
                remainingWeight -= items.get(i-1).weight;
            }
        }
        return dp[N][W];
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the number of items : ");
        int N = scanner.nextInt();
        System.out.print("Enter the truck capacity : ");
        int W = scanner.nextInt();
        
        ArrayList<Item> items = new ArrayList<>();
        
        System.out.println("Enter the weight and utility of each item:");
        for (int i = 0; i < N; i++) {
            System.out.print("Item " + (i + 1) + " - Weight: ");
            int weight = scanner.nextInt();
            System.out.print("Item " + (i + 1) + " - Utility: ");
            int utility = scanner.nextInt();
            items.add(new Item(weight, utility));
        }
        
        ArrayList<Integer> selectedItems = new ArrayList<>();
        
        int maxUtility = knapsack(items, W, selectedItems);
        
        System.out.println("Maximum utility that can be carried: " + maxUtility);
        
        System.out.println("Items chosen :");
        for (int i : selectedItems) {
            System.out.println("Item " + (i + 1) + " - Weight: " + items.get(i).weight + " Utility: " + items.get(i).utility);
        }
        
        scanner.close();
    }
}
