package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreatePODFiles {

	public static void main(String args[]) {

		Pattern p = Pattern.compile("<p>.*?</p>");

		String path = "";
		System.out.println("Reading file...");
		try {

			String header = "// ID \t\t SITE \t\t ADDRESS \t\t	CITY \t\t "
					+ "ZIP \t\t ISD \t\t	LON \t\t LAT \t\t QUAD \n";

			BufferedWriter pods = new BufferedWriter(new FileWriter("pods.txt",
					false));

			pods.write(header);

			Scanner input = new Scanner(new File(path + "PODs_extract.html"));

			while (input.hasNext()) {
				String s = input.nextLine();

				Matcher m = p.matcher(s);
				while (m.find()) {
					String f = "";
					String found = m.group();
					Double d = new Double(found
							.substring(3, found.length() - 5));
					f = ((Integer) d.intValue()).toString();

					for (int i = 0; i < 8; i++) {
						m.find();
						found = m.group();
						found = found.substring(3, found.length() - 5);
						if (i < 3 || i == 4) {
							found = "\"" + found + "\"";
						}
						if (i == 3) {
							found = ((Integer) (new Double(found)).intValue())
									.toString();
						}
						f = f + "\t\t" + found;
					}
					pods.write(f + "\n");

				}

			}
			pods.close();

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
