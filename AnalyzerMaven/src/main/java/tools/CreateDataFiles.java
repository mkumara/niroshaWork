package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateDataFiles {

	public static void main(String args[]) {
		
		// Pattern p = Pattern.compile(REGEX);
		// String[] items = p.split(INPUT);
		// for (String s : items) {
		// System.out.println(s);
		// }

		Pattern p_gis = Pattern.compile("[\\d]{15}");
		// Pattern sdval = Pattern.compile("SDVAL=\"-?[\\d]+\\.[\\d]+\"");
		Pattern sdval = Pattern.compile("SDVAL=\"-?[\\d]+\\.?[\\d]*\"");
		Pattern val = Pattern.compile("-?[\\d]+[\\.]?[\\d]*");
		Boolean first = true;
		String path = "";
		System.out.println("Reading file...");
		try {
			
			BufferedWriter w_gis = new BufferedWriter(new FileWriter(
					"gisid.txt", false));
			BufferedWriter w_area = new BufferedWriter(new FileWriter(
					"area.txt", false));
			BufferedWriter w_population = new BufferedWriter(new FileWriter(
					"population.txt", false));
			BufferedWriter w_centroids = new BufferedWriter(new FileWriter(
					"centroids.txt", false));
			
			Scanner input = new Scanner(new File(path
					+ "BlockID_XY_AREA_POP.html"));
			while (input.hasNext()) {
				String s = input.nextLine();
				if (s.contains("<TR>")) {
					if (first) {
						first = false;
						continue;
					}
					Matcher matcher = p_gis.matcher(input.nextLine());
					matcher.find();
					String gisID = matcher.group();

					matcher = sdval.matcher(input.nextLine());
					matcher.find();
					String value = matcher.group();
					matcher = val.matcher(value);
					matcher.find();
					String centroid_x = matcher.group();

					matcher = sdval.matcher(input.nextLine());
					matcher.find();
					value = matcher.group();
					matcher = val.matcher(value);
					matcher.find();
					String centroid_y = matcher.group();

					matcher = sdval.matcher(input.nextLine());
					matcher.find();
					value = matcher.group();
					matcher = val.matcher(value);
					matcher.find();
					String population = matcher.group();

					matcher = sdval.matcher(input.nextLine());
					matcher.find();
					value = matcher.group();
					matcher = val.matcher(value);
					matcher.find();
					String area = matcher.group();
					
					matcher = sdval.matcher(input.nextLine());
					matcher.find();
					value = matcher.group();
					matcher = val.matcher(value);
					matcher.find();
					int id = (new Integer(matcher.group())) + 1;
					
					w_centroids.write(id + "\t" + centroid_x + "\t" + centroid_y + "\n");
					w_population.write(id + "\t" + population + "\n");
					w_area.write(id + "\t" + area + "\n");
					w_gis.write(id + "\t" + gisID + "\n");

				}
			}
			w_centroids.close();
			w_population.close();
			w_area.close();
			w_gis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("File BlockID_XY_AREA_POP.html not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error writing to file");
			e.printStackTrace();
		}

		System.out.println("Done");

	}

}