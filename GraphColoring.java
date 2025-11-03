import java.util.*;

public class GraphColoring {

    static void addEdge(List<List<Integer>> graph, int u, int v) {
        graph.get(u).add(v);
        graph.get(v).add(u);
    }

    static void greedyColoring(List<List<Integer>> graph, int numCourses) {
        int[] result = new int[numCourses];
        Arrays.fill(result, -1);

        // Assign first color to first vertex
        result[0] = 0;

        // Track which colors are available for current vertex
        boolean[] available = new boolean[numCourses];

        for (int u = 1; u < numCourses; u++) {
            // Initially all colors are available
            Arrays.fill(available, true);

            // Mark colors of adjacent vertices as unavailable
            for (int adj : graph.get(u)) {
                if (result[adj] != -1)
                    available[result[adj]] = false;
            }

            // Find first available color
            int color;
            for (color = 0; color < numCourses; color++) {
                if (available[color]) break;
            }
            result[u] = color;
        }

        // Display results
        System.out.println("Exam Slot Assignment (Greedy Coloring):");
        for (int u = 0; u < numCourses; u++) {
            System.out.println("Course " + u + " → Slot " + result[u]);
        }

        int maxColor = Arrays.stream(result).max().getAsInt();
        System.out.println("\nTotal Exam Slots Used: " + (maxColor + 1));
    }

    public static void main(String[] args) {
        int numCourses = 6;
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++)
            graph.add(new ArrayList<>());

        // Build conflict graph
        addEdge(graph, 0, 1);
        addEdge(graph, 1, 2);
        addEdge(graph, 2, 3);
        addEdge(graph, 3, 4);
        addEdge(graph, 3, 5);
        addEdge(graph, 0, 5);

        greedyColoring(graph, numCourses);
    }
}
