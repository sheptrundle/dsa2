import java.io.InputStream;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.FlowAlgorithm;
import org.jgrapht.graph.*;

// Shep Trundle
// dvf5rd
// DSA2 - PA5

// Sources: https://jgrapht.org/guide/UserOverview for various basic info on JGraphT and its max flow algs

// Helper class for each building
class Building {
    private final String name;
    private final String location;
    private final HashSet<String> orders;

    // Constructor if we have all the data needed
    public Building(String name, String location, HashSet<String> orders) {
        this.name = name;
        this.location = location;
        this.orders = orders;
    }

    public String getName() {return name;}
    public String getLocation() {return location;}
    public HashSet<String> getOrders() {return orders;}

    public String toString() {
        return name + " in " + location + " needs " + orders;
    }
}

// Helper class for each contractor
class Contractor {
    private final String name;
    private final int limit;
    private final String job;
    private final HashSet<String> locations;

    // Constructor if we have all the data
    public Contractor(String name, int limit, String job, HashSet<String> locations) {
        this.name = name;
        this.limit = limit;
        this.job = job;
        this.locations = locations;
    }

    public String getName() {return name;}

    public int getLimit() {return limit;}
    public String getJob() {return job;}
    public HashSet<String> getLocations() {return locations;}

    public String toString() {
        return name + " does " + job + " up to " + limit + " in " + locations;
    }
}

// Helper class for work order (unique building and job)
class WorkOrder {
    private final Building building;
    private final String job;

    public WorkOrder(Building building, String job) {
        this.building = building;
        this.job = job;
    }

    public String getJob() {return job;}
    public String getLocation() {return building.getLocation();}
    public String name() {return building.getName() + " " + job;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkOrder workOrder = (WorkOrder) o;
        return building.getName().equals(workOrder.building.getName()) && job.equals(workOrder.job);
    }
}

public class PA5 {
    public static void main(String[] args) {
        // InputStream inputStream = PA5.class.getResourceAsStream("example15");
        Scanner scanner = new Scanner(System.in);

        // Read all buildings from input
        int b = Integer.parseInt(scanner.nextLine());
        List<Building> buildings = new ArrayList<>();

        for (int i = 0; i < b; i++) {
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");

            String name = tokens[0];
            String location = tokens[1];
            HashSet<String> orders = new HashSet<>(Arrays.asList(tokens).subList(2, tokens.length));

            buildings.add(new Building(name, location, orders));
        }

        // Read all contractors from input
        int c =  Integer.parseInt(scanner.nextLine());
        List<Contractor> contractors = new ArrayList<>();

        for (int i = 0; i < c; i++) {
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");

            String name = tokens[0];
            int limit = Integer.parseInt(tokens[2]);
            String job = tokens[3];
            HashSet<String> locations = new HashSet<>(Arrays.asList(tokens).subList(4, tokens.length));

            contractors.add(new Contractor(name, limit, job, locations));
        }
        scanner.close();

        /*
        // PRINTS OUT WHAT GOT READ FROM INPUT
        System.out.println("Buildings:");
        for (Building bld : buildings) {
            System.out.println(bld.toString());
        }
        System.out.println("\nContractors:");
        for (Contractor con : contractors) {
            System.out.println(con.toString());
        }
        */

        // Get all initial work orders by building
        List<WorkOrder> unfinishedOrders = new ArrayList<>();
        for (Building building : buildings) {
            for (String order : building.getOrders()) {
                unfinishedOrders.add(new WorkOrder(building, order));
            }
        }

        int days = 0;
        boolean possible = true;

        // Run max flow until all orders are complete, each maxflow run is one day
        while (!unfinishedOrders.isEmpty()) {
            /* Graph set up
            One source node to start MaxFlow on that just points to all possible work order.
            Work orders point to contractors that can complete that job (same location and job).
            Contractors point to end node which is their limit of how many jobs/day.
             */
            DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            graph.addVertex("start");
            graph.addVertex("end");

            // Create nodes for each building and its work order
            for (WorkOrder order : unfinishedOrders) {
                    graph.addVertex(order.name());

                    // Starting node (the run we run maxflow on) points to each work order
                    DefaultWeightedEdge edge = graph.addEdge("start", order.name());
                    graph.setEdgeWeight(edge, 1);
            }

            // Create nodes for each contractor and the work orders they can do
            for (Contractor contractor : contractors) {
                String vertex = contractor.getName();
                graph.addVertex(vertex);

                // Ending node gets pointed to by all contractors with their daily limit
                DefaultWeightedEdge edge = graph.addEdge(vertex, "end");
                graph.setEdgeWeight(edge, contractor.getLimit());
            }

            // Now have to bind each work order (and it's location) to a contractor that can do it
            for (WorkOrder order : unfinishedOrders) {
                String location = order.getLocation();
                String job = order.getJob();

                // Check all contractors to see if they can do it
                for (Contractor contractor : contractors) {
                    if (contractor.getJob().equals(job)
                            && contractor.getLocations().contains(location)) {
                        DefaultWeightedEdge edge = graph.addEdge(order.name(), contractor.getName());
                        graph.setEdgeWeight(edge, 1);
                    }
                }
            }

            // Each maxflow run does as much work as possible on one day
            PushRelabelMFImpl<String, DefaultWeightedEdge> maxFlow = new PushRelabelMFImpl<>(graph);

            maxFlow.calculateMaximumFlow("start", "end");
            FlowAlgorithm.Flow<DefaultWeightedEdge> flow = maxFlow.getFlow();

            List<WorkOrder> completedOrders = new ArrayList<>();

            // Go through the edges from start to each work order
            for (WorkOrder order : unfinishedOrders) {
                DefaultWeightedEdge edge = graph.getEdge("start", order.name());

                // Check if this work order got completed during current day
                if (flow.getFlow(edge) > 0.5) {
                    completedOrders.add(order);
                }
            }

            // Stop infinite loops, mark impossible for printing final result
            if (completedOrders.isEmpty()) {
                possible = false;
                break;
            }

            unfinishedOrders.removeAll(completedOrders);
            days++;
        }
        if (possible) {
            System.out.println(days + " days");
        } else {
            System.out.println("impossible");
        }
    }
}