/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pod;

import com.csvreader.CsvReader;
import edu.unt.cerl.applicationframework.controller.DBQueries;
import edu.unt.cerl.replan.controller.GISConversionTools;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author martyo
 */
public class Csv2Db_hardcoded4Counts {

    public static void main(String args[]) throws SQLException, FileNotFoundException, IOException {
        Charset myCharset = Charset.forName("UTF-8");
        //readCsvToTable("new_csv_table", "data/csv_files/TestUTF8.csv", ',', myCharset);
        //readCsvToTable("traffic_locations", "data/traffic/Locations.txt", ',', myCharset);
        readCsvToTable("all_traffic_counts", "data/AllCounts.csv", ',', myCharset);
        //readCsvToTable("tarrant_countlocations_roads_sjoin", "data/traffic/Tarrant_CountLocations_Roads_SJOIN.csv", ',', myCharset);
    }

    public static Boolean readCsvToTable(String newTableName, String filename, char delimeter, Charset charset) throws SQLException, FileNotFoundException, IOException {
        GISConversionTools gisConvTools = new GISConversionTools();

        //set ups the DB connection: db server, username, pw, etc
        Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());

        if (DBQueries.tableExists(newTableName, c)) {
            System.out.println("Table with name " + newTableName + " already exists. Please try again!");
            return false;
        }

        ResultSet results;

        // create db statement, used to query, update, etc (regular SQL statements), obj to work on
        Statement stmt = c.createStatement();

        CsvReader reader = new CsvReader(filename, delimeter, charset);

        reader.readHeaders();
//String[] dataTypes = {"INT","INT","CHAR(30)","CHAR(30)","CHAR(10)","CHAR(30)","CHAR(30)","INT","CHAR(30)","INT","INT","INT","INT","INT","INT","INT","INT","INT","INT"};
        //String[] dataTypes = {"INT","INT","TIMESTAMP without time zone","INT","INT","INT","INT","INT","INT","INT","INT","INT","INT","INT","INT","INT","INT","CHAR(30)","INT","INT","INT","CHAR(5)"};
        String[] dataTypes = {"INT", "INT", "TIMESTAMP without time zone", "INT"};

        int numColumns = reader.getHeaderCount();

        // Iterate through CSV Headers to build SQL CREATE TABLE
        String query = "CREATE TABLE " + newTableName + " (";
        for (int i = 0; i < numColumns; i++) {
            query = query + reader.getHeader(i) + " " + dataTypes[i];
            if (i < numColumns - 1) {
                query = query + ", ";
            }
        }
        query = query + ");";

        System.out.println(query);
        stmt.executeUpdate(query);

        // Iterate through CSV, building SQL queries to add data
        while (reader.readRecord()) {
            query = "INSERT INTO " + newTableName + " VALUES (";
            for (int i = 0; i < numColumns; i++) {
                //if (i==2||i==17||i==21) query = query + "\'";
                if (i==2) query = query + "\'";
                query = query + reader.get(i); // The extra 0 here is to take care of a problem with a particular csv I was reading
                //if (i==2||i==17||i==21) query = query + "\'";

                 if (i == 2) {
                    query = query + "\'";
                }
                        if (i < numColumns - 1) {
                    query = query + ","; // just added this line********
                }
               
            }
            query = query + ");";
            System.out.println(query); // Uncomment this line to view all INSERTs as they are created
            stmt.executeUpdate(query);
        }


        reader.close();

        return true;
    }
}



