import java.util.*;

class Node implements Comparable<Node> {
    int level;
    int pathCost;
    int reducedCost;
    int vertex;
    List<Integer> path;
    int[][] reducedMatrix;

    Node(int n) {
        reducedMatrix = new int[n][n];
        path = new ArrayList<>();
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.reducedCost, other.reducedCost);
    }
}

public class TSP {
    static final int INF = 9999999;

    // Copy matrix from source to destination
    static void copyMatrix(int[][] src, int[][] dest) {
        for (int i = 0; i < src.length; i++)
            System.arraycopy(src[i], 0, dest[i], 0, src.length);
    }

    // Reduce matrix by subtracting row and column minimums
    static int reduceMatrix(int[][] matrix, int n) {
        int reductionCost = 0;

        // Row reduction
        for (int i = 0; i < n; i++) {
            int rowMin = INF;
            for (int j = 0; j < n; j++)
                if (matrix[i][j] < rowMin)
                    rowMin = matrix[i][j];

            if (rowMin != INF && rowMin != 0) {
                reductionCost += rowMin;
                for (int j = 0; j < n; j++)
                    if (matrix[i][j] != INF)
                        matrix[i][j] -= rowMin;
            }
        }

        // Column reduction
        for (int j = 0; j < n; j++) {
            int colMin = INF;
            for (int i = 0; i < n; i++)
                if (matrix[i][j] < colMin)
                    colMin = matrix[i][j];

            if (colMin != INF && colMin != 0) {
                reductionCost += colMin;
                for (int i = 0; i < n; i++)
                    if (matrix[i][j] != INF)
                        matrix[i][j] -= colMin;
            }
        }
        return reductionCost;
    }

    // Create a new node in the state space tree
    static Node createNode(int[][] parentMatrix, List<Integer> path, int level, int i, int j, int n) {
        Node node = new Node(n);
        copyMatrix(parentMatrix, node.reducedMatrix);

        // Set outgoing edges from current city to INF
        for (int k = 0; level != 0 && k < n; k++)
            node.reducedMatrix[i][k] = INF;

        // Set incoming edges to destination city to INF
        for (int k = 0; k < n; k++)
            node.reducedMatrix[k][j] = INF;

        // Prevent returning to starting city before visiting all cities
        if (level + 1 < n)
            node.reducedMatrix[j][0] = INF;

        node.path.addAll(path);
        node.path.add(j);
        node.level = level;
        node.vertex = j;
        return node;
    }

    // Solve TSP using Branch and Bound
    static void solveTSP(int[][] costMatrix, int n) {
        PriorityQueue<Node> pq = new PriorityQueue<>();

        List<Integer> path = new ArrayList<>();
        path.add(0);

        Node root = new Node(n);
        copyMatrix(costMatrix, root.reducedMatrix);

        root.path = path;
        root.level = 0;
        root.vertex = 0;
        root.pathCost = 0;
        root.reducedCost = reduceMatrix(root.reducedMatrix, n);

        pq.add(root);
        int minCost = INF;
        List<Integer> finalPath = new ArrayList<>();

        while (!pq.isEmpty()) {
            Node min = pq.poll();
            int i = min.vertex;

            if (min.level == n - 1) {
                // Complete the tour by returning to starting city
                min.path.add(0);
                int totalCost = min.pathCost + costMatrix[i][0];
                if (totalCost < minCost) {
                    minCost = totalCost;
                    finalPath = new ArrayList<>(min.path);
                }
                continue;
            }

            // Explore all possible next cities
            for (int j = 0; j < n; j++) {
                if (min.reducedMatrix[i][j] != INF) {
                    Node child = createNode(min.reducedMatrix, min.path, min.level + 1, i, j, n);
                    child.pathCost = min.pathCost + costMatrix[i][j];
                    child.reducedCost = child.pathCost + reduceMatrix(child.reducedMatrix, n);
                    pq.add(child);
                }
            }
        }

        System.out.println("\nOptimal Delivery Route (SwiftShip): " + finalPath);
        System.out.println("Minimum Total Delivery Cost: " + minCost);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of cities: ");
        int n = sc.nextInt();

        int[][] costMatrix = new int[n][n];
        System.out.println("Enter cost matrix (use large number for no direct route):");
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                costMatrix[i][j] = sc.nextInt();

        solveTSP(costMatrix, n);
        sc.close();
    }
}
