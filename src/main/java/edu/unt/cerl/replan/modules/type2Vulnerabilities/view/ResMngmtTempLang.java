/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.view;

import com.csvreader.CsvReader;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.Resource;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.Structures;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author saratchandraindrakanti
 */
public class ResMngmtTempLang {
    //Number of translators available

    /*Each Volunteer*/
    //Regional Preferences - Space
    //Time for which avaialble - Time
    //Other Languages 
    //Other skills / roles
    //Home location (Lat/long)
    /*Read inputs of volunteers from a  csv file into a translator structure*/
    
    private ArrayList<Translator> translators;
    private File resourceFile;

    public ResMngmtTempLang() {

    }

    private static final ResMngmtTempLang instance = new ResMngmtTempLang();

    public static ResMngmtTempLang getInstance() {
        return instance;
    }

    public void setFile(File file) {
        this.resourceFile = file;
    }

    public class Translator extends Resource {

        boolean spanish;
        boolean indo_eur;
        boolean asian;
        boolean other;

        public Translator(double latitude, double longitude, int availability, int id) {
            super(latitude, longitude, availability, id);
        }

        public Translator(double latitude, double longitude, int availability, int id, boolean spanish, boolean indo_eur, boolean asian, boolean other) {
            super(latitude, longitude, availability, id);
            this.asian = asian;
            this.spanish = spanish;
            this.indo_eur = indo_eur;
            this.other = other;
        }

//        public double latitude;
//        public double longitude;
//        public int availability; //0-all the time; 1- 1st half; 2-2nd half; ... so on
//        public int id;
//
//        public Translator(double latitude, double longitude, int availability, int id) {
//            this.availability = availability;
//            this.id = id;
//            this.latitude = latitude;
//            this.longitude = longitude;
//        }
    }

    public void addNewTranslator(Translator trans) {
        translators.add(trans);
    }

    public ArrayList<Translator> getTransaltorsList() {
        return this.translators;
    }

    public void populateTranslatorsList(int index) {
        //Read available translators from file
        //add elements to translators arraylist
        translators = new ArrayList<Translator>();
        HashMap<Integer, Resource> resList = new HashMap<Integer, Resource>();
        //use resourceFiles
        try {
            CsvReader readerForCounting;
            int numRecordsFound = 0;
            int numRecords;

            readerForCounting = new CsvReader(resourceFile.getAbsolutePath(), ',', Charset.forName("UTF-8"));
            readerForCounting.readHeaders();
            while (readerForCounting.readRecord()) {
                numRecordsFound++;
            }
            readerForCounting = null;
            numRecords = numRecordsFound;
            CsvReader reader = new CsvReader(resourceFile.getAbsolutePath(), ',', Charset.forName("UTF-8"));
            //String [] headers = new String[reader.getColumnCount()];
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            reader.setHeaders(headers);

            // Set the headers to lowercase
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].toLowerCase();
            }
            reader.setHeaders(headers);

            for (int i = 0; i < numRecords; i++) {
                reader.readRecord();
                double longitude;
                double latitude;
                int availability;
                int id;
                boolean spanish;
                boolean indo_eur;
                boolean asian;
                boolean other;

                if (reader.get("latitude") != null && !reader.get("latitude").equals("")) {
                    latitude = Double.parseDouble(reader.get("latitude"));
                } else {
                    latitude = 0.0;
                }

                if (reader.get("longitude") != null && !reader.get("longitude").equals("")) {
                    longitude = Double.parseDouble(reader.get("longitude"));
                } else {
                    longitude = 0.0;
                }

                id = Integer.parseInt(reader.get("id"));
                availability = Integer.parseInt(reader.get("availability"));
                int spn = Integer.parseInt(reader.get("Spanish".toLowerCase()));
                int indo = Integer.parseInt(reader.get("Other Indo-European".toLowerCase()));
                int asi = Integer.parseInt(reader.get("Asian and Pacific Island".toLowerCase()));
                int oth = Integer.parseInt(reader.get("Other".toLowerCase()));
                if (spn == 1) {
                    spanish = Boolean.TRUE;
                } else {
                    spanish = Boolean.FALSE;
                }
                if (indo == 1) {
                    indo_eur = Boolean.TRUE;
                } else {
                    indo_eur = Boolean.FALSE;
                }
                if (asi == 1) {
                    asian = Boolean.TRUE;
                } else {
                    asian = Boolean.FALSE;
                }
                if (oth == 1) {
                    other = Boolean.TRUE;
                } else {
                    other = Boolean.FALSE;
                }
                
                Translator tr = new Translator(latitude, longitude, availability, id, spanish, indo_eur, asian, other);
                translators.add(tr);
                resList.put(id, tr);
                System.out.println("Added Translator<" + id + "," + latitude + "," + longitude + "," + availability + ">");
            }
            Structures.getInstance().setResourceList(resList, index);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResMngmtTempLang.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
