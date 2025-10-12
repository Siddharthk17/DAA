import java.util.*;
public class UniversityTimetable {
    static class Graph {
        int V; //no.of courses
        List<Integer>[] adj; // adjacency list
        @SuppressWarnings("unchecked")
        Graph(int V) {
            this.V = V;
            adj = new ArrayList[V];
            for (int i = 0; i < V; i++) adj[i] = new ArrayList<>();
        }
        void addEdge(int u, int v) {
            adj[u].add(v);
            adj[v].add(u); // undirected graph
        }
        int[] greedyColoring(int[] roomCapacities, int[] courseSizes) {
            int[] result = new int[V]; // color assigned to each vertex
            Arrays.fill(result, -1);
            boolean[] available = new boolean[V]; // available colors
            Arrays.fill(available, true);
            // Assign color to first vertex
            result[0] = 0;
            // Assign colors to remaining vertices
            for (int u = 1; u < V; u++) {
                Arrays.fill(available, true);
                // mark unavailable colors for adjacent vertices
                for (int v : adj[u]) {
                    if (result[v] != -1) {
                        available[result[v]] = false;
                    }
                }
                // assign first available color
                int c;
                for (c = 0; c < V; c++) {
                    if (available[c]) break;
                }
                result[u] = c;
            }
            // Check room capacity constraints-optional
            if (roomCapacities != null && courseSizes != null) {
                Map<Integer, Integer> slotLoad = new HashMap<>();
                for (int i = 0; i < V; i++) {
                    slotLoad.put(result[i], slotLoad.getOrDefault(result[i], 0) + courseSizes[i]);
                }
                for (int slot : slotLoad.keySet()) {
                    if (slotLoad.get(slot) > roomCapacities[slot]) {
                        System.out.println("Warning: Slot " + slot + " exceeds room capacity.");
                    }
                }
            }
            return result;
        }
        void printTimetable(int[] colors) {
            System.out.println("Course : Exam Slot");
            for (int i = 0; i < V; i++) {
                System.out.println("Course " + i + " -> Slot " + colors[i]);
            }
            int maxSlot = Arrays.stream(colors).max().orElse(0) + 1;
            System.out.println("Total slots used: " + maxSlot);
        }
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of courses: ");
        int n = sc.nextInt();
        Graph g = new Graph(n);
        System.out.print("Enter number of conflicts: ");
        int m = sc.nextInt();
        System.out.println("Enter conflict pairs (course1 course2):");
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            g.addEdge(u, v);
        }
        System.out.print("Do you want to input room capacities? (y/n): ");
        sc.nextLine();
        String ans = sc.nextLine();
        int[] roomCapacities = null;
        int[] courseSizes = null;
        if (ans.equalsIgnoreCase("y")) {
            roomCapacities = new int[n];
            courseSizes = new int[n];
            System.out.println("Enter course sizes (number of students):");
            for (int i = 0; i < n; i++) courseSizes[i] = sc.nextInt();
            System.out.println("Enter room capacities per slot (assume max slots = number of courses):");
            for (int i = 0; i < n; i++) roomCapacities[i] = sc.nextInt();
        }
        int[] colors = g.greedyColoring(roomCapacities, courseSizes);
        g.printTimetable(colors);
    }
}
