package edu.unt.cerl.replan.controller.db;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * Interface ExportCSV
 */
public interface ExportCSV {

    //
    // Fields
    //
    //
    // Methods
    //
    public void exportPOD_fromDB_CSV(String filename) throws IOException, SQLException;

    public void exportPOD_fromDB_CSV(String filename, char delimiter, Charset myCharset) throws IOException, SQLException;
//    public static void exportPOD_fromDB_CSV( String filename) throws IOException, SQLException
//    //{
////        Charset myCharset = Charset.forName("UTF-8");
////        exportPOD_fromDB_CSV( filename, ',', myCharset);
//
//
//    //}
//
//    public static void exportPOD_fromDB_CSV( String filename, char delimiter, Charset myCharset) throws IOException, SQLException
//    //{
////
////        CsvWriter myFile = new CsvWriter(filename, delimiter, myCharset);
////        String headers[] = {"id", "name", "address", "city", "zip", "latitude", "longitude", "type", "status",  "comments", "numbooths"};
////        myFile.writeRecord(headers);
////
//////        GISConversionTools gisConvTools = new GISConversionTools();
//////        Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
//////        Statement stmt = c.createStatement();
////
////        Connection c = REPLAN.getController().getConnection();
////        Statement stmt = c.createStatement();
////        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
////
////
////        String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + "_pods";
////
////        String query = "Select *,  ST_X(location) as lon, ST_Y(location) as lat FROM " + tableName + ";";
////        ResultSet results = stmt.executeQuery(query);
////
////        while (results.next()) {
////            myFile.write((String) results.getString("id"));
////            myFile.write((String) results.getString("name"));
////            myFile.write((String) results.getString("address"));
////            myFile.write((String) results.getString("city"));
////            myFile.write((String) results.getString("zip"));
////            myFile.write((String) results.getString("lon"));
////            myFile.write((String) results.getString("lat"));
////            myFile.write((String) results.getString("type"));
////            myFile.write((String) results.getString("status"));
////            myFile.write((String) results.getString("comments"));
////            myFile.write((String) results.getString("numbooths"));
////            myFile.endRecord();
////        }
////
////
////
////        /*
////        Map tempMap;
////        for (int i = 0, n = myList.size(); i < n; i++) {
////        tempMap = (Map) myList.get(i);
////        myFile.write((String) tempMap.get("id"));
////        myFile.write((String) tempMap.get("name"));
////        myFile.write((String) tempMap.get("addy"));
////        myFile.write((String) tempMap.get("city"));
////        myFile.write((String) tempMap.get("zip"));
////        myFile.write((String) tempMap.get("lon"));
////        myFile.write((String) tempMap.get("lat"));
////        myFile.write((String) tempMap.get("status"));
////        myFile.write((String) tempMap.get("comments"));
////        myFile.write((String) tempMap.get("booths"));
////        myFile.write((String) tempMap.get("is_public"));
////        myFile.endRecord();
////        }
////         */
////
////        myFile.close(); // writes and closes file
////        stmt.close();
////        c.close();
//    //}
    //
    // Accessor methods
    //
    //
    // Other methods
    //
}
