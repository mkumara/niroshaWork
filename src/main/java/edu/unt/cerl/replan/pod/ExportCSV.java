package edu.unt.cerl.replan.pod;

import java.io.IOException;
import java.nio.charset.Charset;
import com.csvreader.CsvWriter;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.controller.GISConversionTools;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author martyo
 */
public class ExportCSV {

    public static void main(String args[]) throws IOException {
        // some basic testing - only actually creating id and name fields in test map
        Charset myCharset = Charset.forName("UTF-8");
        List testList = new LinkedList();
        testList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            Map newMap = new HashMap();
            newMap.put("id", "" + i);
            newMap.put("name", "name" + i);
            testList.add(newMap);
        }
        exportPOD_CSV(testList, "C:/testing_writer.csv");
    }

    // Overloaded copy of exportPOD_CSV which has the comma hard-coded as the delimter
    // and UTF-8 hard-coded as the Charset
    public static void exportPOD_CSV(List myList, String filename) throws IOException {
        Charset myCharset = Charset.forName("UTF-8");
        exportPOD_CSV(myList, filename, ',', myCharset);
    }

    // This method accepts a List of Map's, where each Map is a POD record.
    // It then creates a file of name filename with the POD headers and the 
    // records contained in the Map's of the List.
    public static void exportPOD_CSV(List myList, String filename, char delimiter, Charset myCharset) throws IOException {
        CsvWriter myFile = new CsvWriter(filename, delimiter, myCharset);

        String headers[] = {"id", "name", "address", "city", "zip", "latitude", "longitude", "status", "comments", "numbooths", "type"};
        myFile.writeRecord(headers);
        Map tempMap;
        for (int i = 0, n = myList.size(); i < n; i++) {
            tempMap = (Map) myList.get(i);
            myFile.write((String) tempMap.get("id"));
            myFile.write((String) tempMap.get("name"));
            myFile.write((String) tempMap.get("addy"));
            myFile.write((String) tempMap.get("city"));
            myFile.write((String) tempMap.get("zip"));
            myFile.write((String) tempMap.get("lat"));
            myFile.write((String) tempMap.get("lon"));
            myFile.write((String) tempMap.get("status"));
            myFile.write((String) tempMap.get("comments"));
            myFile.write((String) tempMap.get("booths"));
            myFile.write((String) tempMap.get("is_public"));
            myFile.endRecord();
        }
        myFile.close(); // writes and closes file
    }

    public static void exportPOD_fromDB_CSV(ScenarioState s, String filename) throws IOException, SQLException {
        Charset myCharset = Charset.forName("UTF-8");
        exportPOD_fromDB_CSV(s, filename, ',', myCharset);
    }

    public static void exportPOD_fromDB_CSV(ScenarioState s, String filename, char delimiter, Charset myCharset) throws IOException, SQLException {

        CsvWriter myFile = new CsvWriter(filename, delimiter, myCharset);
        String headers[] = {"id", "name", "address", "city", "zip", "latitude", "longitude", "status", "comments", "numbooths", "type", "onoff"};
        myFile.writeRecord(headers);

        GISConversionTools gisConvTools = new GISConversionTools();
        Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Statement stmt = c.createStatement();


        String tableName = ScenarioState.getAuthor() + "." + "workingcpy_" + s.getName() + DefaultConstants.POD_SUFFIX;

        String query = "SELECT id, name, address, city, zip, status, comments, onoff, numbooths, type, ST_X(location) AS lon, ST_Y(location) AS lat FROM " + tableName + " ORDER BY id;";
        ResultSet results = stmt.executeQuery(query);

        while (results.next()) {
            myFile.write((String) results.getString("id"));
            myFile.write((String) results.getString("name"));
            myFile.write((String) results.getString("address"));
            myFile.write((String) results.getString("city"));
            myFile.write((String) results.getString("zip"));
            myFile.write((String) results.getString("lat"));
            myFile.write((String) results.getString("lon"));
            myFile.write((String) results.getString("status"));
            myFile.write((String) results.getString("comments"));
            myFile.write((String) results.getString("numbooths"));
            myFile.write((String) results.getString("type"));
            myFile.write((String) results.getString("onoff"));
            myFile.endRecord();
        }



        /*
        Map tempMap;
        for (int i = 0, n = myList.size(); i < n; i++) {
        tempMap = (Map) myList.get(i);
        myFile.write((String) tempMap.get("id"));
        myFile.write((String) tempMap.get("name"));
        myFile.write((String) tempMap.get("addy"));
        myFile.write((String) tempMap.get("city"));
        myFile.write((String) tempMap.get("zip"));
        myFile.write((String) tempMap.get("lon"));
        myFile.write((String) tempMap.get("lat"));
        myFile.write((String) tempMap.get("status"));
        myFile.write((String) tempMap.get("comments"));
        myFile.write((String) tempMap.get("booths"));
        myFile.write((String) tempMap.get("is_public"));
        myFile.endRecord();
        }
         */

        myFile.close(); // writes and closes file
        stmt.close();
        c.close();
    }
}
