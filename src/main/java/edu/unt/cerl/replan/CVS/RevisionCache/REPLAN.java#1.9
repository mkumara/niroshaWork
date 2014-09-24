package edu.unt.cerl.replan;


import edu.unt.cerl.applicationframework.controller.ReadDBSettings;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.controller.PODList_Controller;
import edu.unt.cerl.replan.controller.db.DBController;
import edu.unt.cerl.replan.controller.db.DBQueries;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.view.windows.UserSelectionFrame;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;

/**
 * This is the main class of RE-PLAN
 * It initializes the database, updates the system state with the selected user
 * and opens RE-PLAN's main window.
 * 
 * @author Tamara Schneider (tschneider {at} unt.edu)
 * @version 2.0
 */
public class REPLAN {

    private static final String tableSettingsPath = "Settings.txt";
    private static DBQueries q; // gives access to all DB queries supported
    private static DBController db; // provides a database connection
    private static MainFrame mf; // main window of RE-PLAN
    private static Map<String, String> tableSettings; // map to hold table names
    private static List<String> datasetNames; // contains names of datasets
    private static List<Map> mapList; // contains table settings & dataset maps
    private static Map<String,Map> datasetMap;
    /**
     * Holds logger to log error messages and exceptions
     * Example usage:
     * - Logger.log(Level.SEVERE, "Log message");
     * - Logger.log(Level.SEVERE, null, exception);
     *  - Logger.log(Level.SEVERE, "Log message", exception);
     * Details
     * - other sets of parameters are available
     * - log message can be null
     * - other levels than SEVERE are available
     */
    public static final Logger logger = Logger.getLogger("RE-PLAN.log");
    public static boolean GeocodingSessionAvailable = true;
    
    // For Debugging - Display output in terminal/IDE
    private static final boolean ALLPRINTING = true;            // turn on/off ALL printing
        // different levels of priority printing
    private static final boolean ProductionPrinting = true;     // litte to no print statments 
    private static final boolean EnvironmentPrinting = true;    // print statments to describe session
    private static final boolean DevelopmentPrinting = true;    // printing for testing/implementing features & changes
    public enum PrintType {PRODUCTION, ENVIRONMENT, DEVELOPMENT};
    
    
    /**
     * Constructor: controls user selection, cleans up corrupted DB entries,
     * creates a new instance of the RE-PLAN main window
     */
    public REPLAN() {
        // Set up logger
        REPLAN.setUpLogger();

        // Create DB controller and query object
        db = new DBController();
        q = new DBQueriesJava();
        //    tableSettings = ReadDBSettings.readParams(tableSettingsPath);
        mapList = ReadDBSettings.readParamsWithDatasets("Settings2.txt");
        datasetNames = new LinkedList();
        datasetMap = new HashMap<String,Map>();
        Iterator<Map> it = mapList.iterator();
        while (it.hasNext()) {
            Map<String, String> m = it.next();
            System.out.println("type = " + m.get(ReadDBSettings.TYPE));
            if (m.get(ReadDBSettings.TYPE).equals(ReadDBSettings.SETTINGS)) {
                tableSettings = m;
            } else if(m.get(ReadDBSettings.TYPE).equals(ReadDBSettings.DATASET)){
                datasetNames.add(m.get(DefaultConstants.NAME));
                datasetMap.put(m.get(DefaultConstants.NAME),m);
            }
        }

        mf = new MainFrame();
        mf.setVisible(false);

        // Remove corrupted old working copies
        q.removeOldWorkingCopies(db.getConnection());

        // Show window for user selection
        UserSelectionFrame usf = new UserSelectionFrame();
        usf.setVisible(true);
        usf.setLocationRelativeTo(mf);

    }
/*
    ProductionPrinting = true;     // litte to no print statments 
    private static final boolean EnvironmentPrinting = true;    // print statments to describe session
    private static final boolean DevelopmentPrinting = true;    // printing for testing/implementing features & changes
    public enum DebugLevel {PRODUCTION, ENVIRONMENT, DEVELOPMENT};
    */
    public static void print(PrintType type, String message){
        boolean allowPrint = false;
        if (ALLPRINTING) {
            if (ProductionPrinting && type.equals(PrintType.PRODUCTION)) allowPrint = true;
            if (EnvironmentPrinting && type.equals(PrintType.ENVIRONMENT)) allowPrint = true;
            if (DevelopmentPrinting && type.equals(PrintType.DEVELOPMENT)) allowPrint = true;
        }
        if (allowPrint) System.out.println(message);
    }
    
    public static void userSelected() {
        // Set the user schema for the database now that user has been selected
        db.setSchema();

        // activate main frame
        mf.setVisible(true);
        mf.toFront();
    }

    /**
     * Main method of the RE-PLAN framework
     * @param args command line arguments (currently not used)
     */
    public static void main(String args[]) {
        new REPLAN();

      
    }

    /**
     * Accessor method for DBQueries
     * @return database queries
     */
    public static DBQueries getQueries() {
        return REPLAN.q;
    }

    /**
     * Accessor method for DBController
     * @return database controller
     */
    public static DBController getController() {
        return REPLAN.db;
    }

    /**
     * Returns RE-PLAN's main frame.
     * @return the main frame
     */
    public static MainFrame getMainFrame() {
        return REPLAN.mf;
    }

    public static Map<String, String> getTables() {
        return REPLAN.tableSettings;
    }

    public static List<Map> getSettingsAndDatasets(){
        return REPLAN.mapList;
    }

    public static List<String> getDatasetNames(){
        return REPLAN.datasetNames;
    }

    /**
     * This method configures the logger with handler and formatter
     */
    private static void setUpLogger() {
        FileHandler fh;
        try {
            fh = new FileHandler("replan.log", true);
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException ex) {
            REPLAN.displayLoggerError();
        } catch (SecurityException ex) {
            Logger.getLogger(REPLAN.class.getName()).log(Level.SEVERE, null, ex);
            REPLAN.displayLoggerError();
        }
    }

    /**
     * Displays and error if log could not be created
     */
    private static void displayLoggerError() {
        JOptionPane.showMessageDialog(null, "Error creating log file", "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static Map<String,Map> getDatasets(){
        return REPLAN.datasetMap;
    }
}
