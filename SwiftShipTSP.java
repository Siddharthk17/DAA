import java.util.*;
public class SwiftShipTSP {
    static class Node implements Comparable<Node> {
        int level;          // how many cities are visited
        int pathCost;       // current path cost
        int bound;          // lower bound on path cost
        List<Integer> path; // path so far
        Node(int level, int pathCost, int bound, List<Integer> path) {
            this.level = level;
            this.pathCost = pathCost;
            this.bound = bound;
            this.path = new ArrayList<>(path);
        }
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.bound, other.bound); // min-heap by bound
        }
    }
    static int calculateBound(Node node, int[][] costMatrix, int N) {
        int bound = node.pathCost;
        boolean[] visited = new boolean[N];
        for (int city : node.path) visited[city] = true;
        // add minimum outgoing edge cost for unvisited cities
        for (int i = 0; i < N; i++) {
            if (!visited[i]) {
                int minEdge = Integer.MAX_VALUE;
                for (int j = 0; j < N; j++) {
                    if (i != j && !visited[j]) {
                        minEdge = Math.min(minEdge, costMatrix[i][j]);
                    }
                }
                bound += (minEdge == Integer.MAX_VALUE) ? 0 : minEdge;
            }
        }
        return bound;
    }
    static List<Integer> tspBranchAndBound(int[][] costMatrix) {
        int N = costMatrix.length;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        List<Integer> bestPath = new ArrayList<>();
        int minCost = Integer.MAX_VALUE;
        Node root = new Node(0, 0, 0, new ArrayList<>(List.of(0)));
        root.bound = calculateBound(root, costMatrix, N);
        pq.add(root);
        while (!pq.isEmpty()) {
            Node node = pq.poll();
            if (node.bound >= minCost) continue; // prune
            if (node.level == N - 1) {
                // complete the tour
                int last = node.path.get(node.path.size() - 1);
                if (costMatrix[last][0] > 0) {
                    int totalCost = node.pathCost + costMatrix[last][0];
                    if (totalCost < minCost) {
                        minCost = totalCost;
                        bestPath = new ArrayList<>(node.path);
                        bestPath.add(0); // return to start
                    }
                }
                continue;
            }
            // expand the node
            int lastCity = node.path.get(node.path.size() - 1);
            for (int nextCity = 0; nextCity < N; nextCity++) {
                if (!node.path.contains(nextCity) && costMatrix[lastCity][nextCity] > 0) {
                    List<Integer> newPath = new ArrayList<>(node.path);
                    newPath.add(nextCity);
                    int newCost = node.pathCost + costMatrix[lastCity][nextCity];
                    Node child = new Node(node.level + 1, newCost, 0, newPath);
                    child.bound = calculateBound(child, costMatrix, N);
                    if (child.bound < minCost) pq.add(child); // only promising nodes
                }
            }
        }
        System.out.println("Minimum cost: " + minCost);
        return bestPath;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of cities: ");
        int N = sc.nextInt();
        int[][] costMatrix = new int[N][N];
        System.out.println("Enter cost matrix (NxN):");
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                costMatrix[i][j] = sc.nextInt();
        List<Integer> optimalRoute = tspBranchAndBound(costMatrix);
        System.out.println("Optimal route:");
        for (int city : optimalRoute) {
            System.out.print(city + " -> ");
        }
        System.out.println("End");
    }
}
