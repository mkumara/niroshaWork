
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



/*
 * Reads database information from a database settings file
 */
package edu.unt.cerl.applicationframework.controller;

import edu.unt.cerl.applicationframework.model.DBInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author tamara
 */
public class ReadDBSettings {

    public static final String TYPE = "type";
    public static final String DATASET = "dataset";
    public static final String SETTINGS = "settings";

    /*
     * Read in settings from a file with the default path
     * and store settings in a hash map
     */
    public static Map readParams() {
        return readParams("settings/DatabaseSettings.txt");
    }

    /**
     * Read in setting file from a custom path
     * @param path
     * @return settings in a map
     */
    public static List readParamsWithDatasets(String path) {
        List<Map> mapList = new LinkedList<Map>();
        Map<String, String> settings = new HashMap<String, String>();
        settings.put(TYPE, SETTINGS);
        try {

            File file = new File(path);
            Scanner s = new Scanner(file);
            //Scanner s = new Scanner(ReadDBSettings.class.getResourceAsStream(path));
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("%")) {
                    continue;
                }

                line = line.replaceAll("\\s", "");

                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("#")) {
                    Map<String, String> dataset =
                            handleDataset(s, line, mapList);
                    if (dataset != null) {
                        mapList.add(dataset);

                    }
                    continue;
                }
                String[] tokens = line.split("=");
                settings.put(tokens[0], tokens[1]);
            }
            mapList.add(settings);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapList;

    }

    private static Map<String, String> handleDataset(Scanner s, String line,
            List l) {
        //   System.out.println("DATASET");
        Map<String, String> datasetMap = new HashMap();
        if (line.equals("#dataset")) {
            datasetMap.put(TYPE, DATASET);
            while (true) {
                line = s.nextLine();
                if (line.startsWith("%")) {
                    continue;
                }
                if (line.contains("\"")) {

                    Pattern p = Pattern.compile("\"");
                    String[] result = p.split(line);
                    result[0] = result[0].replaceAll("\\s", "");
                    line = result[0] + result[1];
                } else {
                    line = line.replaceAll("\\s", "");
                }
                if (line.length() == 0) {
                    continue;
                }
                if (line.equals("#END")) {
                    break;
                }
                String[] tokens = line.split("=");
                datasetMap.put(tokens[0], tokens[1]);
                //   System.out.println(tokens[0] + "\t\t" + tokens[1]);
            }
            //  System.out.println("END!!!");
        } else {
            System.err.println("Syntax error in dataset");
        }
        return datasetMap;
    }

    /**
     * Read in setting file from a custom path
     * @param path
     * @return settings in a map
     */
    public static Map readParams(String path) {
        Map<String, String> settings = new HashMap<String, String>();
        try {
            File file = new File(path);
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("%")) {
                    continue;
                }
                line = line.replaceAll("\\s", "");
                if (line.length() == 0) {
                    continue;
                }
                String[] tokens = line.split("=");
                settings.put(tokens[0], tokens[1]);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return settings;

    }
}
