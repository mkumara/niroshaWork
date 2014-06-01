package edu.unt.cerl.replan.controller;

import edu.unt.cerl.applicationframework.view.MainFrame;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class ReplanExecution {

    private int lengthOfTask;
    private int current = 0;
    private String statMessage = "RE-PLAN executing...";
    //  private MainFrame mainFrame;
    //  private Scenario s;
    private boolean statusChanged = false;
    MainFrame mf;
    ScenarioState state;

    public ReplanExecution(MainFrame mf, ScenarioState state) {
        this.lengthOfTask = 100;
        this.mf = mf;
        this.state = state;
    }

    //called to start task
    public void go() {
        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                return new ActualTask();
            }
        };
    }

    //called from ProgressBarDemo to find out how much work needs to be done
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    //called from ProgressBarDemo to find out how much has been done
    public int getCurrent() {
        return current;
    }

    public void stop() {
        current = lengthOfTask;
    }

    //called from ProgressBarDemo to find out if the task has completed
    public boolean done() {
        if (current >= lengthOfTask) {
            return true;
        } else {
            return false;
        }
    }

    public boolean msgChanged() {
        if (statusChanged) {
            this.statusChanged = false;
            return true;
        }
        return false;
    }

    public String getMessage() {
        return statMessage;
    }

    //the actual long running task, this runs in a SwingWorker thread
    class ActualTask {

        ActualTask() {
            //fake a long task,
            //make a random amount of progress every second

            try {
          //      mf.disableActionsDuringReplan();
                statMessage = "Executing RE-PLAN...";
                statusChanged = true;
                /*
                 * create necessary objects
                 */
                Statement stmt = REPLAN.getController().getConnection().
                        createStatement();

                current = 5; //make some progress

                if (state.getCatchmentAreasGiven()) {
                    /*
                     * create catchment areas
                     */
                    statMessage = "Calculating catchment areas...";
                    statusChanged = true;
                // TODO    gisTools.calculateCatchmentAreas(s.getWorkingName(), s);
                }

                // TODOs.createCatchmentLayer();
                current = 10; //make some progress

                /*
                 * create rings of proximity
                 */
                statMessage = "Calculating rings of proximity...";
                statusChanged = true;
              //  double ringDist = new Double((String) s.getScenarioState().
                //        getSettings().get("ring_distance"));
                // TODO gisTools.calculateTrafficRings(s.getWorkingName(), 3, ringDist, s);
                // TODO s.createRingLayer();

                current = 20; //make some progress

                /*
                 * generate crossing points and remove unwanted points
                 */
                statMessage = "Calculating crossing points...";
                statusChanged = true;
                // TODO  gisTools.createCrossingPoints(stmt, s.getWorkingName(), s);
                stmt.close();
                //c.close();

                current = 40; //make some progress
                // TODO CrossingPointCleaner cleaner = new CrossingPointCleaner();
                // TODO cleaner.clean(s);

                current = 45; //make some progress
                /*
                 * create ring segments
                 */
                statMessage = "Generating ring segments...";
                statusChanged = true;
                // TODO RingSegmentCreator rsc = new RingSegmentCreator(s);
                // TODO int numRings = 3;
                // TODO int numPods = state.getNumPods();
                // TODO rsc.hybridMethod(s, numPods, numRings);

                current = 70; //make some progress
                /*
                 * estimate traffic at the crossing points
                 */
                statMessage = "Estimating traffic at crossing points...";
                statusChanged = true;
                Connection c = REPLAN.getController().getConnection();
                // TODO TrafficAtCrossingpoints tacpts = new TrafficAtCrossingpoints();

                current = 80; //make some progress
                // TODO tacpts.averagePopulationBasedOnRoadClass(c, s.getWorkingName(),3, s);

                current = 90; //make some progress
                // TODO tacpts.assignTrafficClasses(c, s);

                // TODO s.getScenarioState().unsetPodsChanged();
                current = 100;
             //   mf.enableActionsAfterReplan();
                System.out.println(
                        " ================ no error yet ====================================");
                // TODO s.createCrossingLayer();
                // TODO s.reRender();
                c.close();

            } catch (SQLException ex) {
                Logger.getLogger(ReplanExecution.class.getName()).log(
                        Level.SEVERE, null,
                        ex);
            }
        }
    }
}
