/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.applicationframework.controller;


import edu.unt.cerl.applicationframework.model.DBInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class ReadDatasets {

    public final static String BLOCKS = "_census_blocks";
    public final static String CENTROIDS = "_census_blocks_centroids";
    public final static String SHP = "_shp";
    public final static String ROADS = "_roads";
    public final static String POPULATIOIN = "_population";

    /*
     * Read in datasets file at default location and store names in a list
     */
    public static List<String> readNames() {
            return readNames("settings/Datasets.txt");
    }

        /*
     * Read in datasets file and store names in a list
     */
    public static List<String> readNames(String path) {
        List<String> datasets = new LinkedList<String>();
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
                datasets.add(line);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return datasets;

    }


}
