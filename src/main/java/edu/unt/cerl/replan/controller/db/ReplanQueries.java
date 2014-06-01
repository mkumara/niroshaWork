//Copied from old CVS by Sarat
package edu.unt.cerl.replan.controller.db;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ReplanQueries
 */
public class ReplanQueries {

    //
    // Fields
    //
    //
    // Constructors
    //
    public ReplanQueries() {
    }

    ;

    //
    // Methods
    //
    /**
     * This method queries the database for a list of RE-PLAN users with user
     * name and user id.
     * @param c JDBC databse connection
     * @return array of user(id) entries
     */
    protected String[] getUsersFromDB(Connection c) {

        String[] users = new String[0];
        try {
            Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(query);
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            users = new String[size];
            for (int i = 0; i < size; i++) {
                rs.next();
                String id = rs.getString("id");
                String name = rs.getString("name");
                users[i] = name + " (" + id + ")";
            }
            //c.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }
        return users;
    }

    protected void insertTimestamp(String author, String scenarioPrefix,
            Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "INSERT INTO workingcpy_timestamps VALUES ('"
                    + author + "','" + scenarioPrefix + "');";
            System.out.println(query);
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            System.err.println("error inserting timestamps");
        }
    }

    protected void saveScenarioInformation(ScenarioState state, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "INSERT INTO scenarios VALUES('" + UserState.userId
                    + "','" + state.getName() + "','" + state.getDescription()
                    + "','" + state.arePodsSelected() + "','" + state.isTrafficAnalysisPerformed() + "','" + state.didPodsChange() + "'," + state.getPeoplePerCar() + "," + state.getTimeInterval() + "," + state.getTimeFrame() + "," + state.getDayOfWeek() + "," + state.getBaseTraffic() + "," + state.getPodTraffic() + ", " + state.getTimePerIndividual() + "," + state.getCatchmentAreasGiven() + ");";
            System.out.println(query + "\n");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    protected void updateScenarioInformation(ScenarioState state, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "UPDATE scenarios SET author = '" + UserState.userId + "', name = '"
                    + state.getName() + "', description = '" + state.getDescription()
                    + "', podsselected = '" + state.arePodsSelected() + "', replanexecuted = '"
                    + state.isTrafficAnalysisPerformed() + "', pods_changed = '" + state.didPodsChange()
                    + "', people_per_car = '" + state.getPeoplePerCar() + "', time_interval = '"
                    + state.getTimeInterval() + "', timeframe = '" + state.getTimeFrame()
                    + "', day = '" + state.getDayOfWeek() + "', base_traffic = '"
                    + state.getBaseTraffic() + "', pod_traffic = '" + state.getPodTraffic() + "', timeperind = '" + state.getTimePerIndividual() + "', catchmentareasgiven = '" + state.getCatchmentAreasGiven()
                    + "' WHERE author = '" + UserState.userId + "' AND name = '" + state.getName() + "';";
            System.out.println(query + "\n");
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

    }

    protected List getScenarioInfo(String schema, String name, Connection c) {
        List values = new LinkedList();
        try {

            Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM public.scenarios WHERE author = '"
                    + schema + "' AND name = '" + name + "';";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();

            values.add(UserState.userId); // author of scenario
            values.add(name); // name of scenario
            values.add(rs.getString("description")); // scenario description
            values.add(rs.getBoolean("podsselected"));
            values.add(rs.getBoolean("replanexecuted"));
            values.add(rs.getBoolean("pods_changed"));
            values.add(rs.getInt("people_per_car"));
            values.add(rs.getInt("time_interval"));
            values.add(rs.getInt("timeframe"));
            values.add(rs.getInt("day"));
            values.add(rs.getBoolean("base_traffic"));
            values.add(rs.getBoolean("pod_traffic"));
            values.add(rs.getInt("timeperind"));
            values.add(rs.getBoolean("catchmentareasgiven"));

            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return values;
    }

    String[] getScenariosForUser(String userId, Connection c) {
        String[] scenarios = new String[0];
        try {
            Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM scenarios WHERE author = '" + userId
                    + "';";
            ResultSet rs = stmt.executeQuery(query);
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            scenarios = new String[size];
            for (int i = 0; i < size; i++) {
                rs.next();
                scenarios[i] = rs.getString("name");
            }
            //c.close();

        } catch (SQLException ex) {
            System.err.println("Error retrieving users from DB");
        }
        return scenarios;
    }

    Map<String, String> getDescriptions(String userId, Connection c) {
        Map descriptions = new HashMap<String, String>();
        try {

            Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT name, description FROM scenarios WHERE author = '"
                    + userId + "';";
            ResultSet rs = stmt.executeQuery(query);
            rs.beforeFirst();
            while (rs.next()) {
                descriptions.put(rs.getString("name"), rs.getString(
                        "description"));
            }
            //c.close();

        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }
        return descriptions;
    }

    void createGeographyTable(String id, String name, String[] strings,
            Connection c) {
        try {
            String tableName = id + "." + name;
            Statement stmt = c.createStatement();
            String query = "CREATE TABLE " + tableName
                    + "(geography VARCHAR(50));";
//            String query =
//                    "CREATE TABLE " + tableName + " ("
//                    + DefaultConstants.OUTLINE_TABLE.toLowerCase()
//                    + " VARCHAR(50),"
//                    + DefaultConstants.BLOCK_TABLE.toLowerCase()
//                    + " VARCHAR(50),"
//                    + DefaultConstants.CENTROID_TABLE.toLowerCase()
//                    + " VARCHAR(50),"
//                    + DefaultConstants.ROAD_TABLE.toLowerCase()
//                    + " VARCHAR(50),"
//                    + DefaultConstants.POPULATION_TABLE.toLowerCase()
//                    + " VARCHAR(50));";
            stmt.executeUpdate(query);
            for (int i = 0; i < strings.length; i++) {
//                query = "INSERT INTO " + tableName + " VALUES('"
//                        + map.get(strings[i]).get(DefaultConstants.OUTLINE_TABLE)
//                        + "','"
//                        + map.get(strings[i]).get(DefaultConstants.BLOCK_TABLE)
//                        + "','"
//                        + map.get(strings[i]).get(
//                        DefaultConstants.CENTROID_TABLE) + "','"
//                        + map.get(strings[i]).get(DefaultConstants.ROAD_TABLE)
//                        + "','"
//                        + map.get(strings[i]).get(
//                        DefaultConstants.POPULATION_TABLE)
//                        + "');";
                query = "INSERT INTO " + tableName + " VALUES('" + strings[i]
                        + "" + "');";
                System.out.println(query);
                stmt.executeUpdate(query);
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GeneralDBQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    void createCliffedGeographyTable(String id, String name, String[] strings,
            Connection c) {
        try {
            String tableName = id + "." + name;
            Statement stmt = c.createStatement();
            String query = "CREATE TABLE " + tableName
                    + "(geography VARCHAR(50),x1 float8, y1 float8, x2 float8, y2 float8 );";
//           
            stmt.executeUpdate(query);
            for (int i = 0; i < strings.length; i++) {
//               
                query = "INSERT INTO " + tableName + " VALUES('" + strings[i]
                        + "" + "');";
                System.out.println(query);
                stmt.executeUpdate(query);
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GeneralDBQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        

    }

    protected String[] getGeographies(String userId, String scenario,
            Connection c) {
        List<String> l = new LinkedList();
        try {
            Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + userId + "." + scenario
                    + "_geographies";
            System.out.println("Query in ReplanQueries.getGeography: " + query + "\n");
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                l.add(rs.getString("geography"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return l.toArray(new String[l.size()]);
    }

    void removeOldWorkingCopies(Connection c) {
        try {
            Statement stmt = c.createStatement();
            Statement stmt2 = c.createStatement();
            String expiration = REPLAN.getTables().get("discard_after");
            String query = " SELECT author,name FROM workingcpy_timestamps WHERE "
                    + "((EXTRACT (DAY from(now()-creation_time))*24)"
                    + "+(EXTRACT(HOUR from(now()-creation_time)))) > "
                    + expiration + ";";
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(query);
            while (rs.next()) {
                String author = rs.getString("author");
                String name_prefix = rs.getString("name");
                deleteScenario(author, name_prefix, c);
                System.out.println("deleting scenario " + author + "." + "name_prefix" + "\n");
                query = "DELETE FROM workingcpy_timestamps "
                        + " WHERE author = '" + author + "' " + "AND name = '"
                        + name_prefix + "';";
                System.out.println(query);
                stmt2.executeUpdate(query);
            }
            rs.close();
            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }
    /*
    protected void removeWorkingcopyEntry(String author, String name, Connection c) {
    try {
    Statement stmt = c.createStatement();
    String query =
    "DELETE FROM workingcpy_timestamps " + " WHERE author = '"
    + author + "' " + "AND name = '" + name + "';";
    System.out.println(query);
    } catch (SQLException ex) {
    Logger.getLogger(ReplanQueries.class.getName()).
    log(Level.SEVERE, null, ex);
    }
    }
     */

    protected void deleteScenario(String author, String name, Connection c) {
        String[] tables = {name + DefaultConstants.GEOGRAPHIES_SUFFIX,
            name + DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX,
            name + DefaultConstants.POD_SUFFIX,
            name + DefaultConstants.CATCHMENT_SUFFIX,
            name + DefaultConstants.B2P_SUFFIX,
            name + DefaultConstants.COVERAGE_SUFFIX,
            name + DefaultConstants.RINGS_SUFFIX,
            name + DefaultConstants.CROSSINGPT_SUFFIX,
            name + DefaultConstants.BLOCK_SUFFIX,
            name + DefaultConstants.OUTLINE_SUFFIX,
            name + DefaultConstants.CENTROID_SUFFIX,
            name + DefaultConstants.ROAD_SUFFIX,
            name + DefaultConstants.POPULATION_SUFFIX,
            name + DefaultConstants.RINGS_SUFFIX
        };

        for (int i = 0; i < tables.length; i++) {
            if (REPLAN.getQueries().tableExists(author, tables[i], c)) {
                REPLAN.getQueries().dropTable(author, tables[i], c);
            }
        }

        //Drop author, name row from public.scenario
        try {
            Statement stmt = c.createStatement();
            String query = "DELETE FROM scenarios " + " WHERE author = '"
                    + author + "' " + "AND name = '" + name + "';";
            stmt.executeUpdate(query);
            System.out.println(query);
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    void removeTimestamp(String schema, String scenario, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "DELETE FROM public.workingcpy_timestamps WHERE author = '" + schema + "' AND name = '" + scenario + "';";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }
}
