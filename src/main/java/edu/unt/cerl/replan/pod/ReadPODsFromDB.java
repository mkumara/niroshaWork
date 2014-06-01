/*
 This class is only here to help Angel figure out how to access POD info from DB. We can get rid of it later!!!
 */
package edu.unt.cerl.replan.pod;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.PODList;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.PODEditor_View;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author martyo
 */
public class ReadPODsFromDB {

//    private Map<Integer, Entries2> podTableHash = new HashMap<Integer, Entries2>();
//    private Map<String, String> entryMap;// = new HashMap<String,String>();
    PODEditor_View podEditor;
    private PODList podList;
    private int maxFID;
/*
    public static void main(String args[]) throws SQLException{
        ReadPODsFromDB obj = new ReadPODsFromDB();
    }
*/
    public ReadPODsFromDB( ScenarioState activeScenario ) throws SQLException {
        ScenarioPanel scenarioPanel = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        System.out.println("ReadPODsFromDB 36");

        String podTable = UserState.userId + "." + activeScenario.getWorkingCopyName() + DefaultConstants.POD_SUFFIX;
        System.out.println("Name of current table and scenario: " + podTable);

        podEditor = REPLAN.getMainFrame().getTabs().getSelectedScenario().getPODEditor_View();
        System.out.println("ReadPODsFromDB 42");

        Connection c = REPLAN.getController().getConnection();
        Statement stmt = c.createStatement();
        ResultSet results;
        
        String query = "SELECT MAX(fid) FROM " + podTable + ";";
        System.out.println(query);
        results = stmt.executeQuery(query);
        results.next();
        maxFID = results.getInt("max");

        System.out.println("maxFID: " + maxFID);
        System.out.println("ReadPODsFromDB 55");


        query = "SELECT *, ST_X(location), ST_Y(location) FROM " + podTable + " ORDER BY id;";
        System.out.println(query);
        results = stmt.executeQuery(query);

        System.out.println("ReadPODsFromDB 62");

        //podTableHash = new HashMap<Integer, Entries2>();
        podList = new PODList();
        while(results.next()) {
            int fid = results.getInt("fid");
            int id = results.getInt("id");
            //int fid;
            String name = results.getString("name");
            String address = results.getString("address");
            String city = results.getString("city");
            String zip = results.getString("zip");
            double longitude = Double.parseDouble(results.getString("st_x"));
            double latitude = Double.parseDouble(results.getString("st_y"));
            Boolean type = Boolean.parseBoolean(results.getString("type"));
            if( results.getString("type").equalsIgnoreCase("public") )
            {
                type = true;
            }
            Boolean status = Boolean.parseBoolean(results.getString("status"));
            String comments = results.getString("comments");
            int numBooths = results.getInt("numbooths");
//            String onoff = results.getString("onoff");
            System.out.println( "ReadPODsFromDB 81: " +
                    fid + " " + id + " " + longitude + " " + latitude
                    + " " + name + " "  + address + " " + city + " " + zip +
                    " status " + results.getString("status") + " " + status +
                    " " + comments + " " + numBooths + " type " +
                    results.getString("type") + " " + type );

            podList.update_pod_dontNotify( fid, id, ("Uniform POD " + id), address,
                    city, zip, longitude, latitude, type, status, comments, numBooths );
        }
        System.out.println("ReadPODsFromDB 95");
        
        podList.set_next_fid( maxFID + 1 );
        System.out.println( "ReadPODsFromDB 98 podList.size() = " + podList.get_number_of_pods() );
        podEditor.setPodList( podList );
        scenarioPanel.setPodList( podList );
        System.out.println( "ReadPODsFromDB 100 podList.size() = " + podEditor.getPodList().get_number_of_pods() );
        System.out.println( "ReadPODsFromDB 101 scenarioPanel = " + scenarioPanel );
        podEditor.displayPodList();
        podEditor.getPodList().setListChanged();
        System.out.println("ReadPODsFromDB calling notifyObservers");
        podEditor.getPodList().notifyObservers();
        REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setPodsChanged();
        REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setChangedAndNotify();
        System.out.println("ReadPODsFromDB 107");
        scenarioPanel.setPODEditor_View( podEditor );
    }

/*
    public Map<Integer, Entries2> getMap(){
        return podTableHash;

    }

    public int getMaxFID(){
        return maxFID;
    }
*/

}
