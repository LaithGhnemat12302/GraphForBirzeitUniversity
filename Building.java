package application;

//This class is for each vertex in the university which contains the building name, x and y coordinates
public class Building {
	String index; // The name of the building
	double x;
	double y;
//__________________________________________________________________________________________________________________________________

	public Building(String index, double x, double y) { // Constructor with all parameters
		this.index = index;
		this.x = x;
		this.y = y;
	}
//__________________________________________________________________________________________________________________________________

	@Override
	public String toString() {
		return index;
	}
}
