package application;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HelloApplication extends Application {

	static List<Building> buildings = new ArrayList<>(); // buildings list has all vertices
	static List<Edge> edges = new ArrayList<>(); // edges list has all edges

	ComboBox<Building> comboBoxSource = new ComboBox<>();
	ComboBox<Building> comboBoxDestination = new ComboBox<>();

	AnchorPane root = new AnchorPane();

	Graph graph;

	@Override
	public void start(Stage primarytage) throws IOException {
		Font font = Font.font("Courier New", FontWeight.BOLD, 23);

		graph = graphReadFromFile(); // Invoke method
		Dijkstra dijkstra = new Dijkstra();
		// ___________________________________________________________________________________________________________________________

		Label labelSource = new Label("Source: ");
		labelSource.setFont(font);
		labelSource.setTextFill(Color.BLACK);

		Label labelDestination = new Label("Destination: ");
		labelDestination.setFont(font);
		labelDestination.setTextFill(Color.BLACK);

		comboBoxSource.setLayoutX(1250);
		comboBoxSource.setLayoutY(100);
		comboBoxDestination.setLayoutX(1250);
		comboBoxDestination.setLayoutY(150);
		comboBoxSource.setPrefHeight(40);
		comboBoxDestination.setPrefHeight(40);
		comboBoxDestination.setPrefWidth(200);

		HBox hBoxSource = new HBox();
		hBoxSource.getChildren().addAll(labelSource, comboBoxSource);
		hBoxSource.setSpacing(5);

		HBox hBoxDestination = new HBox();
		hBoxDestination.getChildren().addAll(labelDestination, comboBoxDestination);
		hBoxDestination.setSpacing(1);
		// __________________________________________________________________________________________________________________________________

		Button find_path = new Button("Find Path");
		find_path.setLayoutX(1250);
		find_path.setLayoutY(200);
		find_path.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-radius: 5;");
		find_path.setTextFill(Color.RED);
		find_path.setOnAction(e -> primarytage.close());
		find_path.setFont(font);

		Button reset = new Button("Reset");
		reset.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-radius: 5;");
		reset.setTextFill(Color.RED);
		reset.setFont(font);
		reset.setLayoutX(1250);
		reset.setLayoutY(250);
		reset.setDisable(true);

		Button close = new Button("Close");
		close.setLayoutX(1660);
		close.setLayoutY(50);
		close.setFont(font);
		close.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-radius: 5;");
		close.setTextFill(Color.RED);
		// __________________________________________________________________________________________________________________________________

		Label labelPath = new Label("Path: ");
		labelPath.setFont(font);
		labelPath.setTextFill(Color.BLACK);

		TextArea textArea = new TextArea();
		textArea.setPrefSize(300, 250);
		textArea.setEditable(false);
		textArea.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textArea.setStyle("-fx-text-fill: black;");

		HBox hBoxPath = new HBox();
		hBoxPath.getChildren().addAll(labelPath, textArea);
		hBoxPath.setSpacing(5);

		Label labelDistance = new Label("Distance: ");
		labelDistance.setFont(font);
		labelDistance.setTextFill(Color.BLACK);

		TextField textField = new TextField();
		textField.setPromptText("Total Distance");
		textField.setEditable(false);
		textField.setStyle("-fx-prompt-text-fill: black;");
		textField.setStyle("-fx-text-fill: black;");
		textField.setPrefWidth(255);
		textField.setPrefHeight(50);
		textField.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

		HBox hBoxDistance = new HBox();
		hBoxDistance.getChildren().addAll(labelDistance, textField);
		hBoxDistance.setSpacing(1);
		// __________________________________________________________________________________________________________________________________

		VBox controller = new VBox(); // The right side of the scene
		controller.setAlignment(Pos.CENTER);
		controller.setSpacing(30);
		controller.setLayoutX(1135);
		controller.setLayoutY(40);

		controller.getChildren().addAll(hBoxSource, hBoxDestination, find_path, reset, close, hBoxPath, hBoxDistance);

		Image image = new Image("map.PNG", 1000, 1000, false, false);
		ImageView imageView = new ImageView(image);
		imageView.setLayoutX(0);
		imageView.setLayoutY(0);

		root.getChildren().add(imageView);

		for (Building building : buildings) { // Add all buildings to ComboBoxes and circles to the root pane
			Circle circle = convert(building); // Invoke convert method to get a circle
			if (circle == null)
				continue;
			root.getChildren().add(circle); // Add circles to the root pane
			root.getChildren().add(generateLabel(building));// Invoke method and put the building's name in the root
			comboBoxSource.getItems().add(building); // Add all buildings to comboBoxSource
			comboBoxDestination.getItems().add(building); // Add all buildings to comboBoxDestination
		}

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(root);
		scrollPane.setPrefSize(1126, 820);

		BackgroundFill backgroundFill = new BackgroundFill(Color.BLUE, null, null);
		Background background = new Background(backgroundFill);

		AnchorPane ground = new AnchorPane();
		ground.getChildren().add(scrollPane);
		ground.getChildren().add(controller);
		ground.setBackground(background);
		// __________________________________________________________________________________________________________________________________

		find_path.setOnAction(e -> {
			reset.setDisable(false);	// Turn on

			if (comboBoxSource.getValue() != null && comboBoxDestination.getValue() != null) {// If have values
				List<Building> path = dijkstra.getShortestPath(comboBoxSource.getValue(),
						comboBoxDestination.getValue(), graph);

				if (path == null) {	// If no path
					Alert alert = new Alert(Alert.AlertType.ERROR);// ERROR is a type of alert
					alert.setTitle("Error");
					alert.setHeaderText("No Path");	// Bold font above(summary)
					alert.setContentText("There is no path between " + comboBoxSource.getValue() + " and "
							+ comboBoxDestination.getValue());	// Content text
					alert.showAndWait();

				} else {	// Print in textField and textArea
					for (int i = 0; i < path.size() - 1; i++) {	// Loop on each building in the path
						Building currentBuilding = path.get(i);	// current
						Building nextBuilding = path.get(i + 1);	// next
						
						Circle markerSource = convert(comboBoxSource.getValue());
						markerSource.setFill(Color.GREEN);
						
						Circle markerDestination = convert(comboBoxDestination.getValue());
						markerDestination.setFill(Color.GREEN);

						textArea.appendText("Step " + (i + 1) + " -> " + currentBuilding.index + " -> "
								+ nextBuilding.index + "\n");

						root.getChildren().add(drowLine(currentBuilding, nextBuilding));// Invoke method to print the line
					}

					travelSem(path);	// Invoke method to show and animate the man image
					textField.setText(graph.distances.get(comboBoxDestination.getValue()) + " m");
				}	// end else
			}// end if
		});
		// __________________________________________________________________________________________________________________________________

		reset.setOnAction(e -> {
			root.getChildren().clear();	// Clear the root pane
			ground.getChildren().clear(); // Clear the ground pane

			comboBoxSource.setValue(null);
			comboBoxDestination.setValue(null);

			reset.setDisable(true);	// Turn off
			find_path.setDisable(false);	// Turn on

			root.getChildren().add(imageView);	// Add map to the root pane
			for (Building Building : buildings) {// Add circles to the map
				Circle circle = convert(Building);// Invoke convert method to get a circle
				if (circle == null)
					continue;
				root.getChildren().add(circle);// Add circles to the map
				root.getChildren().add(generateLabel(Building));// Add labels to the map
			}
			controller.getChildren().clear();
			controller.getChildren().addAll(hBoxSource, hBoxDestination, find_path, reset, close, hBoxPath,
					hBoxDistance);

			textArea.setText("");
			textField.clear();

			ground.getChildren().add(controller);
			ground.getChildren().add(scrollPane);
			

			try {
				graph = graphReadFromFile();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

		});

		close.setOnAction(e -> primarytage.close());

		Scene scene = new Scene(ground, 1600, 820);
		primarytage.setScene(scene);
		primarytage.show();
		primarytage.setTitle("Graph Project For Birzeit University");
		primarytage.setFullScreen(true);
	}

	private Label generateLabel(Building building) { // Change the building name to a Label to put in the pane
		Circle circle = convert(building); // Invoke method which return a circle

		if (circle == null) // If no circle
			return null;

		Font fontBuildings = Font.font("Courier New", FontWeight.BOLD, 14);// The size of the label

		Label label = new Label(building.index); // Buildings name
		label.setLayoutX(circle.getLayoutX() + 10); // Get the x coordinate from the circle
		label.setLayoutY(circle.getLayoutY() - 10); // Get the y coordinate from the circle
		label.setFont(fontBuildings);
		label.setTextFill(Color.BLACK);
		return label;
	}

	private void travelSem(List<Building> path) {
		Image image = new Image("man.jpg", 50, 50, false, false);
		ImageView imageView = new ImageView(image);
		imageView.setLayoutX(convert(path.get(0)).getLayoutX() - 10);// Invoke a method and get the x coordinate from
																		// the circle
		imageView.setLayoutY(convert(path.get(0)).getLayoutY() - 10);// Invoke a method and get the y coordinate from
																		// the circle

		root.getChildren().add(imageView);

		Service<Void> service = new Service<Void>() { // Service class is a class in JavaFX
			@Override
			protected Task<Void> createTask() { // Method to apply the task of animation
				return new Task<Void>() { // Inner class
					@Override
					protected Void call() throws Exception { // Method for perform animation(here)
						for (int i = 1; i < path.size(); i++) { // Loop for all the buildings in the path list

							TranslateTransition tr2 = new TranslateTransition();// Animate the movement of the imageView
																				// based on the coordinates
							tr2.setDuration(Duration.seconds(3)); // Time of movement
							double x = convert(path.get(i)).getLayoutX() - convert(path.get(i - 1)).getLayoutX(); // Distance
																													// x
							double y = convert(path.get(i)).getLayoutY() - convert(path.get(i - 1)).getLayoutY(); // Distance
																													// y
							tr2.setByX(x);
							tr2.setByY(y);
							tr2.setNode(imageView);
							tr2.play();

							Thread.sleep(3000); // Pause on each building in the path for 6 seconds
						}
						return null;
					}
				};
			}
		};
		service.start();
	}

	private static Graph graphReadFromFile() throws IOException {// Read from the file
		BufferedReader reader = new BufferedReader(new FileReader("buildings.txt"));
		String line;

		boolean isBuildings = true; // If the line contains of building's name with it's x and y

		String firstLine = reader.readLine(); // firstLine is the numOfBuildings and numOfEdges

		while ((line = reader.readLine()) != null) {
			if (line.equals(firstLine))
				continue;

			// If the numOfBuildings has been finished
			if (line.equals("************************************************************")) {
				isBuildings = false;
				continue;
			}

			else if (isBuildings) {// If the line contains of building's name with it's x and y
				String[] parts = line.split(", ");
				String index = parts[0]; // index is the building's name

				double x = Double.parseDouble(parts[1]); // x coordinate
				double y = Double.parseDouble(parts[2]); // y coordinate

				Building building = new Building(index, x, y);
				buildings.add(building); // Add all buildings to the list
			}

			else {
				String[] parts = line.split(", ");
				String source = parts[0]; // sourceBuilding
				String destination = parts[1]; // destinationBuilding

				Building sourceBuilding = null;
				Building destinationBuilding = null;

				// Loop for all buildings list to find sourceBuilding and destinationBuilding
				for (Building building : buildings) {
					if (building.index.equals(source))
						sourceBuilding = building;
					else if (building.index.equals(destination))
						destinationBuilding = building;
				}

				double dis = Math.abs(Double.parseDouble(parts[2]));// Distance between those two buildings

				Edge edge = new Edge(sourceBuilding, destinationBuilding, dis);
				edges.add(edge); // Add all edges to the list
			} // end else
		} // end while

		reader.close(); // Close the BufferedReader

		Graph graph = new Graph();

		for (Building building : buildings) // Add all buildings to the graph
			graph.addBuilding(building);

		for (Edge edge : edges) // Add all edges to the graph
			graph.addEdge(edge.source, edge.destination, edge.weight);

		return graph; // Return graph with all buildings and edges
	}

	private Circle convert(Building building) { // Convert the building to a circle to add in the pane
		double xCoordinate = building.x;
		double yCoordinate = building.y;

		Circle marker = new Circle(8);
		marker.setLayoutX(xCoordinate);
		marker.setLayoutY(yCoordinate);
		marker.setFill(Color.YELLOW);

		marker.setOnMouseClicked(e -> {
			if (comboBoxSource.getValue() == null) {// Change the comboBoxSource value from the circle
				comboBoxSource.setValue(building);
				marker.setFill(Color.GREEN);
			}
			else { // Change the comboBoxDestination value from the circle
				comboBoxDestination.setValue(building);
				marker.setFill(Color.GREEN);
			}
		});

		return marker; // Return circle
	}

	private Line drowLine(Building source, Building destination) { // Draw line between two buildings
		Line line = new Line();
		// Get x and y from the circle coordinate
		line.setStartX(convert(source).getLayoutX());
		line.setStartY(convert(source).getLayoutY());
		line.setEndX(convert(destination).getLayoutX());
		line.setEndY(convert(destination).getLayoutY());
		line.setStroke(Color.BLACK);

		return line;
	}

	public static void main(String[] args) {
		launch();
	}
}