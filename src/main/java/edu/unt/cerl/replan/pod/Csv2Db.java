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
public class Csv2Db {

    public static void main(String args[]) throws SQLException, FileNotFoundException, IOException
    {
        Charset myCharset = Charset.forName("UTF-8");
        //readCsvToTable("new_csv_table", "data/csv_files/TestUTF8.csv", ',', myCharset);
        //readCsvToTable("traffic_locations", "data/traffic/Locations.txt", ',', myCharset);
        readCsvToTable("traffic_counts", "data/traffic/Counts.txt", ',', myCharset);
    }

    public static Boolean readCsvToTable(String newTableName, String filename, char delimeter, Charset charset) throws SQLException, FileNotFoundException, IOException
    {
        GISConversionTools gisConvTools = new GISConversionTools();

        //set ups the DB connection: db server, username, pw, etc
        Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());

      if (DBQueries.tableExists(newTableName, c))
       {
            System.out.println("Table with name " + newTableName + " already exists. Please try again!");
            return false;
       }

        ResultSet results;

        // create db statement, used to query, update, etc (regular SQL statements), obj to work on
        Statement stmt = c.createStatement();

        CsvReader reader = new CsvReader(filename,delimeter,charset);

        reader.readHeaders();
        int numColumns = reader.getHeaderCount();

        // Iterate through CSV Headers to build SQL CREATE TABLE
        String query="CREATE TABLE " + newTableName + " (";
        for (int i=0; i<numColumns; i++)
        {
           query = query + reader.getHeader(i) + " CHAR(50)";
           if (i<numColumns-1) query = query + ", ";
        }
        query = query + ");";

        System.out.println(query);
        stmt.executeUpdate(query);

       // Iterate through CSV, building SQL queries to add data
        while(reader.readRecord())
        {
            query = "INSERT INTO " + newTableName + " VALUES (";
            for (int i=0; i<numColumns; i++)
        {
            query = query + "\'" + reader.get(i);
            if (i < numColumns-1) query = query + "\', ";
        }
            query = query + "\');";
            //System.out.println(query); // Uncomment this line to view all INSERTs as they are created
            stmt.executeUpdate(query);
        }


        reader.close();

        return true;
        }

    

    }



