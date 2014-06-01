/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.model.traffic;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.db.GeneralDBQueries;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.map.Layer;

/**
 *
 * @author sarat
 *
 */
public class TrafficAtCrossingPoints {

    /**
     * assigns the corresponding traffic class to each crossing point and
     * writes it to the database
     * @param c
     * @param stmt
     * @param s
     */
    public void assignTrafficClasses(Connection c, ScenarioState s) {
        try {

            Statement stmt = REPLAN.getController().getConnection().createStatement();
            String query;

            if (!GeneralDBQueries.columnExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, "class", REPLAN.getController().getConnection())) {
                query = "ALTER table " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX
                        + " ADD COLUMN class integer;";
                System.out.println(query);
                stmt.executeUpdate(query);
            }
            stmt.close();
            Statement stmt3 = REPLAN.getController().getConnection().createStatement();
            query = "SELECT id, road_id, population FROM " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + ";";
            System.out.println(query);
            ResultSet rs = stmt3.executeQuery(query);

            Statement stmt2 = REPLAN.getController().getConnection().createStatement();
            while (rs.next()) {
                int road_id = rs.getInt("road_id");
                int population = rs.getInt("population");
                int id = rs.getInt("id");
//TODO: peoplpercar is hardcoded right now, should that be fixed?
//TODO:dont query the databse each time in the while loop... build a big query maybe. and execute update

                // if road object doesn't exist yet, create it and add it to the HashMap
                if (s.getTrafficRoadsList().get(road_id) == null) {
                    s.getTrafficRoadsList().put(road_id, new TrafficOnRoadSegment(s, road_id));
                }

                int trafficClass = ((TrafficOnRoadSegment) s.getTrafficRoadsList().get(road_id)).getTrafficClass(Boolean.TRUE, Boolean.TRUE, 0, 49, 2, 4, population, 48);

                query = "UPDATE " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " SET class = "
                        + trafficClass + " WHERE id = " + id + ";";
                stmt2.executeUpdate(query);
            }

            stmt3.close();
            stmt2.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrafficAtCrossingPoints.class.getName()).log(
                    Level.SEVERE, null, ex);
            System.err.println("Error retrieving crossing points");
        }
    }

    public void assignTrafficClasses(ScenarioState s, int peoplePerCar, int traffic_type, int interval, Boolean weekday, int timeFrame) {
        try {

            Statement stmt = REPLAN.getController().getConnection().createStatement();
            String query;

            if (!GeneralDBQueries.columnExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, "class", REPLAN.getController().getConnection())) {
                query = "ALTER table " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX
                        + " ADD COLUMN class integer;";
                System.out.println(query);
                stmt.executeUpdate(query);
            }
            stmt.close();
            Statement stmt3 = REPLAN.getController().getConnection().createStatement();
            query = "SELECT id, road_id, population FROM " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + ";";
            System.out.println(query);
            ResultSet rs = stmt3.executeQuery(query);

            Statement stmt2 = REPLAN.getController().getConnection().createStatement();
            while (rs.next()) {
                int road_id = rs.getInt("road_id");
                int population = rs.getInt("population");
                int id = rs.getInt("id");
//TODO: peoplpercar is hardcoded right now, should that be fixed?
//TODO:dont query the databse each time in the while loop... build a big query maybe. and execute update
                //TrafficOnRoad tor = new TrafficOnRoad(road_id, population, 4, s);


                if (s.getTrafficRoadsList().get(road_id) == null) {
                    s.getTrafficRoadsList().put(road_id, new TrafficOnRoadSegment(s, road_id));
                }
                int trafficClass = ((TrafficOnRoadSegment) s.getTrafficRoadsList().get(road_id)).getTrafficClass(Boolean.TRUE, Boolean.TRUE, 1, 50, 2, 4, population, 48);
                query = "UPDATE " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " SET class = "
                        + trafficClass + " WHERE id = " + id + ";";
                stmt2.executeUpdate(query);
            }

            stmt3.close();
            stmt2.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrafficAtCrossingPoints.class.getName()).log(
                    Level.SEVERE, null, ex);
            System.err.println("Error retrieving crossing points");
        }
    }

    /**
     * Adjust traffic upon user changing assumptions
     * @param c
     * @param s
     */
    public void adjustTrafficClasses(Connection c, ScenarioState s,
            int numPeoplePerCar, int interval, int timeframe, int dayOfWeek,
            boolean podTraffic, boolean baseTraffic) throws
            SQLException {

        boolean isWeekday = true;
        if (dayOfWeek == 1) {
            isWeekday = false;
        }

        Statement stmt = REPLAN.getController().getConnection().createStatement();
        String query = "SELECT id, road_id, population FROM " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + ";";
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);
        Statement stmt2 = REPLAN.getController().getConnection().createStatement();
        while (rs.next()) {
            int road_id = rs.getInt("road_id");
            int population = rs.getInt("population");
            int id = rs.getInt("id");
            if (s.getTrafficRoadsList().get(road_id) == null) {
                s.getTrafficRoadsList().put(road_id, new TrafficOnRoadSegment(s, road_id));
            }

            int trafficClass = 0;
            if (baseTraffic || podTraffic) {
                trafficClass = ((TrafficOnRoadSegment) s.getTrafficRoadsList().get(road_id)).getTrafficClass(baseTraffic, podTraffic, dayOfWeek, interval, 2, numPeoplePerCar, population, timeframe);
            }
            query = "UPDATE " + UserState.userId + "." + s.getWorkingCopyName()
                    + DefaultConstants.CROSSINGPT_SUFFIX + " SET class = " + trafficClass
                    + " WHERE id = " + id + ";";

            stmt2.executeUpdate(query);
        }
        stmt.close();
        stmt2.close();
        rs.close();
    }

    /**
     * Adjust the population counts at each crossingpoint such that the population
     * of a sector are distributed among the sector's crossingpoints according
     * to the traffic capacity of the corresponding road segments at the
     * crossing points
     * @param c
     * @param prefix
     * @param numRings
     */
    public void averagePopulationBasedOnRoadClass(Connection c, ScenarioState s,
            int numRings) {
        try {

            Statement stmt1 = REPLAN.getController().getConnection().createStatement();

            Statement stmt2 = REPLAN.getController().getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String query =
                    "ALTER TABLE " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX
                    + " ADD PRIMARY KEY(id, crossing_point);";
            stmt1.executeUpdate(query);
            /*
             * Iterate through all rings from outside to inside, except
             * outmost ring, which does not have any crossingpoints leading
             * to it
             */
            for (int ring = numRings - 1; ring >= 1; ring--) {
                /*
                 * Except for the outer_ring-1, traffic from the crossingpoints
                 * leading into a particular sector need to be added first
                 */
                if (ring <= numRings - 2) {
                    this.addPopulationFromCrossingpoints(c, s, ring);
                }
                /*
                 * Distribute the population onto the crossingpoints
                 */
                this.splitUpPopulationAmongCrossingpoints(stmt1, stmt2, s,
                        ring);
            }
            stmt1.close();
            stmt2.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrafficAtCrossingPoints.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void addPopulationFromCrossingpoints(Connection c, ScenarioState s,
            int ring) {
        System.out.println("=== Adding population from crossingpoints on ring " + (ring
                + 1));
        try {
            String c_table = UserState.userId + "." + s.getWorkingCopyName()
                    + DefaultConstants.CROSSINGPT_SUFFIX;
            String innerQuery =
                    "SELECT feeds_into_sector, SUM(population) as population FROM "
                    + c_table + " WHERE ring = " + (ring + 1)
                    + " GROUP BY (feeds_into_sector)";
            String query = "UPDATE " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX
                    + " SET population = tmp.population + "
                    + c_table + ".population "
                    + " FROM (" + innerQuery
                    + ") AS tmp WHERE tmp.feeds_into_sector = sector;";
            Statement stmt = REPLAN.getController().getConnection().createStatement();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrafficAtCrossingPoints.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void splitUpPopulationAmongCrossingpoints(Statement stmt1,
            Statement stmt2, ScenarioState s, int ring) throws SQLException {

        System.out.println("=== Split up population of crossingpoitns on ring "
                + ring);

        // get all sector ids for that ring
        String query1 = "SELECT distinct(sector) FROM " + UserState.userId + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX
                + " WHERE ring = " + ring + ";";
        System.out.println(query1);
        ResultSet sectors = stmt1.executeQuery(query1);

        // go through all sectors and adjust population
        while (sectors.next()) {
            int i = sectors.getInt("sector");
            String query2 = "SELECT crossing_point, id, road_id, population FROM " + UserState.userId + "." + s.getWorkingCopyName()
                    + DefaultConstants.CROSSINGPT_SUFFIX + " WHERE sector = " + i + ";";

            ResultSet rs = stmt2.executeQuery(query2);
            int totalCapacity = 0;
            // calculate the total capacity of all crossingpoints of a sector
            int pops = -1;

            while (rs.next()) {
                int road_id = rs.getInt("road_id");

                if (s.getTrafficRoadsList().get(road_id) == null) {
                    s.getTrafficRoadsList().put(road_id, new TrafficOnRoadSegment(s, road_id));
                }

                totalCapacity += ((TrafficOnRoadSegment) s.getTrafficRoadsList().get(road_id)).totalCapacityPer15() * 4;
                pops = rs.getInt("population");
            }
            System.out.println("- - - > total per-hour capacity for sector " + i + " = "
                    + totalCapacity);
            System.out.println("- - - > population for sector = " + pops);
            rs.beforeFirst();
            /* distribute the population according to the capacity of
             * the crossingpoints
             */
            while (rs.next()) {
                int road_id = rs.getInt("road_id");
                float factor = (float) ((float) ((TrafficOnRoadSegment) s.getTrafficRoadsList().get(road_id)).totalCapacityPer15() * 4) / totalCapacity;

                int newVal = Math.round(((float) rs.getInt("population"))
                        * factor);
                rs.updateInt("population", newVal);
                rs.updateRow();
            }
            rs.close();
        }
        sectors.close();
    }
}
