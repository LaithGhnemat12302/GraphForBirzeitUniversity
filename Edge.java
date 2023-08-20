package application;

//This class is for each edge in the university, this edge has the source building, destination  building and the weight between these two buildings 
public class Edge {
	Building source;
	Building destination;
	double weight;
//__________________________________________________________________________________________________________________________________

	public Edge(Building source, Building destination, double weight) { // Constructor with all parameters
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}
}
