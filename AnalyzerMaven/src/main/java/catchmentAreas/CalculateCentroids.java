
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import censusBlocks.CensusBlock;
import censusBlocks.Vertex;

public class CalculateCentroids {

	public static void main(String args[]) {
		CalculateCentroids c = new CalculateCentroids();
		c.readCensusBlocks();
	}

	private void readCensusBlocks() {

		int numRecs; // number of records

		try {
			String path = "";
			System.out.println("Reading census blocks...");
			Scanner input = new Scanner(new File(path + "censusBlocks.txt"));
			numRecs = input.nextInt();
			CensusBlock[] censusBlocks = new CensusBlock[numRecs];
			System.out.println("Reading in " + numRecs + " records...");

			for (int i = 0; i < numRecs; i++) {
				int id = input.nextInt();
				int numEdges = input.nextInt();
				Vertex[] vertices = new Vertex[numEdges];

				for (int j = 0; j < numEdges; j++) {
					double x = input.nextDouble();
					double y = input.nextDouble();
					vertices[j] = new Vertex(x, y);
				}

				censusBlocks[i] = new CensusBlock(id, vertices);
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"centroids.txt", false));
			writer.write("numberOfRecords = " + censusBlocks.length + "\n");
			for (int i = 0; i < censusBlocks.length; i++) {
				
				writer.write("id = " + (i + 1) + "\n");

				writer.write(censusBlocks[i].getCentroid().toString() + "\n");

			}
			writer.close();

			System.out.println("Done\n");
		} catch (FileNotFoundException e) {
			System.err.println("Could not find census block file");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error writing to centroid file");
			e.printStackTrace();
		}
	}
}