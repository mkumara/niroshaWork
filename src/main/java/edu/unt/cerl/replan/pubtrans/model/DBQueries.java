/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pubtrans.model;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martyo
 */
public class DBQueries {

    /**
     * Used only for testing purposes of methods in this class!
     * @param args
     * @throws SQLException
     */
    public static void main(String args[]) throws SQLException {
        REPLAN replan = new REPLAN();


        LinkedList all = getRoutesOfAllStops(1);


        //  all = getRoutesOfAllStops();



        for (int i = 0; i < all.size(); i++) {
            for (int j = 0; j < ((LinkedList) all.get(i)).size(); j++) {
                System.out.println("i=" + i + " j=" + j + " all=" + ((LinkedList) ((LinkedList) all).get(i)).get(j));
            }
        }


        LinkedList routes = getRoutesOfStop(18);
        for (int i = 0; i < routes.size(); i++) {
            System.out.println(routes.get(i));
        }


    }

    public static HashMap getRoutesOfAllStops() throws SQLException {
        HashMap all = new HashMap();
        Connection c = REPLAN.getController().getConnection();

        String query = "SELECT stop_id FROM martyo.tarrant_gtfs_stops;";

        ResultSet stop_results = c.createStatement().executeQuery(query);

        int stop_id;
        while (stop_results.next()) {

            stop_id = stop_results.getInt("stop_id");
            System.out.println(stop_id);
            all.put(stop_id, getRoutesOfStop(stop_id));
        }

        return all;
    }

    public static LinkedList getRoutesOfAllStops(int junk) throws SQLException {
        LinkedList all = new LinkedList();
        Connection c = REPLAN.getController().getConnection();

        String query = "SELECT stop_id FROM martyo.tarrant_gtfs_stops;";

        ResultSet stop_results = c.createStatement().executeQuery(query);

        int stop_id;
        while (stop_results.next()) {
            stop_id = stop_results.getInt("stop_id");
System.out.println(stop_id);
            all.add(getRoutesOfStop(stop_id));
        }

        return all;
    }

    /**
     * Returns a LinkedList of integers, where each integer is a route_id associated
     * with the stop_id passed to the method
     * @param stop_id the id of the stop we are investigating
     * @return LinkedList of integers, where each integer is a route_id associated with the stop_id passed to the method
     * @throws SQLException
     */
    public static LinkedList getRoutesOfStop(int stop_id) throws SQLException {
        Connection c = REPLAN.getController().getConnection();
        LinkedList routes = new LinkedList();
        String query = "SELECT route_id "
                + "FROM ( "
                + "SELECT  DISTINCT(trips.route_id, stops.stop_id), "
                + "trips.route_id, "
                + "stops.stop_id "
                + "FROM  "
                + "martyo.tarrant_gtfs_stops AS stops "
                + "JOIN  "
                + "martyo.tarrant_gtfs_stop_times AS stop_times "
                + "ON (stops.stop_id=stop_times.stop_id) "
                + "JOIN  martyo.tarrant_gtfs_trips AS trips "
                + "ON (stop_times.trip_id = trips.trip_id) ) AS route_stop "
                + "WHERE stop_id=" + stop_id + ";";

        ResultSet results = c.createStatement().executeQuery(query);
        while (results.next()) {
            routes.add((results.getInt("route_id")));
        }

        return routes;


    }

    /**
     * This method was from earlier work and his currently hard-coded to use Dallas DART GTFS data - temporarily changing from public to private
     * @param s
     * @param c
     * @param dw
     * @param dw_min
     * @param newTableName
     * @param classTableName
     */
    private static void calculateServiceAreasPT(ScenarioState s, Connection c, double dw, double dw_min, String newTableName, String classTableName) {
// Note: Still need to implement dw_min feature!
        String query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " SET location = transform(location, 4326);";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " SET the_geom = transform(the_geom, 4326);";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX + " SET centroid = transform(centroid, 4326);";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        String pubTransTable = "dallas_dart_shapes";

        query =
                "SELECT \n"
                + " blocks.the_geom, \n"
                + " centroids.centroid, \n"
                + " blocks.logrecno, \n"
                + " centroids.distance AS distance_to_pod_or_station, \n"
                + " centroids.going_to_pod \n"
                // + " centroids.walking_to_station, \n"
                //+ " centroids.travelling_via_station \n"
                + "INTO \n"
                + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + newTableName + " \n"
                + "FROM \n"
                + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS blocks \n"
                + "JOIN \n"
                + "( \n"
                + " /*<Table of census block centroids within walking distance of stations>*/ \n"
                + " /* \n"
                + "   logrecno is the block identifier \n"
                + "   centroid is the point location of the centroid \n"
                + "   to_pod is the POD which is within the walking distance \n"
                + "   to_station is the station individuals must walk to from the census block \n"
                + "   via_station is the station individuals must travel to on public transportation to get to the POD \n"
                + "   distance is the distance from the block centroid to the to_station \n"
                + " */ \n"
                + " SELECT  \n"
                + "  centroids.logrecno AS logrecno, \n"
                + "  centroids.centroid AS centroid,  \n"
                + "  ST_DISTANCE(centroids.centroid,walking.location) AS distance, \n"
                + "  walking.to_pod AS going_to_pod \n"
                // + "  walking.via_station AS travelling_via_station \n"
                // + "  walking.station_fid AS walking_to_station \n"
                + " FROM \n"
                + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX + " AS centroids, \n"
                + " ( \n"
                + " /*<Table of stations and their locations within walking distance of POD>*/ \n"
                + "  SELECT  \n"
                //  + "   fid AS station_fid,  \n"
                + "   location,  \n"
                + "   pub.shape_id,  \n"
                //+ "   station AS via_station,  \n"
                + "   pod AS to_pod,  \n"
                + "   dw_minus_x \n"
                + "  FROM \n"
                + "   " + pubTransTable + " AS pub \n"
                + "  JOIN \n"
                + "  (  \n"
                + "   /*<Table of stations within walking distance of POD>*/ \n"
                + "   SELECT  \n"
                + "    c.route,  \n"
                // + "    c.station,  \n"
                + "    c.pod,  \n"
                + "    c.distance,  \n"
                + "    c.dw_minus_x \n"
                + "   FROM \n"
                + "   ( \n"
                + "    SELECT \n"
                + "     route,  \n"
                + "     MIN(distance) AS distance \n"
                + "    FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + classTableName + " \n"
                + "    GROUP BY route \n"
                + "   ) AS x \n"
                + "   INNER JOIN  \n"
                + "    " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + classTableName + " AS c \n"
                + "   ON \n"
                + "    c.route = x.route  \n"
                + "   AND  \n"
                + "    c.distance = x.distance \n"
                + "   /*</Table of stations within walking distance of POD>*/ \n"
                + "  ) AS class \n"
                + "  ON \n"
                + "   class.route=pub.shape_id \n" //***
                + "  /*</Table of stations and their locations within walking distance of POD>*/ \n"
                + " ) AS walking \n"
                + " WHERE \n"
                + "  ST_DISTANCE(centroids.centroid, walking.location) <= walking.dw_minus_x \n"
                + "/* \n"
                + " OR \n"
                + "  ST_DISTANCE(centroids.centroid,pods.location) <= " + dw + " \n"
                + "*/ \n"
                + " ORDER BY logrecno \n"
                + " /*</Table of census block centroids within walking distance of stations>*/ \n"
                + ") AS centroids \n"
                + "ON \n"
                + " blocks.logrecno = centroids.logrecno \n"
                + "; \n";
        System.out.println(query);

        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Finished creating " + newTableName);
    }

    /**
     * This method was from earlier work on the problem - temporarily changing from public to private
     * @param s
     * @param c
     * @param dw
     * @param newTableName
     * @param classTableName
     */
    private static void calculateServiceAreasPOD(ScenarioState s, Connection c, double dw, String newTableName, String classTableName) {
// Note: Still need to implement dw_min feature!
        String query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " SET location = transform(location, 4326);";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " SET the_geom = transform(the_geom, 4326);";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX + " SET centroid = transform(centroid, 4326);";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        String pubTransTable = "dallas_dart_shapes";

        query =
                "SELECT \n"
                + " blocks.the_geom, \n"
                + " centroids.centroid, \n"
                + " blocks.logrecno, \n"
                + " centroids.distance AS distance_to_pod_or_station, \n"
                + " centroids.going_to_pod \n"
                // + " centroids.walking_to_station, \n"
                //+ " centroids.travelling_via_station \n"
                + "INTO \n"
                + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + newTableName + " \n"
                + "FROM \n"
                + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS blocks \n"
                + "JOIN \n"
                + "( \n"
                + " /*<Table of census block centroids within walking distance of stations>*/ \n"
                + " /* \n"
                + "   logrecno is the block identifier \n"
                + "   centroid is the point location of the centroid \n"
                + "   to_pod is the POD which is within the walking distance \n"
                + "   to_station is the station individuals must walk to from the census block \n"
                + "   via_station is the station individuals must travel to on public transportation to get to the POD \n"
                + "   distance is the distance from the block centroid to the to_station \n"
                + " */ \n"
                + " SELECT  \n"
                + "  centroids.logrecno AS logrecno, \n"
                + "  centroids.centroid AS centroid,  \n"
                + "  ST_DISTANCE(centroids.centroid,walking.location) AS distance, \n"
                + "  walking.to_pod AS going_to_pod \n"
                // + "  walking.via_station AS travelling_via_station \n"
                // + "  walking.station_fid AS walking_to_station \n"
                + " FROM \n"
                + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX + " AS centroids, \n"
                + " ( \n"
                + " /*<Table of stations and their locations within walking distance of POD>*/ \n"
                + "  SELECT  \n"
                //  + "   fid AS station_fid,  \n"
                + "   location,  \n"
                + "   pub.shape_id,  \n"
                //+ "   station AS via_station,  \n"
                + "   pod AS to_pod,  \n"
                + "   dw_minus_x \n"
                + "  FROM \n"
                + "   " + pubTransTable + " AS pub \n"
                + "  JOIN \n"
                + "  (  \n"
                + "   /*<Table of stations within walking distance of POD>*/ \n"
                + "   SELECT  \n"
                + "    c.route,  \n"
                // + "    c.station,  \n"
                + "    c.pod,  \n"
                + "    c.distance,  \n"
                + "    c.dw_minus_x \n"
                + "   FROM \n"
                + "   ( \n"
                + "    SELECT \n"
                + "     route,  \n"
                + "     MIN(distance) AS distance \n"
                + "    FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + classTableName + " \n"
                + "    GROUP BY route \n"
                + "   ) AS x \n"
                + "   INNER JOIN  \n"
                + "    " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + classTableName + " AS c \n"
                + "   ON \n"
                + "    c.route = x.route  \n"
                + "   AND  \n"
                + "    c.distance = x.distance \n"
                + "   /*</Table of stations within walking distance of POD>*/ \n"
                + "  ) AS class \n"
                + "  ON \n"
                + "   class.route=pub.shape_id \n" //***
                + "  /*</Table of stations and their locations within walking distance of POD>*/ \n"
                + " ) AS walking \n"
                + " WHERE \n"
                // + "  ST_DISTANCE(centroids.centroid, walking.location) <= walking.dw_minus_x \n"
                // + "/* \n"
                // + " OR \n"
                + "  ST_DISTANCE(centroids.centroid,pods.location) <= " + dw + " \n"
                //+ "*/ \n"
                + " ORDER BY logrecno \n"
                + " /*</Table of census block centroids within walking distance of stations>*/ \n"
                + ") AS centroids \n"
                + "ON \n"
                + " blocks.logrecno = centroids.logrecno \n"
                + "; \n";
        System.out.println(query);

        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Finished creating " + newTableName);
    }

    public static void calculateStation2POD(ScenarioState s, Connection c, double dw, String newTableName) {
        /*  String query = "SELECT *, " + dw + " -distance AS dw_minus_x INTO "
        + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + newTableName + " FROM "
        + "( "
        + "SELECT pub.route AS route, pub.fid AS station, pods.id AS pod, ST_DISTANCE(pub.location, pods.location) AS distance "
        + "FROM martyo.pub_trans_denton_synth AS pub, "
        + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " AS pods "
        + ") AS temp "
        + "WHERE distance <= " + dw + " ;";
         */
        String query = "SELECT *, " + dw + " -distance AS dw_minus_x INTO "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + "_" + newTableName + " FROM "
                + "( "
                + "SELECT pub.shape_id AS route, pods.id AS pod, ST_DISTANCE(pub.location, pods.location) AS distance "
                + "FROM dallas_dart_shapes AS pub, "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " AS pods "
                + ") AS temp "
                + "WHERE distance <= " + dw + " ;";

        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
