// **********************************************
// **********************************************
// **********************************************
// **********************************************
// **********************************************
// **********************************************
// **********************************************

// NOTE: This class is going away

// **********************************************
// **********************************************
// **********************************************
// **********************************************
// **********************************************
// **********************************************
// **********************************************

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller;

import edu.unt.cerl.replan.controller.db.PODQueries;
import edu.unt.cerl.replan.model.POD;
import edu.unt.cerl.replan.model.PODList;
import edu.unt.cerl.replan.model.ScenarioState;
import java.sql.SQLException;
import java.util.Observable;

/**
 * This class contains methods for updating the list of PODs
 * @author martyo
 */
public class PODList_Controller extends Observable {

    private PODList pods;

    public PODList_Controller() {
        pods = new PODList();
    }

    public void add_POD(ScenarioState state, String name, String address, String city, String zip, double longitude, double latitude, Boolean type, Boolean status, String comments, int numBooths) throws SQLException {
        // add POD to local data structure
        pods.add_pod(state, name, address, city, zip, longitude, latitude, type, status, comments, numBooths);

        // add pod to database
        PODQueries.addNewPOD(state, name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
    }

    public void delete_POD(int fid) throws SQLException {
        // delete POD in local data structure
        pods.delete_pod(fid); //deletes POD from PODList, shifts id numbers down in PODList, returns deleted POD

        // delete POD from database
        PODQueries.delete_POD(fid);

    }

    public POD get_POD(int fid) {
        return pods.access_POD(fid);
    }

// for updating everything:
    public void update_POD(int fid, String name, String address, String city, String zip, double longitude, double latitude, Boolean type, Boolean status, String comments, int numBooths) throws SQLException {
        ((POD) pods.access_POD(fid)).update_POD(name, address, city, zip, longitude, latitude, type, status, comments, numBooths);

        PODQueries.update_POD(fid, name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
    }

// for updating individual POD attributes...
    public void update_POD_name(int fid, String name) throws SQLException {
        ((POD) pods.access_POD(fid)).set_name(name);

        PODQueries.update_POD(fid, "name", name);
    }

    public void update_POD_address(int fid, String address) throws SQLException {
        ((POD) pods.access_POD(fid)).set_address(address);

        PODQueries.update_POD(fid, "address", address);
    }

    public void update_POD_city(int fid, String city) throws SQLException {
        ((POD) pods.access_POD(fid)).set_city(city);

        PODQueries.update_POD(fid, "city", city);
    }

    public void update_POD_zip(int fid, String zip) throws SQLException {
        ((POD) pods.access_POD(fid)).set_zip(zip);

        PODQueries.update_POD(fid, "zip", zip);
    }

    public void update_POD_location(int fid, double longitude, double latitude) throws SQLException {
        ((POD) pods.access_POD(fid)).set_longitude(longitude);
        ((POD) pods.access_POD(fid)).set_latitude(latitude);

        PODQueries.update_POD(fid, longitude, latitude);
    }

    public void update_POD_status(int fid, Boolean status) throws SQLException {
        ((POD) pods.access_POD(fid)).set_status(status);

        PODQueries.update_POD(fid, "status", new Boolean(status).toString());
    }

    public void update_POD_type(int fid, Boolean type) throws SQLException {
        ((POD) pods.access_POD(fid)).set_type(type);

        PODQueries.update_POD(fid, "type", new Boolean(type).toString());
    }

    public void update_POD_comments(int fid, String comments) throws SQLException {
        ((POD) pods.access_POD(fid)).set_comments(comments);

        PODQueries.update_POD(fid, "comments", comments);
    }

    public void update_POD_numBooths(int fid, int numBooths) throws SQLException {
        ((POD) pods.access_POD(fid)).set_numBooths(numBooths);

        PODQueries.update_POD(fid, "numBooths", Integer.toString(numBooths));
    }
}
