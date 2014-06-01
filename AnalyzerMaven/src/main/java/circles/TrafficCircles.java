package circles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import roadNetwork.Node;
import roadNetwork.Road;

import censusBlocks.Location;
import data.CircleRecord;
import dataOperations.InputFileReader;

public class TrafficCircles {

	private Location podLocation;

	private int numberOfCircles;

	private int[] podids;

	private Location[] centroids;

	private double maxDist;

	public TrafficCircles(Location podLocation) {
		this.podLocation = podLocation;
	}

	public void setNumberOfCircles(int numberOfCirlces) {
		this.numberOfCircles = numberOfCircles;
	}

	public LinkedList<Location>[] findCirlceIntersectingRoads(
			int numberOfCircles) {
		// go through all road segments
		// check distances of end points (in and out of circle)
		LinkedList<Location>[] intersections = new LinkedList[numberOfCircles];
		for (int i = 0; i < intersections.length; i++) {
			intersections[i] = new LinkedList<Location>();
		}
		String path = "";
		String fileName = "Network2007b_POD23_GCS_WGS84.txt";
		int podId = 23;
		Road[] roads = InputFileReader
				.readCatchmentRoads(path, fileName, podId);
		for (int i = 0; i < roads.length; i++) {
			// System.out.println(i);
			LinkedList<Node> nodes = roads[i].getNodes();
			Iterator<Node> it = nodes.iterator();
			Node n1 = null, n2 = null;
			if (it.hasNext()) {
				n1 = it.next();
			}
			while (it.hasNext()) {
				n2 = it.next();
				Location l1 = new Location(n1.getX(), n2.getY());
				Location l2 = new Location(n2.getX(), n2.getY());
				double d1 = this.getDistance(podLocation, l1);
				double d2 = this.getDistance(podLocation, l2);
				for (int j = numberOfCircles - 1; j >= 1; j--) {
					double distance = (j * maxDist) / numberOfCircles;
					if ((d1 <= distance && d2 >= distance)
							|| (d1 >= distance && d2 <= distance)) {
						Location intersection = null;
						if (d1 <= d2) {
							intersection = new Location(n1.getX(), n1.getY());
						} else {
							intersection = new Location(n2.getX(), n2.getY());
						}
						intersections[j].add(intersection);
					}
				}
				n1 = n2;
			}
		}
		return intersections;
	}

	/**
	 * In respect to the crosspoint of an inner circle, define subdivisions of a
	 * circular slice
	 */
	public void defineSubdivisions(int numberOfCircles,
			LinkedList<Integer>[] circles) {
		LinkedList<CircleRecord> records = new LinkedList<CircleRecord>();
		LinkedList<Location>[] intersections = findCirlceIntersectingRoads(numberOfCircles);
		for (int i = 0; i < intersections.length; i++) {
			LinkedList<Location> circleIntersections = intersections[i];
			LinkedList<Integer> blockIds = circles[i];
			Iterator<Integer> it = blockIds.iterator();
			while (it.hasNext()) {
				Integer blockId = it.next();
				CircleRecord record = new CircleRecord();
				record.setCircleId(i + 1);
				record.setBlockId(blockId);
				if (i == 0) {
					record.setSubdivisonId(0);
				} else {
					double minDist = Double.MAX_VALUE;
					int subdiv = -1;
					Iterator<Location> crossing = circleIntersections
							.iterator();
					int currDiv = 0;
					while (crossing.hasNext()) {
						Location cross = crossing.next();
						Location blockLocation = new Location(
								centroids[blockId - 1].longitude,
								centroids[blockId - 1].latitude);
						double dist = this.getDistance(cross, blockLocation);
						if (dist < minDist) {
							minDist = dist;
							subdiv = currDiv;
						}
						currDiv++;
					}
					record.setSubdivisonId(subdiv);
				}
				records.add(record);
			}
		}
		this.writeSudbdivisions(records, 23, numberOfCircles);
	}

	public LinkedList<Integer>[] defineCircles(int numberOfCircles) {
		maxDist = findFurthestCentroid(23);
		LinkedList<Integer>[] circles = new LinkedList[numberOfCircles];
		double d = maxDist / numberOfCircles;
		// find census blocks for each circle
		for (int i = 0; i < numberOfCircles; i++) {
			double innerDistance = i * d;
			double outerDistance = (i + 1) * d;
			LinkedList circleBlocks = new LinkedList<Integer>();
			for (int j = 0; j < podids.length; j++) {
				double distance = Math.sqrt(Math.pow(podLocation.longitude
						- centroids[podids[j] - 1].longitude, 2)
						+ Math.pow(podLocation.latitude
								- centroids[podids[j] - 1].latitude, 2));
				if (distance <= outerDistance && distance > innerDistance) {
					circleBlocks.add(new Integer(podids[j]));
				}
			}
			circles[i] = circleBlocks;
		}
		this.writeCircles(circles, 23, numberOfCircles);
		return circles;
	}

	void writeCircles(LinkedList<Integer>[] circles, int area, int refinement) {
		String circlePath = "catchmentAreas/circles/";
		String fileName = "circles_pod" + area + "_ref" + refinement + ".txt";
		try {
			BufferedWriter c = new BufferedWriter(new FileWriter(circlePath
					+ fileName));
			for (int i = 0; i < circles.length; i++) {
				LinkedList<Integer> circleBlocks = circles[i];
				Iterator it = circleBlocks.iterator();
				while (it.hasNext()) {
					c.write((i + 1) + "\t" + it.next() + "\n");
					c.flush();
				}
			}
			c.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void writeSudbdivisions(LinkedList<CircleRecord> records, int area,
			int refinement) {
		String circlePath = "catchmentAreas/circles/";
		String fileName = "circles_div_pod" + area + "_ref" + refinement
				+ ".txt";
		try {
			BufferedWriter c = new BufferedWriter(new FileWriter(circlePath
					+ fileName));
			Iterator<CircleRecord> it = records.iterator();
			while (it.hasNext()) {
				CircleRecord r = it.next();
				c.write(r.getBlockId() + "\t" + r.getCircleId() + "\t"
						+ r.getSubdivisionId() + "\n");
			}
			c.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private double findFurthestCentroid(int catchmentNumber) {
		Location furthest = this.podLocation;
		String path = "catchmentAreas/pods/";
		String fileName = "pod" + catchmentNumber + ".txt";
		double maxDist = 0;
		centroids = InputFileReader.readCentroids("", "centroids.txt");
		podids = InputFileReader.readPodCentroids(path, fileName);
		for (int i = 0; i < podids.length; i++) {
			double distance = Math.sqrt(Math.pow(podLocation.longitude
					- centroids[podids[i] - 1].longitude, 2)
					+ Math.pow(podLocation.latitude
							- centroids[podids[i] - 1].latitude, 2));
			if (distance > maxDist) {
				furthest = centroids[podids[i] - 1];
				maxDist = distance;
			}

		}
		System.out.println(maxDist);

		return maxDist;
	}

	private double getDistance(Location a, Location b) {
		return Math.sqrt(Math.pow(a.longitude - b.longitude, 2)
				+ Math.pow(a.latitude - b.latitude, 2));
	}
}
