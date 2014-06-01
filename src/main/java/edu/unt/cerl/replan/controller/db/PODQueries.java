package edu.unt.cerl.replan.controller.db;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.POD;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Class PODQueries
 */
public class PODQueries implements Observer {

    private static Observable obs;

    public PODQueries(Observable obs) {
        obs.addObserver(this);
    }

    ;

    public static void main(String args[]) throws SQLException {
        //createPODTable();
        //

        /*String[] geographies = new String[3];
        geographies[0] = "denton";
        geographies[1] = "tarrant";
        geographies[2] = "dallas";

        ScenarioState scenario = new ScenarioState("testing", "martyo", "this is just a dummy state", true, geographies);
         * */
        //createPODTable();
    }

    public static boolean createPODTable(ScenarioState state) throws SQLException { // returns true if table was created, false if table already existed
        Statement stmt = REPLAN.getController().getConnection().createStatement();
        //ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        DBQueriesJava dbQueries = new DBQueriesJava();
        // determine if table exists. If not, create it.
        //if (!dbQueries.tableExists(REPLAN.getController().getConnection(), tableName)) {
        if (dbQueries.tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) {
            String query = "DROP TABLE " + tableName + ";";
            stmt.executeUpdate(query);
        }
        // if (!dbQueries.tableExists(REPLAN.getController().getConnection(), UserState.userId, state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX)) {
        // String tableName = "test_table_20110726a";
        System.out.println("Table Name: " + tableName + " exists? " + dbQueries.tableExists(tableName, REPLAN.getController().getConnection()));
        String query = "CREATE TABLE " + tableName + " (fid integer, \"location\" geometry, id integer, \"name\" character varying(255), address character varying(255),  city character varying(255),  zip character varying(255),  status character varying(255),  comments character varying(255),  onoff character varying(255),  numbooths integer,  \"type\" character varying(255));";
        stmt.executeUpdate(query);
        System.out.println("*** Created table " + tableName + " ***");
        query = "UPDATE " + tableName + " SET location = transform(location, 4326);";
        System.out.println(query);
        stmt.executeUpdate(query);
        state.setPodsSelected(true);
        REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
        return true;
    }

    public static void addNewPOD(ScenarioState state, String name, String address, String city, String zip, double latitude, double longitude, Boolean type, Boolean status, String comments, int numBooths) throws SQLException {
//// creates POD table if one doesn't yet exist and then adds POD to it
        Statement stmt = REPLAN.getController().getConnection().createStatement();
        //ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        boolean isFirstPODAdded = false;
        DBQueriesJava dbQueries = new DBQueriesJava();
        if (!dbQueries.tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) {
            createPODTable(state);
            isFirstPODAdded = true;
        }
        String query = "SELECT MAX(id) AS max_id, MAX(fid) AS max_fid FROM " + tableName + ";";
        ResultSet results = stmt.executeQuery(query);
        results.next();
        // the new POD's id and fid will be the max of each plus 1 unless the table was just created
        // This means if all PODs are deleted, the new fid will be 1.
        int id = results.getInt("max_id") + 1;
        int fid;
        if (isFirstPODAdded) {
            fid = 0;
        } else {
            fid = results.getInt("max_fid") + 1;
        }
//        createPODTable(); // if table doesn't exist, this method creates it
//        query = "INSERT INTO " + tableName + " VALUES ( " + fid + ", SETSRID(MAKEPOINT(" + latitude + "," + longitude + "),4326)," + id + ",'" + name + "','" + address + "','" + city + "','" + zip + "'," + type + "," + status + ",'" + comments + "'," + numBooths + ");"; // need to fill this in the rest of the way
        query = "INSERT INTO " + tableName + " VALUES ( " + fid + ", SETSRID(MAKEPOINT(" + latitude + "," + longitude + "),4326)," + id + ",'" + name + "','" + address + "','" + city + "','" + zip + "'," + status + ",'" + comments + "','" + "" + "'," + numBooths + "," + type + ");";
        System.out.println(query);
        stmt.executeUpdate(query);
    }

    public static void delete_POD(int fid) throws SQLException {
        Statement stmt = REPLAN.getController().getConnection().createStatement();
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        DBQueriesJava dbQueries = new DBQueriesJava();




        // if table exists, if entry exists, delete POD entry
        //if ((dbQueries.tableExists(tableName, REPLAN.getController().getConnection())) && (dbQueries.entryExists(tableName, "fid", Integer.toString(fid), REPLAN.getController().getConnection()))) {
        if ((dbQueries.tableExists( UserState.userId, state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) && (dbQueries.entryExists(tableName, "fid", Integer.toString(fid), REPLAN.getController().getConnection()))) {
            // get id of this POD
            String query = "SELECT id FROM " + tableName + " WHERE fid=" + fid + ";";
            ResultSet results = stmt.executeQuery(query);
            results.next();
            int id_to_be_deleted = results.getInt("id");

            //shift all down by 1
            query = "UPDATE " + tableName + " SET id=id-1 WHERE id>" + id_to_be_deleted + ";";
            stmt.executeUpdate(query);

            // delete POD
            query = "DELETE FROM " + tableName + " WHERE fid =" + fid + ";";
            System.out.println(query);
            stmt.executeUpdate(query);


        }
    }

    public static void update_POD(int fid, String name, String address, String city, String zip, double longitude, double latitude, Boolean type, Boolean status, String comments, int numBooths) throws SQLException {
        Statement stmt = REPLAN.getController().getConnection().createStatement();
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String tableName = state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        String schema = UserState.userId;
        // check to see if table and entry exist
        DBQueriesJava dbQueries = new DBQueriesJava();
        if ((dbQueries.tableExists(schema, tableName, REPLAN.getController().getConnection())) && (dbQueries.entryExists(UserState.userId + "." + tableName, "fid", Integer.toString(fid), REPLAN.getController().getConnection()))) {
//if ((dbQueries.tableExists(tableName, REPLAN.getController().getConnection())) && (dbQueries.entryExists(tableName, "fid", Integer.toString(fid), REPLAN.getController().getConnection()))) {
            //dbQueries.tableExists(REPLAN.getController().getConnection(), UserState.userId, state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX)

            //String query = "UPDATE " + tableName + " SET name='" + name + "', address='" + address + "',city='" + city + "',zip='" + zip + "',type=" + type + ",status=" + status + ",comments='" + comments + "' WHERE fid = " + fid + ";"; // still need to update location
            //String query = "UPDATE " + tableName + " SET name='" + name +  "', address='" + address + "' WHERE fid = " + fid + ";"; // still need to update location
            String query = "UPDATE " + schema + "." + tableName + " SET location = " + " SETSRID(MAKEPOINT(" + longitude + "," + latitude + "),4326)" + ",name='" + name + "', address='" + address + "', zip='" + zip + "'" + ", city='" + city + "', type='" + type + "', status='" + status + "', comments='" + comments + "',numbooths=" + numBooths + " WHERE fid = " + fid + ";"; // still need to update location
            System.out.println(query);
            stmt.executeUpdate(query);
        } else {
            System.out.println("edu.unt.cerl.replan.controller.db.PODQueries.update_POD(...)   Table or Entry does not exist.");
        }
    }

    public static void update_POD(int fid, String field, String value) throws SQLException {
        Statement stmt = REPLAN.getController().getConnection().createStatement();
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        String schema = UserState.userId;
        // check to see if table and entry exist
        DBQueriesJava dbQueries = new DBQueriesJava();

                String query = "UPDATE " + tableName + " SET " + field + "='" + value + "' WHERE fid = " + fid + ";";
        System.out.println(query);

        if ((dbQueries.tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) && (dbQueries.entryExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, "fid", Integer.toString(fid), REPLAN.getController().getConnection()))) {
            //String query = "UPDATE " + tableName + " SET " + field + "='" + value + "' WHERE fid = " + fid + ";";
            
            System.out.println("Inside table exists");
            
            stmt.executeUpdate(query);
            
            System.out.println("Table exists: "+query+"\n");
        }

        System.out.println("edu.unt.cerl.replan.controller.db.PODQueries.update_POD(int fid, String field, String value)   Table or Entry does not exist.");

    }

    public static void update_POD(int fid, double latitude, double longitude) throws SQLException {
        Statement stmt = REPLAN.getController().getConnection().createStatement();
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        String schema = UserState.userId;
        // check to see if table and entry exist
        DBQueriesJava dbQueries = new DBQueriesJava();
        if ((dbQueries.tableExists(schema, tableName, REPLAN.getController().getConnection())) && (dbQueries.entryExists(UserState.userId + "." + tableName, "fid", Integer.toString(fid), REPLAN.getController().getConnection()))) {
            String query = "UPDATE " + schema + "." + tableName + " SET location=SETSRID(MAKEPOINT(" + latitude + "," + longitude + "),4326) WHERE fid = + " + fid + ";";
            stmt.executeUpdate(query);
        }
        System.out.println("edu.unt.cerl.replan.controller.db.PODQueries.update_POD(int fid, double latitude, double longitude)   Table or Entry does not exist.");

    }

    PODQueries() {
    }

    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map readPODsFromDB(ScenarioState state) throws SQLException {
        LinkedHashMap list = new LinkedHashMap();
        DBQueriesJava dbQueries = new DBQueriesJava();

        Statement stmt = REPLAN.getController().getConnection().createStatement();
        //ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        //String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        String schema = UserState.userId;
        String tableName = state.getName() + DefaultConstants.POD_SUFFIX;

        if (dbQueries.tableExists(schema, tableName, REPLAN.getController().getConnection())) {
            String query = "Select *,  ST_X(location), ST_Y(location) FROM " + schema + "." + tableName + ";";
            System.out.println("Query in readpodsfromdb: " + query + "\n");
            ResultSet results = stmt.executeQuery(query);
            while (results.next()) {
                int next_fid = results.getInt(1);
                int id = results.getInt("id");
                //int fid;
                String name = results.getString("name");
                String address = results.getString("address");
                String city = results.getString("city");
                String zip = results.getString("zip");
                double latitude = Double.parseDouble(results.getString("st_y"));
                double longitude = Double.parseDouble(results.getString("st_x"));
                Boolean type = Boolean.parseBoolean(results.getString("type"));
                Boolean status = Boolean.parseBoolean(results.getString("status"));
                String comments = results.getString("comments");
                int numBooths = results.getInt("numbooths");
                //System.out.println("Adding pod to podlist\n");
                list.put(next_fid, new POD(id, next_fid, name, address, city, zip, longitude, latitude, type, status, comments, numBooths));
            }
        }
//fid integer, \"location\" geometry, id integer, \"name\" character varying(255), address character varying(255),  city character varying(255),  zip character varying(255),  status character varying(255),  comments character varying(255),  onoff character varying(255),  numbooths integer,  \"type\" character varying(255))
        // select all pods from DB
        //add each pod to podlist
        // pods.put(next_fid, new POD((pods.size() + 1), next_fid, name, address, city, zip, latitude, longitude, type, status, comments, numBooths))

        return list;
    }

    //Copy PODS from working copy table in DB
    public Map readWrkCpyPODsFromDB(ScenarioState state) throws SQLException {
        LinkedHashMap list = new LinkedHashMap();
        DBQueriesJava dbQueries = new DBQueriesJava();

        Statement stmt = REPLAN.getController().getConnection().createStatement();
        //ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        //String tableName = UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        String schema = UserState.userId;
        String tableName = state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;

        if (dbQueries.tableExists(schema, tableName, REPLAN.getController().getConnection())) {
            String query = "Select *,  ST_X(location), ST_Y(location) FROM " + schema + "." + tableName + ";";
            System.out.println("Query in readpodsfromdb: " + query + "\n");
            ResultSet results = stmt.executeQuery(query);
            while (results.next()) {
                int next_fid = results.getInt(1);
                int id = results.getInt("id");
                //int fid;
                String name = results.getString("name");
                String address = results.getString("address");
                String city = results.getString("city");
                String zip = results.getString("zip");
                double latitude = Double.parseDouble(results.getString("st_y"));
                double longitude = Double.parseDouble(results.getString("st_x"));
                Boolean type = Boolean.parseBoolean(results.getString("type"));
                Boolean status = Boolean.parseBoolean(results.getString("status"));
                String comments = results.getString("comments");
                int numBooths = results.getInt("numbooths");
                System.out.println("Adding pod to podlist\n");
                list.put(next_fid, new POD(id, next_fid, name, address, city, zip, longitude, latitude, type, status, comments, numBooths));
            }
        }
//fid integer, \"location\" geometry, id integer, \"name\" character varying(255), address character varying(255),  city character varying(255),  zip character varying(255),  status character varying(255),  comments character varying(255),  onoff character varying(255),  numbooths integer,  \"type\" character varying(255))
        // select all pods from DB
        //add each pod to podlist
        // pods.put(next_fid, new POD((pods.size() + 1), next_fid, name, address, city, zip, latitude, longitude, type, status, comments, numBooths))

        return list;
    }
}
