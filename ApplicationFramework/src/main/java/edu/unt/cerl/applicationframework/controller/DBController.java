
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package edu.unt.cerl.applicationframework.controller;

import edu.unt.cerl.applicationframework.model.DBInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class DBController {

    private static Connection c = null;
    private static Map<String,String> postgis_params = null;
    private static String connection = null;
    private static Properties jdbc_properties = null;

    private static void initDBInfo() {
        DBInfo.setDBInfo(ReadDBSettings.readParams());
    }

    /**
     * Initialize the DB parameters and create a connection String. This is
     * necessary to set up the connection parameters of this class
     */
    public static void initDBParams() {
        if (DBInfo.getParams() == null) {
            initDBInfo();
        }
        Map<String, String> m = DBInfo.getParams();
        connection = "jdbc:postgresql://" + m.get("host") + ":" + m.get(
                "port") + "/" + m.get("database");
        postgis_params = new HashMap<String,String>();
        postgis_params.put("dbtype", "postgis");      //must be postgis
        postgis_params.put("host", m.get("host"));   //the name or ip address of the machine running PostGIS
        if (m.get("port") == null || m.get("user") == null || m.get("password")
                == null || m.get("role") == null) {
        }
        postgis_params.put("port", new Integer(m.get("port")).toString());  //the port that PostGIS is running on (generally 5432)
        postgis_params.put("database", m.get("database"));      //the name of the database to connect to.
        postgis_params.put("user", m.get("user"));         //the user to connect with
        postgis_params.put("passwd", m.get("password"));   //the password of the user.
        postgis_params.put(("role"), m.get("role"));
        jdbc_properties = new Properties();
        jdbc_properties.put("user", m.get("user"));
        jdbc_properties.put("password", m.get("password"));
        jdbc_properties.put("role", m.get("role"));
    }

    /**
     * Creates a database connection based on the connection String
     * @return database connection
     */
    public static Connection connectToDB() {
        try {
            c = DriverManager.getConnection(connection, jdbc_properties);
        } catch (SQLException ex) {
            Logger.getLogger(DBController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return c;
    }

    /**
     * Close the database connection. Database connections should not be left
     * open. Creating too many different connections may cause leaks.
     */
    public static void closeConnection() {
        try {
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public static Map<String,String> getPostgisParams(){
        return postgis_params;
    }
}
