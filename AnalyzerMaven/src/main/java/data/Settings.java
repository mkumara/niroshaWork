
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package data;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Settings {

    private final String DEFAULT_TIME = "DEFAULT_TIME_PER_BOOTH";
    private final String DEFAULT_CAR = "DEFAULT_NUMBER_OF_PEOPLE_PER_CAR";
    private final String DEFAULT_DIST_METRIC = "DEFAULT_DIST_KM";
    private double default_time_per_booth;
    private double default_people_per_car;
    private boolean distance_in_km = true;

    public Settings() {
        try {
            Scanner s = new Scanner(new FileReader("podanalyzer_settings.txt"));
            while (s.hasNextLine()) {
                String line = s.nextLine();
                StringTokenizer tokenizer = new StringTokenizer(line);
                String token = tokenizer.nextToken();
                if (token.equals(this.DEFAULT_TIME)) {
                    tokenizer.nextToken();
                    this.default_time_per_booth = new Double(tokenizer.nextToken());

                }
                if (line.startsWith(this.DEFAULT_CAR)) {
                    Scanner lineScanner = new Scanner(line);
                    tokenizer.nextToken();
                    this.default_people_per_car = new Double(tokenizer.nextToken());
                }
                if (line.startsWith(this.DEFAULT_DIST_METRIC)) {
                    Scanner lineScanner = new Scanner(line);
                    tokenizer.nextToken();
                    String valueRead = tokenizer.nextToken();
                    if (valueRead.contentEquals("TRUE") || valueRead.contentEquals("true")) {
                        this.distance_in_km = true;
                    } else {
                        this.distance_in_km = false;
                    }

                }

            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading settings");

        }
    }

    public double getDefaultTimePerBooth() {
        return this.default_time_per_booth;
    }

    public double getDefaultPeoplePerCar() {
        return this.default_people_per_car;
    }

    public void setDefaultTimePerBooth(double newTime) {
        this.default_time_per_booth = newTime;
    }

    public void setDefaultPeoplePerCar(double newNumber) {
        this.default_people_per_car = newNumber;
    }

    public boolean getDefaultDistinKM() {
        return this.distance_in_km;
    }

    public void setDefaultTimePerBooth(boolean val) {
        this.distance_in_km = val;
    }

    public void writeSettings() {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(
                    "settings.txt", false));
            output.write(this.DEFAULT_TIME + " = "
                    + this.default_time_per_booth);
            output.write("\n");
            output.write(this.DEFAULT_CAR + " = "
                    + this.default_people_per_car);
            output.write("\n");
            output.write(this.DEFAULT_DIST_METRIC + " = "
                    + this.distance_in_km);
            output.close();
        } catch (IOException e) {
            System.err.println("Error writing to settings.txt");
        }
    }
}
