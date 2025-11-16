// PA1 Skeleton Code
// DSA2, fall 2025
// Shep Trundle
// dvf5rd

// This code will read in the input, and put the values into lists.  It is up
// to you to properly represent this as a graph -- this code only reads in the
// input properly.


import java.util.*;

class Pair {
    public String s, t;
    Pair(String s, String t) { this.s=s; this.t=t; }
    public String toString() { return s + "->" + t; }

    // Needed to add this for when comparing pairs later in treeEdges and backEdges
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair p)) return false;
        return s.equals(p.s) && t.equals(p.t);
    }
    @Override
    public int hashCode() {
        return Objects.hash(s, t);
    }
}

class Node {
    // For coloring nodes
    public enum Color {
        WHITE, GRAY, BLACK
    }

    public String name;
    private int d;
    private int f;
    private Color color;
    private long pathogenLevel;

    Node(String name) {
        this.name = name;
        this.d = 0;
        this.f = 0;
        this.color = Color.WHITE;
        this.pathogenLevel = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node otherNode)) return false;
        return name.equals(otherNode.name);
    }

    // Learned this from ChatGPT. Prompt = "How can I search a map in Java by using an object's field instead of the object itself
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() { return name; }
    public int getD() { return d; }
    public void setD(int d) { this.d = d; }
    public int getF() { return f; }
    public void setF(int f) { this.f = f; }
    public boolean isColor(Color color) { return this.color == color; }
    public void setColor(Color color) { this.color = color; }
    public long getPathogenLevel() { return pathogenLevel; }
    public void addPathogenAmount(int pathogenAmount) {this.pathogenLevel += pathogenAmount; }

}


@SuppressWarnings({"JavadocBlankLines", "DanglingJavadoc"})
public class PA1 {
    // Time variable during DFS
    private static int time = 0;
    // Array for keeping track of tree edges
    private static Set<Pair> treeEdges = new HashSet<>();
    private static Set<Pair> backEdges = new HashSet<>();

    public static void main(String[] args) {

        Scanner stdin = new Scanner(System.in);
        int test_cases = stdin.nextInt();

        for ( int i = 0; i < test_cases; i++ ) {
            // read in the weights for the different pathogen loads
            int wt = stdin.nextInt(), wf = stdin.nextInt(), wb = stdin.nextInt(), wc = stdin.nextInt();
            // read in the number of vertices and edges
            long v = stdin.nextInt(), e = stdin.nextInt();
            // read in the edges
            ArrayList<Pair> edges = new ArrayList<>();
            for ( int j = 0; j < e; j++ )
                edges.add(new Pair(stdin.next(), stdin.next()));
            // read in the start outputNode and outputNode to print the pathogen load for
            String source = stdin.next(), outputNode = stdin.next();

            /** At this point, the data structures are as follows:
             *
             * The integers wt, wf, wb, and wc are the weights of the edges
             * for tree, forward, back, and cross edges
             *
             * The number of vertices and edges are in the integers v and e
             *
             * The edges themselves are in an ArrayList of Pairs; printing it
             * out might look like:
             * [A->B, B->C, C->D, D->E, A->C, A->F, E->C, E->D, F->C, F->D]
             *
             * The start outputNode is in the String source, the outputNode to print the
             * pathogen load is in the String outputNode
             */

            // REMOVE THESE LINES from your final version -- this is just to
            // show the data read in
            /*
            System.out.println("\ntest case " + i + ":");
            System.out.println("weights (tree, forward, back, and cross): " + wt+" "+wf+" "+wb+" "+wc);
            System.out.println(v + " vertices and " + e + " edges");
            System.out.println("the edges themselves: " + edges);
            System.out.println("start outputNode: " + source + ", outputNode to print the pathogen load for: " + outputNode);
             */

            // Create map of all nodes by name
            Map<String, Node> nodes = new HashMap<>();
            for (Pair edge : edges) {
                nodes.putIfAbsent(edge.s, new Node(edge.s));
                nodes.putIfAbsent(edge.t, new Node(edge.t));
            }
            // Create and populate adjacency list with nodes
            Map<Node, List<Node>> adjacencyList = new HashMap<>();
            for (Pair edge : edges) {
                Node first = nodes.get(edge.s);
                Node second = nodes.get(edge.t);

                adjacencyList.putIfAbsent(first, new ArrayList<>());
                adjacencyList.get(first).add(second);
            }

            // Sort each list to ensure neighbor visits are done so in alphabetical order
            for (List<Node> neighbors : adjacencyList.values()) {
                neighbors.sort(Comparator.comparing(Node::getName));
            }

            // Begin DFS
            treeEdges.clear();
            backEdges.clear();
            time = 0;
            dfsVisit(adjacencyList, nodes.get(source));

            // Calculate pathogen level for each node
            for (Pair edge : edges) {
                Node first = nodes.get(edge.s);
                Node second = nodes.get(edge.t);

                // Not found edges
                if (first.getD() == 0 || first.getF() == 0
                        || second.getD() == 0 || second.getF() == 0) {
                    continue;
                }

                Pair pair = new Pair(edge.s, edge.t);
                // Tree edge
                if (treeEdges.contains(pair)) {
                    second.addPathogenAmount(wt);
                }
                // Back edge
                else if (backEdges.contains(pair)) {
                    second.addPathogenAmount(wb);
                }
                // Cross edge
                else if (second.getF() < first.getD()) {
                    second.addPathogenAmount(wc);
                }
                // Forward edge
                else {
                    second.addPathogenAmount(wf);
                }
            }

            // Final output for desired node
            System.out.println(nodes.get(outputNode).getPathogenLevel());
        }

        // YOUR CODE HERE (or called from here)

    }

    private static void dfsVisit(Map<Node, List<Node>> graph, Node vertex) {
        time++;
        vertex.setD(time);
        vertex.setColor(Node.Color.GRAY);
        for (Node neighbor : graph.get(vertex)) {
            if (neighbor.isColor(Node.Color.WHITE)) {
                treeEdges.add(new Pair(vertex.getName(), neighbor.getName()));
                dfsVisit(graph, neighbor);
            } else if (neighbor.isColor(Node.Color.GRAY)) {
                backEdges.add(new Pair(vertex.getName(), neighbor.getName()));
            }
        }
        vertex.setColor(Node.Color.BLACK);
        time++;
        vertex.setF(time);
    }
}