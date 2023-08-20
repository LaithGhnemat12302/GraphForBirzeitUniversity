package application;

import java.util.*;

public class Dijkstra {
	public Dijkstra() { // No argument constructor
	}

	// This method to get the shortest path(list of buildings) for a given source
	// and destination from a given graph
	public List<Building> getShortestPath(Building source, Building destination, Graph graph) {

		// Inner class to compare between two building's distances
		class BuildingComparator implements Comparator<Building> {
			public int compare(Building building1, Building building2) {
				// Compare buildings based on their distances in the graph
				return (int) (graph.distances.get(building1) - graph.distances.get(building2));
			}
		}

		// Heap to store buildings based on their distances
		PriorityQueue<Building> heap = new PriorityQueue<>(new BuildingComparator());

		heap.add(source); // Add source building to the queue
		graph.distances.put(source, 0.0); // Initial distance with zero

		// Map to store the previous building in the shortest path
		Map<Building, Building> previous = new HashMap<>();

		while (!heap.isEmpty()) {
			Building current = heap.poll(); // Remove and return the building with lowest distance from the heap

			if (current == destination)
				break;

			for (Edge edge : graph.graph.get(current)) { // Loop on the edges from the current building
				Building neighbor = edge.destination; // neighbor is an adjacent building for the current
														// building(edge's destination)

				double distance = edge.weight; // distance is the weight between current building and neighbor
												// building(edge's weight)

				// Update the distance of the neighbor building if a shorter path is found
				if (graph.distances.get(neighbor) > graph.distances.get(current) + distance) {
					graph.distances.put(neighbor, graph.distances.get(current) + distance); // Update the distance
					previous.put(neighbor, current); // Add to the HashMap previous
					heap.add(neighbor); // Add the neighbor building (has minimum distance) to the heap
				}
			}
		}

		if (previous.get(destination) == null) // If no path
			return null;

		List<Building> path = new ArrayList<>(); // path is a list that give the shortest path from destination to
													// source
		Building current = destination; // current starts from the destination building until the source

		while (current != source) {
			path.add(current); // Add current building to the path list
			current = previous.get(current); // Go to the previous building of current building by previous HashMap
		}

		path.add(source); // Add source building to the path list
		Collections.reverse(path); // Take path in reverse order (from source to destination)

		return path; // Return the path list
	}
}
