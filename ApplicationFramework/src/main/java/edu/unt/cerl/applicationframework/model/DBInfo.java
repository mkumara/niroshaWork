
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package edu.unt.cerl.applicationframework.model;

/**
 * Stores connection parameters for the database
 * @author tamara
 */
import java.util.Map;

public class DBInfo {

    private static Map<String, String> settings = null;

    /*
     * Assign settings
     */
    public static void setDBInfo(Map settings) {
        DBInfo.settings = settings;
    }

    /*
     * Retrieve settings
     */
    public static Map<String, String> getParams() {
        return settings;
    }
}
