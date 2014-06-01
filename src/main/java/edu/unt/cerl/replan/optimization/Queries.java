/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

/**
 *
 * @author tamara
 */
public class Queries {

    public static String createCentroidsFromBlocks(String centroidTable,
            String blockTable) {
        return "SELECT logrecno, centroid(the_geom) INTO public." + centroidTable
                + " FROM "
                + blockTable + ";";
    }

    public static String createBlock2PodTable(String schema, String prefix) {
        return "CREATE TABLE " + schema + "." + prefix
                + "_block_to_pod (block INTEGER);";
    }

    public static String createCatchmentTable(String schema, String prefix) {
        return "CREATE TABLE " + schema + "." + prefix
                + "_catchment (logrecno INTEGER);";
    }

    public static String fillBlocksIntoBlock2PodTable(String schema,
            String prefix, String blockTable) {
        return "INSERT INTO " + schema + "." + prefix
                + "_block_to_pod SELECT logrecno AS block FROM " + blockTable + ";";
    }

    public static String addPodColumnToB2P(String schema, String prefix) {
        return "ALTER TABLE " + schema + "." + prefix
                + "_block_to_pod ADD pod INTEGER;";
    }

    public static String createPodTable(String schema, String prefix) {
        return "SELECT (logrecno-1) AS fid, logrecno, centroid(the_geom) AS location INTO "
                + schema + "." + prefix + "_pods" + " FROM " + schema + "."
                + prefix + "_catchment;";
    }

    public static String addColumnsToPodTable(String schema, String prefix) {
        return "ALTER TABLE " + schema + "." + prefix + "_pods "
                + "ADD COLUMN name character varying(255), "
                + "ADD COLUMN address character varying(255), "
                + "ADD COLUMN city character varying(255), "
                + "ADD COLUMN zip character varying(255), "
                + "ADD COLUMN additional character varying(255), "
                + "ADD COLUMN comments character varying(255), "
                + "ADD COLUMN onoff character varying(255), "
                + "ADD COLUMN numbooths integer, "
                + "ADD COLUMN type character varying(255);";
    }
}
