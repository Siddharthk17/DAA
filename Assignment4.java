import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * SmartTrafficDijkstra.java
 *
 * Corrected example: updates edge (3,4) (which is on the initial shortest path)
 * to demonstrate a visible path change after a dynamic weight update.
 *
 * Includes debug prints showing edges from node 3 before/after update,
 * and confirms whether updateEdge succeeded.
 */
public class SmartTrafficDijkstra {

    // Edge structure
    static class Edge {
        int to;
        double weight;
        Edge(int to, double w) { this.to = to; this.weight = w; }
    }

    // Graph with directed edges (for undirected roads add both directions)
    static class Graph {
        final int n;
        final List<List<Edge>> adj;

        Graph(int n) {
            this.n = n;
            adj = new ArrayList<>(n);
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        }

        // add directed edge u -> v with weight w
        void addEdge(int u, int v, double w) {
            checkNode(u); checkNode(v);
            adj.get(u).add(new Edge(v, w));
        }

        // For undirected roads, convenience:
        void addUndirectedEdge(int u, int v, double w) {
            addEdge(u, v, w);
            addEdge(v, u, w);
        }

        // Update edge weight u->v if exists; returns true if updated
        boolean updateEdge(int u, int v, double newWeight) {
            checkNode(u); checkNode(v);
            boolean updated = false;
            for (Edge e : adj.get(u)) {
                if (e.to == v) {
                    e.weight = newWeight;
                    updated = true;
                    break; // update first matching parallel edge
                }
            }
            return updated;
        }

        void checkNode(int u) {
            if (u < 0 || u >= n) throw new IllegalArgumentException("Node id out of range: " + u);
        }
    }

    // Result container from Dijkstra
    static class DijkstraResult {
        double[] dist;        // distances from source
        int[] parent;         // parent pointers for path reconstruction (-1 for none)
        DijkstraResult(int n) {
            this.dist = new double[n];
            Arrays.fill(this.dist, Double.POSITIVE_INFINITY);
            this.parent = new int[n];
            Arrays.fill(this.parent, -1);
        }
    }

    // Run Dijkstra from source on graph, returns DijkstraResult
    public static DijkstraResult dijkstra(Graph g, int source) {
        int n = g.n;
        DijkstraResult res = new DijkstraResult(n);
        res.dist[source] = 0.0;

        class NodeDist implements Comparable<NodeDist> {
            final int node;
            final double dist;
            NodeDist(int node, double dist) { this.node = node; this.dist = dist; }
            @Override public int compareTo(NodeDist o) { return Double.compare(this.dist, o.dist); }
        }

        PriorityQueue<NodeDist> pq = new PriorityQueue<>();
        pq.add(new NodeDist(source, 0.0));

        boolean[] visited = new boolean[n];

        while (!pq.isEmpty()) {
            NodeDist nd = pq.poll();
            int u = nd.node;
            double du = nd.dist;

            if (du > res.dist[u]) continue;
            if (visited[u]) continue;
            visited[u] = true;

            for (Edge e : g.adj.get(u)) {
                int v = e.to;
                double w = e.weight;
                if (w < 0) throw new IllegalArgumentException("Negative edge weight detected. Dijkstra cannot handle negative weights.");
                double ndist = du + w;
                if (ndist + 1e-12 < res.dist[v]) {
                    res.dist[v] = ndist;
                    res.parent[v] = u;
                    pq.add(new NodeDist(v, ndist));
                }
            }
        }
        return res;
    }

    // Find nearest hospital from a set of hospital node IDs; returns the hospital node id, or -1 if none reachable
    public static int findNearestHospital(DijkstraResult res, Set<Integer> hospitals) {
        double best = Double.POSITIVE_INFINITY;
        int bestNode = -1;
        for (int h : hospitals) {
            if (h < 0 || h >= res.dist.length) continue;
            if (res.dist[h] < best) {
                best = res.dist[h];
                bestNode = h;
            }
        }
        return bestNode;
    }

    // Reconstruct path from source to target using parent pointers
    public static List<Integer> reconstructPath(DijkstraResult res, int source, int target) {
        List<Integer> path = new ArrayList<>();
        if (target < 0 || target >= res.dist.length) return path;
        if (Double.isInfinite(res.dist[target])) return path; // unreachable
        int cur = target;
        while (cur != -1 && cur != source) {
            path.add(cur);
            cur = res.parent[cur];
        }
        if (cur == source) {
            path.add(source);
            Collections.reverse(path);
        } else {
            path.clear(); // no valid path back to source
        }
        return path;
    }

    // Export Graphviz DOT file, highlighting given path (list of node ids).
    public static void exportDot(String filename, Graph g, List<Integer> path) throws IOException {
        Set<String> pathEdges = new HashSet<>();
        for (int i = 0; i + 1 < path.size(); i++) {
            pathEdges.add(path.get(i) + "->" + path.get(i + 1));
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("digraph G {");
            pw.println("  rankdir=LR;");
            pw.println("  node [shape=circle, style=filled, fillcolor=white];");

            // nodes
            for (int i = 0; i < g.n; i++) {
                pw.printf("  %d [label=\"%d\"];%n", i, i);
            }

            // edges with weights; highlight if part of path
            for (int u = 0; u < g.n; u++) {
                for (Edge e : g.adj.get(u)) {
                    String key = u + "->" + e.to;
                    if (pathEdges.contains(key)) {
                        pw.printf("  %d -> %d [label=\"%.2f\", color=red, penwidth=2.5];%n", u, e.to, e.weight);
                    } else {
                        pw.printf("  %d -> %d [label=\"%.2f\"];%n", u, e.to, e.weight);
                    }
                }
            }
            pw.println("}");
        }
    }

    // A very simple Swing graph visualizer: places nodes in circle and draws edges. Highlights path in red.
    public static void showSimpleVisualizer(Graph g, List<Integer> path, String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 800);

            JPanel panel = new JPanel() {
                @Override protected void paintComponent(Graphics gg) {
                    super.paintComponent(gg);
                    Graphics2D g2 = (Graphics2D) gg;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    int cx = w / 2, cy = h / 2;
                    int r = Math.min(w, h) / 3;
                    int n = g.n;
                    Point[] pts = new Point[n];
                    for (int i = 0; i < n; i++) {
                        double ang = 2 * Math.PI * i / Math.max(1, n);
                        int x = cx + (int) (r * Math.cos(ang));
                        int y = cy + (int) (r * Math.sin(ang));
                        pts[i] = new Point(x, y);
                    }

                    Set<String> pathEdges = new HashSet<>();
                    for (int i = 0; i + 1 < path.size(); i++) pathEdges.add(path.get(i) + "->" + path.get(i + 1));

                    // draw edges
                    g2.setStroke(new BasicStroke(1.0f));
                    for (int u = 0; u < n; u++) {
                        for (Edge e : g.adj.get(u)) {
                            int v = e.to;
                            boolean isPath = pathEdges.contains(u + "->" + v);
                            if (isPath) {
                                g2.setStroke(new BasicStroke(3.0f));
                                g2.setColor(Color.RED);
                            } else {
                                g2.setStroke(new BasicStroke(1.0f));
                                g2.setColor(Color.LIGHT_GRAY);
                            }
                            Line2D line = new Line2D.Float(pts[u], pts[v]);
                            g2.draw(line);

                            int mx = (pts[u].x + pts[v].x) / 2;
                            int my = (pts[u].y + pts[v].y) / 2;
                            g2.setColor(Color.BLACK);
                            g2.setFont(g2.getFont().deriveFont(10f));
                            g2.drawString(String.format("%.1f", e.weight), mx + 4, my - 4);
                        }
                    }

                    // draw nodes
                    for (int i = 0; i < n; i++) {
                        int x = pts[i].x, y = pts[i].y;
                        int nodeR = 18;
                        if (path.contains(i)) {
                            g2.setColor(Color.ORANGE);
                            g2.fillOval(x - nodeR, y - nodeR, nodeR * 2, nodeR * 2);
                        }
                        g2.setColor(Color.BLACK);
                        g2.drawOval(x - nodeR, y - nodeR, nodeR * 2, nodeR * 2);
                        g2.drawString(String.valueOf(i), x - 4, y + 4);
                    }
                }
            };

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    // Utility: print outgoing edges from a node (debug)
    private static void printOutgoingEdges(Graph g, int node) {
        System.out.printf("Outgoing edges from node %d:%n", node);
        for (Edge e : g.adj.get(node)) {
            System.out.printf("  -> %d (weight=%.2f)%n", e.to, e.weight);
        }
    }

    public static void main(String[] args) throws Exception {
        Graph g = new Graph(9);

        // Add undirected edges (roads). weight = travel time in minutes
        g.addUndirectedEdge(0, 1, 4.0);
        g.addUndirectedEdge(0, 2, 2.0);
        g.addUndirectedEdge(1, 2, 1.0);
        g.addUndirectedEdge(1, 3, 5.0);
        g.addUndirectedEdge(2, 3, 8.0);
        g.addUndirectedEdge(2, 4, 10.0);
        g.addUndirectedEdge(3, 4, 2.0);
        g.addUndirectedEdge(3, 5, 6.0);
        g.addUndirectedEdge(4, 6, 3.0);
        g.addUndirectedEdge(5, 6, 1.0);
        g.addUndirectedEdge(5, 7, 2.0);
        g.addUndirectedEdge(6, 8, 7.0);
        g.addUndirectedEdge(7, 8, 3.0);

        // Suppose ambulance is at node S = 0, hospitals are nodes {6, 8}
        int S = 0;
        Set<Integer> hospitals = new HashSet<>(Arrays.asList(6, 8));

        // 1) initial shortest paths
        DijkstraResult res = dijkstra(g, S);
        int nearestHospital = findNearestHospital(res, hospitals);
        System.out.println("Nearest hospital (initial): " + nearestHospital + " dist=" + res.dist[nearestHospital]);
        List<Integer> path = reconstructPath(res, S, nearestHospital);
        System.out.println("Path: " + path);
        exportDot("smart_traffic_initial.dot", g, path);
        System.out.println("Wrote smart_traffic_initial.dot (open with Graphviz or online DOT viewer).");
        showSimpleVisualizer(g, path, "Initial shortest path from " + S + " to " + nearestHospital);

        // DEBUG: print outgoing edges from node 3 before update
        System.out.println();
        printOutgoingEdges(g, 3);

        // 2) dynamic update: heavy congestion appears on edge 3-4 (increase travel time)
        System.out.println("\nTraffic update: edge (3,4) increased to 40.0 minutes ");
        boolean updated1 = g.updateEdge(3, 4, 40.0);
        boolean updated2 = g.updateEdge(4, 3, 40.0); // update reverse for undirected
        System.out.println("updateEdge(3,4) success: " + updated1 + ", updateEdge(4,3) success: " + updated2);

        // DEBUG: print outgoing edges from node 3 after update
        printOutgoingEdges(g, 3);

        // Recompute shortest paths after update
        DijkstraResult res2 = dijkstra(g, S);
        int nearestHospital2 = findNearestHospital(res2, hospitals);
        System.out.println("Nearest hospital (after update): " + nearestHospital2 + " dist=" + res2.dist[nearestHospital2]);
        List<Integer> path2 = reconstructPath(res2, S, nearestHospital2);
        System.out.println("Path after update: " + path2);
        exportDot("smart_traffic_after_update.dot", g, path2);
        showSimpleVisualizer(g, path2, "After congestion update: path to " + nearestHospital2);

        // 3) dynamic improvement: traffic clears on (3,4) restore to original weight 2.0
        System.out.println("\nTraffic update: edge (3,4) back to 2.0 minutes ");
        g.updateEdge(3, 4, 2.0);
        g.updateEdge(4, 3, 2.0);
        DijkstraResult res3 = dijkstra(g, S);
        int nearestHospital3 = findNearestHospital(res3, hospitals);
        System.out.println("Nearest hospital (after clearing): " + nearestHospital3 + " dist=" + res3.dist[nearestHospital3]);
        List<Integer> path3 = reconstructPath(res3, S, nearestHospital3);
        System.out.println("Path after clearing: " + path3);
        exportDot("smart_traffic_after_clear.dot", g, path3);
        showSimpleVisualizer(g, path3, "After clearing congestion: path to " + nearestHospital3);
    }
}

// OUTPUT
// Nearest hospital (initial): 6 dist=13.0
// Path: [0, 2, 1, 3, 4, 6]
// Wrote smart_traffic_initial.dot (open with Graphviz or online DOT viewer).

// Outgoing edges from node 3:
//   -> 1 (weight=5.00)
//   -> 2 (weight=8.00)
//   -> 4 (weight=2.00)
//   -> 5 (weight=6.00)

// Traffic update: edge (3,4) increased to 40.0 minutes 
// updateEdge(3,4) success: true, updateEdge(4,3) success: true
// Outgoing edges from node 3:
//   -> 1 (weight=5.00)
//   -> 2 (weight=8.00)
//   -> 4 (weight=40.00)
//   -> 5 (weight=6.00)
// Nearest hospital (after update): 6 dist=15.0
// Path after update: [0, 2, 4, 6]

// Traffic update: edge (3,4) back to 2.0 minutes 
// Nearest hospital (after clearing): 6 dist=13.0
// Path after clearing: [0, 2, 1, 3, 4, 6]
