/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.model.traffic;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author martyo
 */
public class BaseTrafficEstimator {

    private double[][][][] allTraffic; // the one array to rule them all!
    // [funcl] [weekday=1, weekend=0][15-minute intervals of day][max=0,min=1,avg=2,stddev=3]
    private ScenarioState s;
    String roadTable;
    String trafficCountsTable;
    String trafficRoadsJoinTable;
    String trafficCountsAllTable;
    Connection c;
    Statement stmt;

    /**
     *  Creates a BaseTrafficEstimator object which calculates and maintains a list of all base traffic assumptions per lane.
     *  The ScenarioState is needed to maintain consistancy among different scenario tabs. For each scenario, there should
     *  be only one, BaseTrafficEstimator object.
     * @param s ScenarioState of scenario this is for
     * @throws SQLException
     */
    public BaseTrafficEstimator(ScenarioState s) throws SQLException {
        this.s = s;

        roadTable = s.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.ROAD_SUFFIX;  //(String) s.getScenarioState().getSettings().get("roads");

        trafficCountsTable = (String) s.getSettings().get("trafficCounts");
        trafficRoadsJoinTable = (String) s.getSettings().get("trafficRoadsJoin");
        trafficCountsAllTable = (String) s.getSettings().get("trafficCountsAll");

        c = REPLAN.getController().getConnection();  //DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        stmt = c.createStatement();
        getFunclTrafficFromDB();
    }

    /**
     * Obtains base traffic data from database, loading it into the allTraffic[][][][] array
     * @throws SQLException
     */
    private void getFunclTrafficFromDB() throws SQLException {
        allTraffic = new double[8][2][96][4];
        // for when funcl=1
        int funclass = 1;
        String query = "SELECT "
                + "AVG(1.0*" + trafficCountsAllTable + ".total/" + roadTable + ".pklna) AS average, "
                + "MAX(" + trafficCountsAllTable + ".total/" + roadTable + ".pklna) As maximum, "
                + "MIN(" + trafficCountsAllTable + ".total/" + roadTable + ".pklna) AS minimum, "
                + "STDDEV(1.0*" + trafficCountsAllTable + ".total/" + roadTable + ".pklna) AS standard_dev "
                + "FROM " + trafficCountsAllTable + " "
                + "JOIN " + trafficRoadsJoinTable + " "
                + "ON " + trafficCountsAllTable + ".id=" + trafficRoadsJoinTable + ".traffic_id "
                + "JOIN " + roadTable + " "
                + "ON " + roadTable + ".id=" + trafficRoadsJoinTable + ".road_id "
                + "WHERE " + roadTable + ".funcl=1;";
        //System.out.println(query);
        ResultSet results = stmt.executeQuery(query);
        results.next();

        // Now we will populate array with 15 minute counts equal
        // to 1/96 of what was returned from the above DB query.
        // This will result allow us to use all of the same methods
        // for funcl 1 roads, while also keeping base traffic unchanged
        // throughout the day.

        // Different traffic for weekdays and weekends is not available for
        // funcl=1 roads due to the limited amount of input traffic count
        // data available.
        for (int hour = 0; hour < 24; hour++) {

            for (int minute = 0; minute < 60; minute += 15) {
                //     System.out.println("funclass: " + funclass + " hour: " + hour + " minute: " + minute);
                allTraffic[funclass - 1][0][(minute / 15 + hour * 4)][0] = results.getInt("maximum") / 96;
                allTraffic[funclass - 1][0][(minute / 15 + hour * 4)][1] = results.getInt("minimum") / 96;
                allTraffic[funclass - 1][0][(minute / 15 + hour * 4)][2] = results.getFloat("average") / 96;
//                System.out.println("allTraffic avg funcl!=1 : " + allTraffic[funclass - 1][0][0][(minute / 15 + hour * 4)][2]);
                allTraffic[funclass - 1][0][(minute / 15 + hour * 4)][3] = results.getFloat("standard_dev") / 96;

                allTraffic[funclass - 1][1][(minute / 15 + hour * 4)][0] = results.getInt("maximum") / 96;
                allTraffic[funclass - 1][1][(minute / 15 + hour * 4)][1] = results.getInt("minimum") / 96;
                allTraffic[funclass - 1][1][(minute / 15 + hour * 4)][2] = results.getFloat("average") / 96;
                allTraffic[funclass - 1][1][(minute / 15 + hour * 4)][3] = results.getFloat("standard_dev") / 96;
            }
        }

        // for when funcl != 1

        query = "SELECT * "
                + "FROM tc_per_lane_analysis_traffic_per_funcl_per_day_per_time ;";
        //System.out.println(query);
        results = stmt.executeQuery(query);
        // [funcl] [weekday=0, weekend=1][base=0,additional=1][15-minute intervals of day][max=0,min=1,avg=2,stddev=3]
        int temp_funclass = 0;
        int temp_weekday = 0;
        int temp_base = 0; // will always be 0 in the following loop
        int temp_interval = 0;
        int temp_avgminmaxstd = 0; // will always be 0 in the following loop
        while (results.next()) {
            temp_funclass = results.getInt("funcl") - 1;

            if (results.getBoolean("weekday")) {
                temp_weekday = 0;
            } else {
                temp_weekday = 1;
            }

            //temp_base=0;

            temp_interval = ((results.getInt("hour") * 4) + (results.getInt("minute") / 15));

            allTraffic[temp_funclass][temp_weekday][temp_interval][0] = results.getInt("maximum");
            allTraffic[temp_funclass][temp_weekday][temp_interval][1] = results.getInt("minimum");
            allTraffic[temp_funclass][temp_weekday][temp_interval][2] = results.getFloat("average");
            allTraffic[temp_funclass][temp_weekday][temp_interval][3] = results.getFloat("standard_dev");
        }
    }

    /**
     * Used to obtain base traffic estimate per lane for a road segment
     * @param funcl is the functional class of the road segment
     * @param weekday weekday=0, weekend=1
     * @param interval - must be 0-95, representing 15 minute intervals of the day starting at 0:00
     * @param type - max=0,min=1,avg=2,stddev=3
     * @return base traffic estimate per lane
     */
    public double getBaseTraffic(int funcl, int weekday, int interval, int type) {
        return allTraffic[funcl - 1][weekday][interval][type];
    }
}
