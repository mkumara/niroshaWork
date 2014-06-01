/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller;

import com.csvreader.CsvReader;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.POD;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.PODTracker;
import edu.unt.cerl.replan.view.TAMU_Geocoding;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author sarat
 */
public class ReadPODsCSV {

//    private class POD_Record {
//
//        private int id;
//        private String name;
//        private String address;
//        private String city;
//        private String zip;
//        private double longitude;
//        private double latitude;
//        //private String additional;
//        private String comments;
//        private int numBooths;
//        private Boolean type;
//        private Boolean status;
//
//        private POD_Record() {
//        }
//    } // end inner class POD_Record
//    // **********************************************
    //private POD_Record records[];
    private POD[] records;
    private int numRecords;
    
    


    public ReadPODsCSV(String filename, char delimeter, Charset charset) throws SQLException, FileNotFoundException, IOException {
        readCsv(filename, delimeter, charset);
    }

    
//    public static void main(String args[]) throws SQLException, FileNotFoundException, IOException {
//        Charset myCharset = Charset.forName("UTF-8");
//        readCsvToTable("testing_csv9", "data/csv_files/TestUTF8c.csv", ',', myCharset);
//    }
    public Boolean returnType(int i) {
        return records[i].get_type();
    }

    public int returnNumBooths(int i) {
        return records[i].get_numBooths();
    }

    public double returnLatitude(int i) {
        return records[i].get_latitude();
    }

    public double returnLongitude(int i) {
        return records[i].get_longitude();
    }

    public String returnComments(int i) {
        return records[i].get_comments();
    }

//    public String returnAdditional(int i) {
//        return records[i].additional;
//    }
    public String returnZip(int i) {
        return records[i].get_zip();
    }

    public String returnCity(int i) {
        return records[i].get_city();
    }

    public String returnAddress(int i) {
        return records[i].get_address();
    }

    public String returnName(int i) {
        return records[i].get_name();
    }

    public int returnId(int i) {
        return records[i].get_id();
    }

//    public String returnOn(int i) {
//        if (records[i].on) return "true";
//        return "false";
//    }
    public Boolean returnStatus(int i) {
        return records[i].get_status();
    }

    public int returnNumRecords() {
        return numRecords;
    }

    public Boolean readCsv(String filename, char delimeter, Charset charset) throws SQLException, FileNotFoundException, IOException {
        // *******************************
        //Need to find number of records in CSV file
        CsvReader readerForCounting = new CsvReader(filename, delimeter, charset);
        int numRecordsFound = 0;

        readerForCounting.readHeaders();
        while (readerForCounting.readRecord()) {
            numRecordsFound++;
        }
        readerForCounting = null; // We don't need this object anymore. Hopefully garabage collector will realize that.
        // *******************************
        numRecords = numRecordsFound;
        //records = new POD_Record[numRecords];
        records = new POD[numRecords];
        CsvReader reader = new CsvReader(filename, delimeter, charset);
        String headers[] = new String[reader.getColumnCount()];
        reader.readHeaders();
        headers = reader.getHeaders();

        reader.setHeaders(headers);

        // Set the headers to lowercase
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].toLowerCase();
        }
        reader.setHeaders(headers);

//        String tempPublicOrCorporate = "";
//        char tempPublicOrCorporateFirstLetter = ' ';
//        int character = 0;
        
        
       
        
        
        for (int i = 0; i < numRecords; i++) {
            reader.readRecord();
            records[i] = new POD();
            if (reader.get("id") == null || reader.get("id").equals("")) {
                records[i].set_id(-999); //no data available
            } else {
                records[i].set_id((int) Double.parseDouble(reader.get("id")));
            }

            if (reader.get("numbooths") == null || reader.get("numbooths").equals("")) {
                records[i].set_numBooths(1); //Set to 1 by default if no value given
            } else {
                records[i].set_numBooths((int) Double.parseDouble(reader.get("numbooths")));
                if (records[i].get_numBooths() < 1) {
                    records[i].set_numBooths(1); // if zero or negative, set to 1 by default
                }
            }

            double longitude = 0.0;
            double latitude = 0.0;
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
            
            records[i].createPostGisPoint(longitude, latitude);
            records[i].createVividPoint(longitude, latitude);

//            if (reader.get("latitude") == null || reader.get("latitude").equals("")) {
//                records[i].set_latitude(0.0);
//            } else {
//                records[i].set_latitude(Double.parseDouble(reader.get("latitude")));
//            }
//
//            if (reader.get("longitude") == null || reader.get("longitude").equals("")) {
//                records[i].set_longitude(0.0);
//            } else {
//                records[i].set_longitude(Double.parseDouble(reader.get("longitude")));
//            }
//            records[i].createPostGisPoint(records[i].get_longitude(), records[i].get_latitude());
//            records[i].createVividPoint(records[i].get_longitude(), records[i].get_latitude());

            records[i].set_name(reader.get("name"));

            // validate the name
            records[i].set_name(PODTracker.makeValidPODName(records[i].get_name()));

            records[i].set_address(reader.get("address"));
            records[i].set_city(reader.get("city"));
            records[i].set_zip(reader.get("zip"));
            records[i].set_type(Boolean.parseBoolean(reader.get("type")));
            //records[i].set_latitude(Double.parseDouble(reader.get("latitude")));

            records[i].set_comments(reader.get("comments"));
            //records[i].set_numBooths(Integer.parseInt(reader.get("numbooths")));
            records[i].set_status(Boolean.parseBoolean(reader.get("status")));
            
            records[i].set_latitude(latitude); 
            records[i].set_longitude(longitude);
            
            
            
//            tempPublicOrCorporate = reader.get("type").toLowerCase();
//            // Just look at first letter. If it is a 'c', then corporate, by default
//            // it is public
//            if (tempPublicOrCorporate.length() > 0 && tempPublicOrCorporate.charAt(0) == 'c') {
//                records[i].type = "corporate";
//            } else {
//                records[i].type = "public";
//            }
//            if (reader.get("longitude").equals("")) {
//                records[i].longitude = -999; //no data available
//            } else {
//                records[i].longitude = Double.parseDouble(reader.get("longitude"));
//            }
//
//            if (reader.get("latitude").equals("")) {
//                records[i].latitude = -999; //no data available
//            } else {
//                records[i].latitude = Double.parseDouble(reader.get("latitude"));
//            }
//            records[i].additional = reader.get("additional");
//            records[i].comments = reader.get("comments");
//            records[i].status = reader.get("status");
//            if (reader.get("on").equals("")) {
//                records[i].on = true;
//            } else {
//                if (reader.get("on").equals("off")) {
//                    records[i].on = false;
//                } else {
//                    records[i].on = true;
//                }
//            }
        }

        
        reader.close();
        
        return true;
    }
   
    public static Boolean readCsvToTable(String newTableName, String filename, char delimeter, Charset charset) throws SQLException, FileNotFoundException, IOException {

        //GISConversionTools gisConvTools = new GISConversionTools();
        //set ups the DB connection: db server, username, pw, etc
        Connection c = REPLAN.getController().getConnection();

        if (REPLAN.getQueries().tableExists(UserState.userId, newTableName, c)) {
            System.out.println("Table with name " + newTableName + " already exists. Please try again!");
            return false;
        }

        // create db statement, used to query, update, etc (regular SQL statements), obj to work on
        Statement stmt = c.createStatement();

        CsvReader reader = new CsvReader(filename, delimeter, charset);
        String headers[] = new String[reader.getColumnCount()];
        reader.readHeaders();
        headers = reader.getHeaders();

        String query = "CREATE TABLE " + newTableName + " (id INT, name TEXT, address TEXT, city TEXT, zip TEXT, longitude NUMERIC(10,5), latitude NUMERIC(10,5), additional TEXT, comments TEXT);";

        stmt.executeUpdate(query);

        // Set the headers to lowercase
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].toLowerCase();
        }
        reader.setHeaders(headers);

        while (reader.readRecord()) {
            query = "INSERT INTO " + newTableName + " VALUES (" + reader.get("id") + ", '" + reader.get("name") + "', '" + reader.get("address") + "', '" + reader.get("city") + "', '" + reader.get("zip") + "', " + reader.get("longitude") + ", " + reader.get("latitude") + ", '" + reader.get("additional") + "', '" + reader.get("comments") + "');";
            System.out.println(query);
            stmt.executeUpdate(query);
        }

        reader.close();
        return true;
    }
}
