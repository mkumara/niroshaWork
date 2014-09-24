
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package catchmentAreas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import censusBlocks.Address;
import censusBlocks.CensusBlock;
import censusBlocks.Location;
import censusBlocks.Pod;
import censusBlocks.Vertex;

public class FindCatchmentAreas {

	public static void main(String args[]) {
		FindCatchmentAreas find = new FindCatchmentAreas();
		LinkedList<Pod> pods = find.readPods();
		CensusBlock[] censusBlocks = find.readCensusBlocks();
		find.readCentroids(censusBlocks);
		find.assignPods(pods, censusBlocks);
	}

	/**
	 * Reads in file of PODs
	 */
	private LinkedList<Pod> readPods() {
		LinkedList<Pod> pods = new LinkedList<Pod>();

		String path = "";
		try {
			Scanner input = new Scanner(new File(path + "pods.txt"));
			System.out.println("Rading file of format " + input.nextLine());
			while (input.hasNext()) {
				String line = input.nextLine();
				Scanner s = new Scanner(line);
				int id = s.nextInt();
				String site = this.readWholeString(s);
				String address = this.readWholeString(s);
				String city = this.readWholeString(s);
				int zip = s.nextInt();
				String isd = this.readWholeString(s);
				double longitude = s.nextDouble();
				double latitude = s.nextDouble();
				String quad = s.next();
				Address addy = new Address(address, city, zip);
				Location loc = new Location(longitude, latitude);
				Pod p = new Pod(id, site, addy, isd, loc, quad);
				pods.add(p);
			}
			System.out.println("---");
			while (input.hasNext()){
				int index = input.nextInt();
				double x = input.nextDouble();
				double y = input.nextDouble();
				Pod p = pods.get(index-1);
				p.setLocation(new Location(x,y));
				System.out.println(p.getLocation());
				
			}
		} catch (FileNotFoundException e) {
			System.err.println("File pods.txt not found");
			e.printStackTrace();
		}
		System.out.println("Done reading pods.txt");
		return pods;
	}

	private String readWholeString(Scanner s) {
		String x = s.next();
		while (x.charAt(x.length() - 1) != '\"') {
			x += " " + s.next();
		}
		return x.substring(1, x.length() - 1);
	}

	private void readCentroids(CensusBlock[] censusBlocks) {
		String path = "";
		System.out.println("Reading centroids...");
		int i = 0;
		try {
			Scanner input = new Scanner(new File(path + "centroids.txt"));
			input.nextInt();
			while (input.hasNext()) {
				int id = input.nextInt();
				double x = input.nextDouble();
				double y = input.nextDouble();
				if (censusBlocks[i].getID() == id) {
					censusBlocks[i].setCentroid(new Location(x, y));
				} else {
					System.out.println("ReadCentroids: Data problem");
				}
				i++;
			}
			System.out.println("Done reading centroids");
		} catch (FileNotFoundException e) {
			System.err.println("Error reading centroids");
			e.printStackTrace();
		}

	}

	private CensusBlock[] readCensusBlocks() {
		int numRecs; // number of records
		CensusBlock[] censusBlocks = null;
		try {
			String path = "";
			System.out.println("Reading census blocks...");
			Scanner input = new Scanner(new File(path + "censusBlocks.txt"));
			numRecs = input.nextInt();
			censusBlocks = new CensusBlock[numRecs];
			System.out.println("Reading in " + numRecs + " records...");

			for (int i = 0; i < numRecs; i++) {
				int id = input.nextInt();
				int numEdges = input.nextInt();
				Vertex[] v = new Vertex[numEdges];
				for (int j = 0; j < numEdges; j++) {
					double x = input.nextDouble();
					double y = input.nextDouble();
					v[j] = new Vertex(x, y);
				}
				censusBlocks[i] = new CensusBlock(id, v);
			}
			System.out.println("Done reading censusBlocks.txt\n");

		} catch (FileNotFoundException e) {
			System.err.println("Could not find census block file");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error writing to file");
			e.printStackTrace();
		}
		return censusBlocks;

	}

	private void assignPods(LinkedList<Pod> pods, CensusBlock[] censusBlocks) {

		String podpath = "catchmentAreas/pods/";
		System.out.println("\nCalculating assignments for " + pods.size()
				+ " PODs and " + censusBlocks.length + " census Blocks");

		try {
			BufferedWriter all = new BufferedWriter(new FileWriter(podpath + "catchmentAreas.txt"));
			BufferedWriter all0 = new BufferedWriter(new FileWriter(podpath + "catchmentAreas0.txt"));
			BufferedWriter[] files = new BufferedWriter[pods.size()];
			for (int i = 0; i < files.length; i++) {
				files[i] = new BufferedWriter(new FileWriter(podpath + "pod"
						+ (i + 1) + ".txt"));
			}

			for (int i = 0; i < censusBlocks.length; i++) {
				double lo1 = censusBlocks[i].getCentroid().longitude;
				double la1 = censusBlocks[i].getCentroid().latitude;
				Iterator it = pods.iterator();
				double mindist = Double.MAX_VALUE;
				int podId = -1;
				while (it.hasNext()) {
					Pod p = (Pod) it.next();
					double lo2 = p.loc.longitude;
					double la2 = p.loc.latitude;
					double d1 = la2 - la1;
					double d2 = lo2 - lo1;
					double dist = Math.sqrt(d1 * d1 + d2 * d2);
					if (dist < mindist) {
						mindist = dist;
						podId = p.id; 
					}
				}
				if(podId < 1) {
					System.err.println("Bad ID");
				}
				files[podId-1].write(censusBlocks[i].getID() + "\n");
				all.write(censusBlocks[i].getID() + "," + podId + "\n");
				all0.write(censusBlocks[i].getID()-1 + "," + podId + "\n");
				files[podId-1].flush();
				all.flush();
				all0.flush();
			}

			for (int i = 0; i < files.length; i++) {
				files[i].close();
			}
			System.out.println("Done calculating catchment areas");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
