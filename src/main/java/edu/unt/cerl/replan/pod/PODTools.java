/*
 * enables communication between the database and pod editor
 */
package edu.unt.cerl.replan.pod;

import edu.unt.cerl.applicationframework.controller.DBQueries;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.GISConversionTools;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class PODTools {

    private ScenarioState s;
    private GISConversionTools gisConvTools;
    private Connection c;
    private Statement stmt;
    //private static int num;

    public PODTools(ScenarioState s) throws SQLException {
        this.s = s;

        //if (connectionClosed){
        gisConvTools = new GISConversionTools();
        c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        stmt = c.createStatement();
        //connectionClosed=false;
        //}

    }

    public void closeConnection() throws SQLException {
        stmt.close();
        c.close();
        //connectionClosed=true;

    }

    public void saveChangesToPOD(Entries2 pod) throws IOException {
        // Note to self (that means you, Marty!): Need to check to see if FID exists. If exists, the POD is already in the DB and we need to UPDATE. Otherwise, we need to insert a new POD record!
        try {
            ScenarioState state = s;
            state.setPodsChanged();

            String tableName = UserState.userId + "." +  s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;


            if (!(DBQueries.tableExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, c))) { // if table does not already exist, we must create it
                //updates = "CREATE TABLE " + newTableName + " (fid integer, "location" geometry, id integer, name character varying(255), address character varying(255), city varying(255), zip integer,status varying(255), comments varying(255), numBooths integer, type varying(255), on varying(255));";
                System.out.println("The table does not yet exist, so I'll create it!!!!!!!!!!!!!!!");
                String updates = "CREATE TABLE " + tableName
                        + "(  fid integer,  "
                        + "\"location\" geometry,  "
                        + "id integer,  "
                        + "\"name\" character varying(255),  "
                        + "address character varying(255),  "
                        + "city character varying(255),  "
//                        + "zip integer,  "
                        + "zip character varying(255),  "
                        + "status character varying(255),  "
                        + "comments character varying(255),  "
                        + "onOff character varying(255),  "
                        + "numBooths integer,  "
                        + "type character varying(255),  "
                        //+ "CONSTRAINT " + newTableName + "_pkey PRIMARY KEY (fid),  "
                        + "CONSTRAINT enforce_dims_location CHECK (ndims(location) = 2),  "
                        + "CONSTRAINT enforce_geotype_location CHECK "
                        + "(geometrytype(location) = 'POINT'::text OR location IS NULL),  "
                        + "CONSTRAINT enforce_srid_location "
                        + "CHECK (srid(location) = 4326))"
                        + "WITH (  OIDS=FALSE);";

                System.out.println(updates);

                stmt.executeUpdate(updates);

                System.out.println("*********************** Now we must add the layer! *******************************");

            } // done creating table

            //gisConvTools = new GISConversionTools();
            //c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
            if (!DBQueries.entryExists("workingcpy_timestamps", "author", UserState.userId, "name", s.getWorkingCopyName(), c)) {
                REPLAN.getQueries().insertTimestamp(UserState.userId, s.getWorkingCopyName(), c);
            }
            //String tableName = UserState.getSchemaPrefix() + s.getScenarioState().getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
            int idOfRecordToAdd = Integer.parseInt(pod.readPodInfo().get("id"));
            String query = "SELECT count(id) FROM " + tableName + " WHERE id=" + idOfRecordToAdd + ";";
            System.out.println(query);
            ResultSet results = stmt.executeQuery(query);
            results.next();
            if (results.getInt("count") == 0) { // this is a new POD, and we need to INSERT
                query = "SELECT MAX(id) AS max_id, MAX(fid) AS max_fid, COUNT(fid) FROM " + tableName + ";";
                System.out.println(query);
                results = stmt.executeQuery(query);
                results.next();
                int nextFID = results.getInt("max_fid") + 1;
                if (results.getInt("count") == 0) {
                    nextFID = 0;
                }
                String updates = "INSERT INTO " + tableName + " VALUES (" + nextFID + ", ST_GeomFromText('POINT(" + pod.readPodInfo().get("lon") + " " + pod.readPodInfo().get("lat") + ")', 4326), " + idOfRecordToAdd + ", '" + pod.readPodInfo().get("name") + "', '" + pod.readPodInfo().get("addy") + "', '" + pod.readPodInfo().get("city") + "', " + pod.readPodInfo().get("zip") + ", '" + pod.readPodInfo().get("status") + "', '" + pod.readPodInfo().get("comments") + "', '" + pod.readPodInfo().get("on") + "', " + pod.readPodInfo().get("booths") + ", '" + pod.readPodInfo().get("is_public") + "'); ";
                System.out.println(updates);
                stmt.executeUpdate(updates);
            } else { // this is an existing POD and we need to UPDATE
                String updates = "UPDATE " + tableName + " SET location=ST_GeomFromText('POINT(" + pod.readPodInfo().get("lon") + " " + pod.readPodInfo().get("lat") + ")', 4326), id=" + pod.readPodInfo().get("id") + ", name='" + pod.readPodInfo().get("name") + "', address='" + pod.readPodInfo().get("addy") + "', city='" + pod.readPodInfo().get("city") + "', zip=" + pod.readPodInfo().get("zip") + ", status='" + pod.readPodInfo().get("status") + "', comments='" + pod.readPodInfo().get("comments") + "', onoff='" + pod.readPodInfo().get("on") + "', numbooths=" + pod.readPodInfo().get("booths") + ", type='" + pod.readPodInfo().get("is_public") + "' WHERE id=" + idOfRecordToAdd + ";";
                System.out.println(updates);
                stmt.executeUpdate(updates);
            }

            if (!state.arePodsSelected()) {
//                s.createPODLayer(); // if the layer doesn't already exist, we must create it.
                  REPLAN.getMainFrame().getTabs().getSelectedScenario().createLayer(s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, DefaultStyles.createPODStyle(), "PODs");
                state.setPodsSelected(true);
            }

            //c.close();

        } catch (Exception ex) {
            Logger.getLogger(PODTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("will be saving changes...");
        // need to update table by fid

//        s.reRender();
    }

    public void addNewPOD(Map pod) throws SQLException, IOException {
        ScenarioState state = s;
        state.setPodsChanged();
        System.out.println("will be adding new pod");

        int nextFID = 0;
        int nextID = 1;


        // need to do update of sql db to include pods
        String updates = "";
        //GISConversionTools gisConvTools = new GISConversionTools();
        //Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        //Statement stmt = c.createStatement();
        String newTableName = UserState.userId + "." +  s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        if (!(DBQueries.tableExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, c))) { // if table does not already exist, we must create it
            //updates = "CREATE TABLE " + newTableName + " (fid integer, "location" geometry, id integer, name character varying(255), address character varying(255), city varying(255), zip integer,status varying(255), comments varying(255), numBooths integer, type varying(255), on varying(255));";
            System.out.println("The table does not yet exist, so I'll create it!!!!!!!!!!!!!!!");
            updates = "CREATE TABLE " + newTableName
                    + "(  fid integer,  "
                    + "\"location\" geometry,  "
                    + "id integer,  "
                    + "\"name\" character varying(255),  "
                    + "address character varying(255),  "
                    + "city character varying(255),  "
                    //+ "zip integer,  "
                    + "zip character varying(255),  "
                    + "status character varying(255),  "
                    + "comments character varying(255),  "
                    + "onOff character varying(255),  "
                    + "numBooths integer,  "
                    + "type character varying(255),  "
                    //+ "CONSTRAINT " + newTableName + "_pkey PRIMARY KEY (fid),  "
                    + "CONSTRAINT enforce_dims_location CHECK (ndims(location) = 2),  "
                    + "CONSTRAINT enforce_geotype_location CHECK "
                    + "(geometrytype(location) = 'POINT'::text OR location IS NULL),  "
                    + "CONSTRAINT enforce_srid_location "
                    + "CHECK (srid(location) = 4326))"
                    + "WITH (  OIDS=FALSE);";

            System.out.println(updates);

            stmt.executeUpdate(updates);

            System.out.println("*********************** Now we must add the layer! *******************************");

        } // done creating table
        else { // If table already exists, we need to find max fid and id so we know which values to assign next.
            String query = "SELECT MAX(id) AS max_id, MAX(fid) AS max_fid FROM " + newTableName + ";";
            System.out.println(query);

            if (state.arePodsSelected()) {
                ResultSet results = stmt.executeQuery(query);
                results.next(); // we need to call this so we can get our results on the next two lines
                nextFID = results.getInt("max_fid") + 1;
                nextID = results.getInt("max_id") + 1;
                System.out.println("nextFID: " + nextFID + "  nextID: " + nextID);
            } else {
                nextFID = 0;
                nextID = 1;
            }


        }
        System.out.println("Here is where we need to insert the record!");

        // Need to change second get of fid back to id!!!!!!!!!!!!!!!!
        //updates = "INSERT INTO " + newTableName + " VALUES (" + pod.get("fid") + ", ST_GeomFromText('POINT(" + pod.get("lon") + " " + pod.get("lat") + ")', 4326), " + pod.get("fid") + ", '" + pod.get("name") + "', '" + pod.get("addy") + "', '" + pod.get("city") + "', " + pod.get("zip") + ", '" + pod.get("status") + "', '" + pod.get("comments") + "', '" + pod.get("on") + "', " + pod.get("booths") + ", '" + pod.get("is_public") + "'); ";
        updates = "INSERT INTO " + newTableName + " VALUES (" + nextFID + ", ST_GeomFromText('POINT(" + pod.get("lon") + " " + pod.get("lat") + ")', 4326), " + nextID + ", '" + pod.get("name") + "', '" + pod.get("addy") + "', '" + pod.get("city") + "', " + pod.get("zip") + ", '" + pod.get("status") + "', '" + pod.get("comments") + "', '" + pod.get("on") + "', " + pod.get("booths") + ", '" + pod.get("is_public") + "'); ";
        System.out.println(updates);

        stmt.executeUpdate(updates);


        if (!state.arePodsSelected()) {
//            s.createPODLayer(); // if the layer doesn't already exist, we must create it.
            try {
                REPLAN.getMainFrame().getTabs().getSelectedScenario().createLayer(s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, DefaultStyles.createPODStyle(), "PODs");
            } catch (Exception ex) {
                Logger.getLogger(PODTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setPodsSelected(true);
        }
        // create table if does not already exist
        //s.getScenarioState().setPodsSelected(true);
        // s.reRender(); // for refreshing after change

        //stmt.close();
        //c.close();
    }

    public void removePOD(int fid) throws SQLException {
        //String fid = pod.get("fid");
//fid++; // Decently Large Hack!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!********************

        int currentId;
        System.out.println("### will be removing POD..." + fid);

        String query = "";
        String sqlDelete = "";
        String sqlUpdate = "";
        ScenarioState state = s;

        state.setPodsChanged();

        if (state.arePodsSelected()) { // do nothing if there are no pods
            // GISConversionTools gisConvTools = new GISConversionTools();
            // Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
            // Statement stmt = c.createStatement();

            query = "SELECT id FROM " + UserState.userId + "." +  s.getWorkingCopyName()
                    + DefaultConstants.POD_SUFFIX+ " WHERE FID = " + fid + ";";
            System.out.println(query);
            ResultSet results = stmt.executeQuery(query);
            results.next();
            currentId = results.getInt("id");
            System.out.println("Query returned id=" + currentId);

            sqlDelete = "DELETE FROM " + UserState.userId + "." +  s.getWorkingCopyName()
                    + DefaultConstants.POD_SUFFIX + " WHERE FID = " + fid + ";";
            System.out.println(sqlDelete);
            stmt.executeUpdate(sqlDelete);

            sqlUpdate = "UPDATE " + UserState.userId + "." +  s.getWorkingCopyName()
                    + DefaultConstants.POD_SUFFIX + " SET id=id-1 WHERE id>" + currentId;
            System.out.println(sqlUpdate);

            stmt.executeUpdate(sqlUpdate);

            query = "SELECT count(id) FROM " + UserState.userId + "." +  s.getWorkingCopyName()
                    + DefaultConstants.POD_SUFFIX + ";";
            System.out.println(query);
            results = stmt.executeQuery(query);
            results.next();
            if (results.getInt("count") < 1) {
                state.setPodsSelected(false);
            }
        } else {
            System.out.println("There are no pods to remove!");
        }
        //if table ends up empty:
        //s.getScenarioState().setPodsSelected(false);
        // s.reRender(); // for refreshing after change

        String tableName = UserState.userId + "." +  s.getWorkingCopyName() +
                DefaultConstants.POD_SUFFIX;




    }
}
