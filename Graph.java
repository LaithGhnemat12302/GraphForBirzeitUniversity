package application;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph {

	Map<Building, List<Edge>> graph; // Map to store the graph representation with building and list of edges
	Map<Building, Double> distances; // Map to store the weight(distance) from a source building to each building
	// __________________________________________________________________________________________________________________________________

	public Graph() { // Initialize the graph and distances maps
		graph = new HashMap<>();
		distances = new HashMap<>();
	}
	// __________________________________________________________________________________________________________________________________

	public void addBuilding(Building building) {
		graph.put(building, new LinkedList<>()); // Add a building to the graph with an empty list of edges
		distances.put(building, Double.POSITIVE_INFINITY); // Initial distance
	}
	// __________________________________________________________________________________________________________________________________

	public void addEdge(Building source, Building destination, double weight) {
		graph.get(source).add(new Edge(source, destination, weight)); // Add the new edge to the list of edges for the
																		// source building
	}
}
