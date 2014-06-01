/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pod;

import edu.unt.cerl.replan.controller.GISConversionTools;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 *
 */
public class CrossingPointCleaner {

    private String tableName;
    private String tableNameCrossingPoints;
    private String tableNameCatchment;
    private boolean realTraffic;
    private String roadTable;

    public void clean(ScenarioState s) throws SQLException {
        this.tableName = ScenarioState.getAuthor() + "." + "workingcpy_"
                + s.getName() + "_crossingpoints";
        this.tableNameCrossingPoints = ScenarioState.getAuthor() + "."
                + "workingcpy_" + s.getName() + "_crossingpoints";
        this.tableNameCatchment = ScenarioState.getAuthor() + "."
                + "workingcpy_" + s.getName() + "_catchment";

        this.realTraffic = Boolean.parseBoolean((String) s.getSettings().get("traffic_info_available"));

        this.roadTable = (String) s.getSettings().get(
                "road_table");
       double buffer = new Double((String) s.getSettings().get("buffer"));
        // This number may need to be user-specified, though a minimum value should be required.
        this.executeQuery3(buffer);
        if (realTraffic) {
            this.eliminateFreewayRamps();
            boolean elim = Boolean.parseBoolean((String) s.getSettings().get("eliminate_duplicate_ids"));
            if (elim) {
                this.eliminateDuplicates_road_id();
            }
        }
    }

    private void eliminateFreewayRamps() throws SQLException {
        GISConversionTools gisConvTools = new GISConversionTools();
        Connection c = DriverManager.getConnection(gisConvTools.
                getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Connection c2 = DriverManager.getConnection(gisConvTools.
                getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Statement stmt = c.createStatement();
        Statement stmt2 = c2.createStatement();
        ResultSet results;

        String query =
                "SELECT " + tableNameCrossingPoints + ".id FROM " // + tableNameCrossingPoints + " JOIN roads_cap_tarrant_shp ON "
                + tableNameCrossingPoints + " JOIN " + roadTable + " ON " +
                tableNameCrossingPoints // + ".road_id=roads_cap_tarrant_shp.id WHERE roads_cap_tarrant_shp.funcl=6;";
                + ".road_id=" + roadTable + ".id WHERE " + roadTable +
                ".funcl=6;";
        System.out.println("\n\n" + query + "\n\n");
        results = stmt.executeQuery(query);

        int numDeleted = 0;
        String update = "";
        while (results.next()) {
            update = "DELETE FROM " + tableNameCrossingPoints + " WHERE id=" +
                    results.getInt("id") + ";";
            System.out.println(update);
            stmt2.executeUpdate(update);
            numDeleted++;
        }
        System.out.println("#############DELETED " + numDeleted +
                " funcl6 Crossing Points ################################");
        stmt2.close();
        stmt.close();
        c2.close();
        c.close();
    }

    private void eliminateDuplicates_road_id() throws SQLException {
        GISConversionTools gisConvTools = new GISConversionTools();
        Connection c = DriverManager.getConnection(gisConvTools.
                getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Connection c2 = DriverManager.getConnection(gisConvTools.
                getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Connection c3 = DriverManager.getConnection(gisConvTools.
                getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Statement stmt = c.createStatement();
        Statement stmt2 = c2.createStatement();
        Statement stmt3 = c3.createStatement();

        ResultSet results;
        ResultSet results2;

// Get list of duplicates with number of occurences
        String query =
                "SELECT road_id, COUNT(road_id) AS numoccurrences FROM " +
                tableNameCrossingPoints +
                " GROUP BY road_id HAVING (COUNT(road_id)>1) ORDER BY numoccurrences;";
        System.out.println(
                "///////////////////////////////////////////////////////////////////////////////");
        System.out.println("\n\n" + query + "\n\n");
        System.out.println(
                "///////////////////////////////////////////////////////////////////////////////");
        results = stmt.executeQuery(query);
        int temp_road_id;
        int temp_numOccurrences;
        String query2 = "";
        String update = "";
// For each occurence, we have a road_id. Find all id with each road_id and delete all but last one.
        while (results.next()) {
            temp_road_id = results.getInt("road_id");
            temp_numOccurrences = results.getInt("numoccurrences");
            for (int i = 0; i < temp_numOccurrences; i++) {
                query2 = "SELECT id FROM " + tableNameCrossingPoints +
                        " WHERE road_id=" + temp_road_id + ";";
                System.out.println(query2);
                results2 = stmt2.executeQuery(query2);
                results2.next(); // This will prime the ResutSet so that the call to next() in the while loop below will skip the first record, thus leaving us one record for each road_id
                while (results2.next()) { // now we are stepping through each duplicate id
                    update = "DELETE FROM " + tableNameCrossingPoints +
                            " WHERE id=" + results2.getInt("id") + ";";
                    System.out.println(update);
                    stmt3.executeUpdate(update);
                }
            }


        }
        stmt3.close();
        stmt2.close();
        stmt.close();
        c3.close();
        c2.close();
        c.close();
    }

    private void executeQuery1() {
        try {
            GISConversionTools gisConvTools = new GISConversionTools();
            Connection c = DriverManager.getConnection(gisConvTools.
                    getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
            Statement stmt = c.createStatement();
            String query = "DELETE FROM " + tableName + " WHERE pod > 5";
            System.out.println("\n\n" + query + "\n\n");
            stmt.executeUpdate(query);
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(CrossingPointCleaner.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void executeQuery2() {
        try {
            GISConversionTools gisConvTools = new GISConversionTools();
            Connection c = DriverManager.getConnection(gisConvTools.
                    getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
            Statement stmt = c.createStatement();
            String query =
                    "DELETE FROM " + tableName +
                    " c1 WHERE EXISTS (SELECT c1.crossing_point FROM " +
                    tableName +
                    " c2 WHERE (c1.crossing_point = c2.crossing_point) AND (c1.pod != c2.pod));";
            System.out.println("\n\n" + query + "\n\n");
            stmt.executeUpdate(query);
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(CrossingPointCleaner.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    /*
     * The following method eliminates crossing points which lie on catchment area boundaries.
     * It creates a temporary buffer around each crossing point. It then deletes each crossing
     * point whose buffer is not completely within its assigned catchment area. The radius of
     * this buffer is specified as a parameter.
     *
     * This has the same effect as specifying a minimum distance for crossing points from
     * the catchment area's border.
     *
     * bufferSize must be given in the units of the crossing points data.
     */
    private void executeQuery3(double bufferSize) {
        try {
            ResultSet results;
            GISConversionTools gisConvTools = new GISConversionTools();
            Connection c = DriverManager.getConnection(gisConvTools.
                    getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
            Statement stmt = c.createStatement();
            String query;

            /*
            query = "SELECT COUNT(DISTINCT(" + tableNameCrossingPoints + ".id)) "
            + "FROM " + tableNameCrossingPoints + " "
            + "JOIN " + tableNameCatchment + " "
            + "ON " + tableNameCrossingPoints + ".pod="
            + tableNameCatchment + ".id "
            + "WHERE ST_WITHIN( "
            + "ST_SetSRID(  "
            + "ST_BUFFER("
            + tableNameCrossingPoints + ".crossing_point," + bufferSize + "),  4326 ), "
            + "ST_SetSRID(" + tableNameCatchment + ".the_geom,4326))="
            + "FALSE ;"; // +

            System.out.println("\n\n" + query + "\n\n");
            results = stmt.executeQuery(query);
            results.next();
            System.out.println("num crossing points to delete: " + results.getInt("count"));
             */
            query =
                    "SELECT DISTINCT(" + tableNameCrossingPoints + ".id) " +
                    "FROM " + tableNameCrossingPoints + " " + "JOIN " +
                    tableNameCatchment + " " + "ON " + tableNameCrossingPoints +
                    ".pod=" + tableNameCatchment + ".id " + "WHERE ST_WITHIN( " +
                    "ST_SetSRID(  " + "ST_BUFFER(" + tableNameCrossingPoints +
                    ".crossing_point," + bufferSize + "),  4326 ), " +
                    "ST_SetSRID(" + tableNameCatchment + ".the_geom,4326))=" +
                    "FALSE  " + "ORDER BY " + tableNameCrossingPoints + ".id;";

            System.out.println(query);
            results = stmt.executeQuery(query);

            int numDeleted = 0;
            Boolean firstTimeThrough = true;
            query = "DELETE FROM " + tableNameCrossingPoints + " WHERE ";
            while (results.next()) {
                if (!firstTimeThrough) {
                    query = query + " OR ";
                }
                firstTimeThrough = false;
                query = query + "id=" + results.getInt("id");
                numDeleted++;
            }
            query = query + ";";
            if (numDeleted > 0) {
                stmt.executeUpdate(query);
            }
            System.out.println(numDeleted + " Crossing Points Deleted.");




            // ***********************************************************************
            // Now we must split apart all of the multipoints

            // First find max id of crossing points
            query = "SELECT MAX(id) FROM " + tableNameCrossingPoints + ";";
            //System.out.println(query);
            results = stmt.executeQuery(query);
            results.next();
            int max = results.getInt("max");
            //System.out.println("max id found: " + max);

            // Get list of all points inside all multipoints
            query =
                    "SELECT pod, ring, road_id, ASTEXT((ST_DUMP(crossing_point)).geom) AS geom FROM " +
                    tableNameCrossingPoints +
                    " WHERE GEOMETRYTYPE(crossing_point)='MULTIPOINT';";
            //System.out.println(query);

            // Compute new id for each point based on max id and add back to DB table
            Statement stmt2 = c.createStatement();
            ResultSet results2 = stmt.executeQuery(query);
            String toInsert;
            while (results2.next()) {
                max++;
                toInsert = "INSERT INTO " + tableNameCrossingPoints +
                        " VALUES (ST_GEOMETRYFROMTEXT('" + results2.getString(
                        "geom") + "',4326), " + results2.getInt("pod") + ", " +
                        results2.getInt("ring") + ", " + max + "," + results2.
                        getInt(
                        "road_id") + ");";
                //System.out.println(toInsert);
                stmt2.executeUpdate(toInsert);
            }

            // Delete all multipoints from the table
            String toDelete = "DELETE FROM " + tableNameCrossingPoints +
                    " WHERE GEOMETRYTYPE(crossing_point)='MULTIPOINT';";
            //System.out.println(toDelete);
            stmt2.executeUpdate(toDelete);

            // Done splitting apart multipoints!
            // ***********************************************************************

            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(CrossingPointCleaner.class.getName()).log(
                    Level.SEVERE, null, ex);
        }




    }
}
