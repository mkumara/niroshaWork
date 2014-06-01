package edu.unt.cerl.replan.controller.db;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.UserState;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Class DBController
 */
public class DBController {

    //
    // Fields
    //
    private Connection c = null;
    private String cString = null;
    private Map<String, String> settings = null;
    private Map<String, String> postgisParams = null;
    private Properties jdbcProperties = null;
    private Map<String, String> postgis_userschema_params = null;
    private Properties jdbc_userschema_properties = null;

    //
    // Constructors
    //
    public DBController() {
        this.readParams();
        this.initDBParams();
    }

    //
    // Methods
    //
    //
    // Accessor methods
    //
    /**
     * Returns a connection object and connects to DB if necessary.
     * @return connection to the DB
     */
    public Connection getConnection() {
        try {
            // If connection is null, then it needs to be established
            if (c == null || c.isClosed()) {
                this.connectToDB();
            }

        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Error checking for closed connection", ex);

        }
        return c;
    }

    //
    // Other methods
    //
    /**
     * Connect to the DB. If settings have not been read from the settings file,
     * then call methods to read settings.
     */
    public void connectToDB() {
        if (settings == null) {
            this.readParams();
            this.initDBParams();
        }
        try {
            c =
                    DriverManager.getConnection(this.cString,
                    this.jdbcProperties);
        } catch (SQLException ex) {
            Logger.getLogger(DBController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     */
    public void closeConnection() {
        try {
            if (this.c != null && !this.c.isClosed()) {
                c.close();
            }
        } catch (SQLException ex) {
            System.err.println("Error closing connection");
            Logger.getLogger(DBController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void setSchema() {
        this.jdbc_userschema_properties =
                (Properties) this.jdbcProperties.clone();
        this.jdbc_userschema_properties.put("schema", UserState.userId);
        this.postgis_userschema_params = (Map<String, String>) ((HashMap) this.postgisParams).
                clone();
        this.postgis_userschema_params.put("schema", UserState.userId);

    }

    /**
     * This method reads the DB parameters from a settings file
     */
    private void readParams() {
        try {
            //InputStream is = DBController.class.getResourceAsStream("DatabaseSettings.txt");
            File file = new File("DatabaseSettings.txt");
        //URL urldb = DBController.class.getResource("/DatabaseSettings.txt");
        //System.out.println("DBController databasesettings path: "+urldb.getPath());
            //Scanner s = new Scanner(DBController.class.getResourceAsStream("/DatabaseSettings.txt"));
                    Scanner s = new Scanner(file);
            settings = new HashMap<String, String>();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("%")) {
                    continue;
                }
                line = line.replaceAll("\\s", "");
                if (line.length() == 0) {
                    continue;
                }
                String[] tokens = line.split("=");
                settings.put(tokens[0], tokens[1]);
            }
        } catch (FileNotFoundException ex) {
            REPLAN.logger.log(Level.SEVERE, "DatabaseSettings.txt not found", ex);
        }
    }

    /**
     */
    private void initDBParams() {
        postgisParams = new HashMap<String, String>();
        jdbcProperties = new Properties();

        // create connection string
        this.cString = "jdbc:postgresql://" + settings.get("host") + ":"
                + settings.get("port") + "/" + settings.get("database");

        // must be postgis
        postgisParams.put("dbtype", "postgis");

        // the name or ip address of the machine running PostGIS
        postgisParams.put("host", settings.get("host"));

        // check for empty field as indication of non-existing settings file
        if (settings.get("port") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL PORT ERROR: Make sure DatabaseSettings.txt is in"
                    + " same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        if (settings.get("user") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL USER ERROR: Make sure DatabaseSettings.txt is in"
                    + " same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        if (settings.get("password") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL PASSWORD ERROR: Make sure DatabaseSettings.txt is in"
                    + " same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        if (settings.get("role") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL ROLE ERROR: Make sure DatabaseSettings.txt is"
                    + " in same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }

        // fill postgis parameters
        postgisParams.put(("role"), settings.get("role"));
        postgisParams.put("user", settings.get("user"));
        postgisParams.put("passwd", settings.get("password"));
        postgisParams.put("port", settings.get("port"));
        postgisParams.put("database", settings.get("database"));
        System.out.println(postgisParams.get("dbtype"));
        System.out.println(postgisParams.get("host"));
        System.out.println(postgisParams.get("port"));
        System.out.println(postgisParams.get("database"));
        System.out.println(postgisParams.get("user"));
        System.out.println(postgisParams.get("passwd"));
        System.out.println(postgisParams.get("role"));
       

        // fill jdbc properties
        jdbcProperties.put("user", settings.get("user"));
        jdbcProperties.put("password", settings.get("password"));
        jdbcProperties.put("role", settings.get("role"));
    }

    public Map getPostGIS() {
        return this.postgisParams;
    }
}
