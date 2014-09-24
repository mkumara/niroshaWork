
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package dataOperations;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import censusBlocks.Location;

import roadNetwork.Node;
import roadNetwork.Road;
import roadNetwork.RoadNetwork;

public class InputFileReader {

	/**
	 * input format: number of records for each record: id, number of nodes,
	 * list of nodes
	 * 
	 */
	public static RoadNetwork readRoads() {
		RoadNetwork network = new RoadNetwork();
		String path = "data/";
		String fileName = "Network2007_POD23_GCS_WGS84.out.txt";
		try {
			Scanner s = new Scanner(new FileReader(path + fileName));
			int records = s.nextInt();
			// loop through individual roads
			for (int i = 0; i < records; i++) {
				int id = s.nextInt();
				if ((i + 1) != id) {
					System.err.println("Data error");
					System.exit(0);
				}
				int nodes = s.nextInt();
				LinkedList<Node> road = new LinkedList<Node>();
				// loop through nodes of a single road
				for (int j = 0; j < nodes; j++) {
					double x = s.nextDouble();
					double y = s.nextDouble();
					Node node = new Node(x, y);
					road.add(node);
				}
				network.addRoad(new Road(road,id));
			}
		} catch (FileNotFoundException e) {
			System.err.println("Road file not found");
		}
		return network;
	}

	public static void readCatchmentArea() {
		String path = "data/";
		String fileName = "POD23CatchmentBlocks_GCS_WGS84.out.txt";
		try {
			Scanner s = new Scanner(new FileReader(path + fileName));
			int records = s.nextInt();
			for (int i = 0; i < records; i++) {
				int id = s.nextInt();
				if ((i + 1) != id) {
					System.err.println("Data error");
					System.exit(0);
				}
				int nodes = s.nextInt();
				for (int j = 0; j < nodes; j++) {
					double x = s.nextDouble();
					double y = s.nextDouble();
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Catchment area file not found");
		}
	}

	public static Location[] readCentroids(String path, String fileName) {
		Location[] centroids = null;
		try {
			Scanner s = new Scanner(new FileReader(path + fileName));
			int records = s.nextInt();
			centroids = new Location[records];
			for (int i = 0; i < records; i++) {
				int id = s.nextInt();
				if ((i + 1) != id) {
					System.err.println("Data error in centroids.txt");
					System.exit(0);
				} else {
					double x = s.nextDouble();
					double y = s.nextDouble();
					centroids[i] = new Location(x, y);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("centroids.txt not found");
		}

		return centroids;
	}

	public static int[] readPodCentroids(String path, String fileName) {
		int[] podids = null;
		try {
			Scanner s = new Scanner(new FileReader(path + fileName));
			int count = 0;
			while(s.hasNextInt()){
				s.nextInt();
				count++;
			}
			s = new Scanner(new FileReader(path + fileName));
			
			podids = new int[count];
			int i = 0;
			while(s.hasNextInt()){
				podids[i] = s.nextInt();
				i++;
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("Pod file not found");
		}
		return podids;
	}
	
	public static Road[] readCatchmentRoads(String path, String fileName, int podId) {
		
		Road[] roads = null;
		try {
			Scanner s = new Scanner(new FileReader(path + fileName));
			int records = s.nextInt();
			roads = new Road[records];
			// loop through individual roads
			for (int i = 0; i < records; i++) {
				int id = s.nextInt();
				int nodes = s.nextInt();
				LinkedList<Node> road = new LinkedList<Node>();
				// loop through nodes of a single road
				for (int j = 0; j < nodes; j++) {
					double x = s.nextDouble();
					double y = s.nextDouble();
					Node node = new Node(x, y);
					road.add(node);
				}
				roads[i] = new Road(road,id);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Road file not found");
		}
		return roads;
	} 
}
