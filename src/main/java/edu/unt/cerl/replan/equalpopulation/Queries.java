/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.equalpopulation;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.model.UserState;
/**
 *
 * @author tamara
 */
public class Queries {

    public static String createCentroidsFromBlocks(String centroidTable,
            String blockTable) {
        return "SELECT logrecno, centroid(the_geom) INTO " + UserState.userId + "."+centroidTable +
                " FROM " + UserState.userId + "." + blockTable + ";";
    }

    public static String createBlock2PodTable(String schema, String prefix) {
        return "CREATE TABLE " + schema + "." + prefix + DefaultConstants.B2P_SUFFIX +
                " (block INTEGER);";
    }

    public static String createCatchmentTable(String schema, String prefix) {
        return "CREATE TABLE " + schema + "." + prefix +
                "_catchment (id INTEGER);";
    }

    public static String fillBlocksIntoBlock2PodTable(String schema,
            String prefix, String blockTable) {
        return "INSERT INTO " + schema + "." + prefix + DefaultConstants.B2P_SUFFIX +
                " SELECT logrecno AS block FROM " + schema + "." + blockTable + ";";
    }

    public static String addPodColumnToB2P(String schema, String prefix) {
        return "ALTER TABLE " + schema + "." + prefix + DefaultConstants.B2P_SUFFIX +
                " ADD pod INTEGER;";
    }

    public static String createPodTable(String schema, String prefix) {
        return "SELECT (id-1) AS fid, centroid(the_geom) AS location, id INTO " +
                schema + "." + prefix + DefaultConstants.POD_SUFFIX + " FROM " + schema + "." +
                prefix + "_catchment;";
    }

    public static String addColumnsToPodTable(String schema, String prefix) {
        return "ALTER TABLE " + schema + "." + prefix + DefaultConstants.POD_SUFFIX + " " +
                "ADD COLUMN name character varying(255), " +
                "ADD COLUMN address character varying(255), " +
                "ADD COLUMN city character varying(255), " +
                "ADD COLUMN zip character varying(255), " +
                "ADD COLUMN status character varying(255), " +
                "ADD COLUMN comments character varying(255), " +
                "ADD COLUMN onoff character varying(255), " +
                "ADD COLUMN numbooths integer, " +
                "ADD COLUMN type character varying(255);";
    }

}
