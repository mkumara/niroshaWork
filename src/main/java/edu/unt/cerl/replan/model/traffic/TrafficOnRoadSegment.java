package edu.unt.cerl.replan.model.traffic;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author martyo
 */
public class TrafficOnRoadSegment {

    private BaseTrafficEstimator baseTraffic;
    private int road_id;
    private int funcl;
    private int numLanes;
    private int capacityPerLane;
    private ScenarioState s;

    /**
     * Creates a TrafficOnRoadSegment object. This object is needed to get traffic
     * situation estimates.
     * @param s ScenarioState of calling scenario - needed to differentiate between
     * different scenarios in different tabs
     * @param set_road_id road_id needed to get data from db
     * @throws SQLException
     */
    public TrafficOnRoadSegment(ScenarioState s, int set_road_id) throws SQLException {
        this.s = s;
        road_id = set_road_id;
        this.baseTraffic = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().getBaseTrafficEstimator();
        getTrafficDataFromDB();
    }

    /**
     * Get data about road segment from DB and load into private variables
     * @throws SQLException
     */
    private void getTrafficDataFromDB() throws SQLException {

        ResultSet results;
        Statement stmt = REPLAN.getController().getConnection().createStatement();

        String query = "SELECT funcl, pklna, hourly_cap_per_lane FROM " + s.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.ROAD_SUFFIX + " WHERE id=" + road_id + ";";
        results = stmt.executeQuery(query);
        results.next();
        funcl = results.getInt("funcl");
        numLanes = results.getInt("pklna");
        capacityPerLane = results.getInt("hourly_cap_per_lane");
    }

    /**
     * Returns the total 15-minute capacity of the road segment
     * @return the total capacity of the road segment
     */
    public int totalCapacityPer15() {
        return capacityPerLane * numLanes/4;
    }

    /**
     * Calculates and returns the number of cars per 15 minute interval resulting from traffic to POD
     * @param peoplePerCar number of people assumed to be in each car
     * @param numPeople number of people who must cross this road segment over the entire timeframe
     * @param timeFrame length of the time frame in hours
     * @return number of cars per 15 minute interval resulting from traffic to POD
     */
    private int podTrafficPer15(int peoplePerCar, int numPeople, int timeFrame) {
        return numPeople / peoplePerCar / (timeFrame * 4);
    }

    /**
     * Calculates and returns the total load (in numbers of cars) on the road segment
     * @param base_on true - base traffic included
     * @param pod_on true - pod traffic included
     * @param weekday 0 if weekday, 1 if weekend
     * @param interval 0-95 representing the 96 fifteen-minute intervals of the day starting at 0:00
     * @param type max=0,min=1,avg=2,stddev=3
     * @param peoplePerCar average number of people represented by each car
     * @param numPeople number of people who must cross this road segment over the entire timeframe
     * @param timeFrame length of the time frame in hours
     * @return
     */
    public double totalLoadOnSegment(Boolean base_on, Boolean pod_on, int weekday, int interval, int type, int peoplePerCar, int numPeople, int timeFrame) {
        double totalLoad = 0;
        if (base_on) {
            totalLoad += baseTraffic.getBaseTraffic(funcl, weekday, interval, type) * numLanes;
        }

        if (pod_on) {
            totalLoad += (double) podTrafficPer15(peoplePerCar, numPeople, timeFrame);
        }
        return totalLoad;
    }

    /**
     * Calculates and returns the proportion of capacity resulting from current traffic demands
     * @param base_on true - base traffic included
     * @param pod_on true - pod traffic included
     * @param weekday 0 if weekday, 1 if weekend
     * @param interval 0-95 representing the 96 fifteen-minute intervals of the day starting at 0:00
     * @param type max=0,min=1,avg=2,stddev=3
     * @param peoplePerCar average number of people represented by each car
     * @param numPeople number of people who must cross this road segment over the entire timeframe
     * @param timeFrame length of the time frame in hours
     * @return the proportion of capacity resulting from current traffic demands
     */
    public double proportionOfCapacity(Boolean base_on, Boolean pod_on, int weekday, int interval, int type, int peoplePerCar, int numPeople, int timeFrame) {
    //    System.out.println(road_id + " " + totalLoadOnSegment(base_on, pod_on, weekday, interval, type, peoplePerCar, numPeople, timeFrame) / ((float) totalCapacity()/4) + " " + base_on + " " + pod_on + " " + weekday + " " + interval + " " + type + " " + peoplePerCar + " " + numPeople + " " + timeFrame);
        return totalLoadOnSegment(base_on, pod_on, weekday, interval, type, peoplePerCar, numPeople, timeFrame) / ((double) totalCapacityPer15());
    }

    /**
     * Calculates and returns the traffic class resulting from current traffic demands
     * Class 0 - proportion <= 0.25
     * Class 1 0.25 < proportion <= 0.50
     * Class 2 0.50 < proportion <= 0.75
     * Class 3 0.75 < proportion <= 1.0
     * Class 4 1.0 < proportion <= 1.5
     * Class 5 proportion > 1.5
     * 
     * @param base_on true - base traffic included
     * @param pod_on true - pod traffic included
     * @param weekday 0 if weekday, 1 if weekend
     * @param interval 0-95 representing the 96 fifteen-minute intervals of the day starting at 0:00
     * @param type max=0,min=1,avg=2,stddev=3
     * @param peoplePerCar average number of people represented by each car
     * @param numPeople number of people who must cross this road segment over the entire timeframe
     * @param timeFrame length of the time frame in hours
     * @return the traffic class resulting from current traffic demands
     */
    public int getTrafficClass(Boolean base_on, Boolean pod_on, int weekday, int interval, int type, int peoplePerCar, int numPeople, int timeFrame) {
        double proportion = proportionOfCapacity(base_on, pod_on, weekday, interval, type, peoplePerCar, numPeople, timeFrame);

        //System.out.println(base_on + " " + pod_on + " " + weekday + " " + interval + " " + type + " " + peoplePerCar + " " + numPeople + " " + timeFrame);
        //System.out.println("totalLoadOnSegment: " + totalLoadOnSegment(base_on, pod_on, weekday, interval, type, peoplePerCar, numPeople, timeFrame));
        //System.out.println("totalCapacity: " + totalCapacityPer15());
        
        if (funcl == 1 || funcl == 7) {
            proportion *= 2; // This is to fix the problem caused by funcl 1 roads being represented as separate road segments for each direction
        }

        if (proportion <= 0.25) {
            //System.out.println(road_id + " " + proportion + " " + 0);
            //System.out.println("*********************************************************");
            return 0;
        }
        if (proportion <= 0.50) {
            //System.out.println(road_id + " " + proportion + " " + 1);
            //System.out.println("*********************************************************");
            return 1;
        }
        if (proportion <= 0.75) {
            //System.out.println(road_id + " " + proportion + " " + 2);
            //System.out.println("*********************************************************");
            return 2;
        }
        if (proportion <= 1.00) {
            //System.out.println(road_id + " " + proportion + " " + 3);
            //System.out.println("*********************************************************");
            return 3;
        }
        if (proportion <= 1.5) {
            //System.out.println(road_id + " " + proportion + " " + 4);
            //System.out.println("*********************************************************");
            return 4;
        }
        //System.out.println(road_id + " " + proportion + " " + 5);
        //System.out.println("*********************************************************");
        return 5;

    }
}
