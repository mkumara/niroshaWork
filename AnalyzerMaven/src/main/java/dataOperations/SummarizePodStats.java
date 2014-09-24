
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
import java.util.Scanner;
import java.util.StringTokenizer;


public class SummarizePodStats {

	int numberOfcatchmentAreas = 28;

	public static void main(String args[]) {
		SummarizePodStats sps = new SummarizePodStats();
		sps.summarizePopulation();
		sps.getBooths();
	}

	/**
	 * adds up the population for the individual catchment areas
	 * @return
	 * 		array filled with the population count for each of the catchment areas
	 */
	public int[] summarizePopulation() {
		int[] populationCount = new int[28];
		for (int i = 0; i < populationCount.length; i++) {
			populationCount[i] = 0;
		}

		try {
			Scanner ca = new Scanner(new FileReader("data/catchmentAreas.txt"));
			Scanner p = new Scanner(new FileReader("data/population.txt"));
			int block1, block2, catchmentArea, population;

			while (ca.hasNextLine()) {
				StringTokenizer tokenizer = new StringTokenizer(ca.nextLine(),
						",");
				block1 = new Integer(tokenizer.nextToken());
				catchmentArea = new Integer(tokenizer.nextToken());
				block2 = p.nextInt();
				population = p.nextInt();
				if (block1 != block2) {
					System.err.println("Data error: Block mismatch!");
					System.exit(0);
				}
				populationCount[catchmentArea-1] += population;
			}

		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			e.printStackTrace();
		}
		//for (int i = 0; i < populationCount.length; i++) {
		//	System.out.println(populationCount[i]);
		//}
		return populationCount;
	}

	/**
	 * read in the number of booths per POD
	 * @return
	 * 		array filled with the number of booths for each of the PODs
	 */
	public double[] getBooths() {
		double[] boothCount = new double[28];
		for (int i = 0; i < boothCount.length; i++) {
			boothCount[i] = 0;
		}

		try {
			Scanner b = new Scanner(new FileReader("data/numberOfBooths.txt"));
			int catchmentArea;
			double booths;

			while (b.hasNextDouble()) {
				catchmentArea = b.nextInt();
				booths = b.nextDouble();
				boothCount[catchmentArea-1] = booths;
			}

		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			e.printStackTrace();
		}
	//	for (int i = 0; i < boothCount.length; i++) {
	//		System.out.println(boothCount[i]);
	//	}
		return boothCount;
	}
	
}
