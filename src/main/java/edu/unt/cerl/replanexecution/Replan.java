//http://www.iam.ubc.ca/guides/javatut99/uiswing/components/progress.html

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replanexecution;

//import edu.unt.cerl.pod.CrossingPointCleaner;
//import edu.unt.cerl.pod.GISConversionTools;
//import edu.unt.cerl.pod.GISTools;
//import edu.unt.cerl.pod.RingSegmentCreator;
//import edu.unt.cerl.pod.gui.MainFrame;
//import edu.unt.cerl.pod.gui.Scenario;
//import edu.unt.cerl.traffic.TrafficAtCrossingpoints;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.TrafficAnalysis;
import edu.unt.cerl.replan.controller.action.Voronoi;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.coverage.Coverage;
import edu.unt.cerl.replan.coverage.CoverageOptions;
import edu.unt.cerl.replan.equalpopulation.SelectTableNames;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.model.traffic.TrafficAtCrossingPoints;
import edu.unt.cerl.replan.optimization.OptimizationTableNames;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class Replan {

    private int lengthOfTask;
    private int current = 0;
    private String statMessage = "RE-PLAN executing...";
    //private MainFrame mainFrame;
    private ScenarioPanel s;
    private boolean statusChanged = false;
    DBQueriesJava dbQueries;
    Connection c;
    Replan task;

    public Replan(ScenarioPanel s) {
        this.lengthOfTask = 100;
        //this.mainFrame = mainFrame;
        this.s = s;
        dbQueries = new DBQueriesJava();
        c = REPLAN.getController().getConnection();
        task = this;
    }

    //called to start task
//    public void go() {
//        final SwingWorker worker = new SwingWorker() {
//
//            public Object construct() {
//                return new ActualTask();
//            }
//        };
//    }
    public void initVoronoi() {

        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                prepareForReexec(s.getState());
                return new Voronoi(s.getState(), s, task);

            }
        };
    }

    public void initEqPopPartitioning() {

        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                prepareForReexec(s.getState());
                return new SelectTableNames(REPLAN.getMainFrame(), s, task);

            }
        };
    }

    public void initProportionalPartitioning() {

        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                prepareForReexec(s.getState());
                CoverageOptions coverageOptions = new CoverageOptions(REPLAN.getMainFrame(), true);
                coverageOptions.setVisible(true);
                s.setCoverageOptions(coverageOptions.returnPartitioningOptions());
                return (Object) coverageOptions;
                //return new Coverage(s);

            }
        };
    }

    public void initCoverageAnalysis() {

        final SwingWorker worker = new SwingWorker() {

            public Object construct() {

                prepareForReexec(s.getState());
                CoverageOptions coverageOptions = new CoverageOptions(REPLAN.getMainFrame(), true);
                coverageOptions.setVisible(true);
                s.setCoverageOptions(coverageOptions.returnCoverageOptions());
                return (Object) coverageOptions;
//                CoverageOptions coverageOptions = new CoverageOptions(REPLAN.getMainFrame(), true, s.getState(), s, task);
//                coverageOptions.setVisible(true);
//                s.setCoverageOptions(coverageOptions.returnCoverageOptions());
//                return (Object) coverageOptions;
//                return new Coverage(s);

            }
        };
    }

    public void initNcK() {

        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                return new OptimizationTableNames(s.getState(), s, task);

            }
        };
    }

    public void initTrafficAnalysis() {
        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                return new TrafficAnalysis(s.getState(), s, task);

            }
        };
    }

    public void refreshAnalysis() {
        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                return new RefreshAnalysis(s.getState(), s, task);

            }
        };
    }

    public class RefreshAnalysis {

        public RefreshAnalysis(ScenarioState state, ScenarioPanel s, Replan task) {
            prepareForReexec(state);
            new Voronoi(state, s, task);
            new TrafficAnalysis(state, s, task);
        }
    }
    //called from ProgressBarDemo to find out how much work needs to be done

    public int getLengthOfTask() {
        return lengthOfTask;
    }

    //called from ProgressBarDemo to find out how much has been done
    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
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

    public void setStatMessage(String statMessage) {
        this.statMessage = statMessage;
    }

    public void setScenarioPanel(ScenarioPanel spanel) {
        this.s = spanel;
    }

    public boolean isStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(boolean statusChanged) {
        this.statusChanged = statusChanged;
    }

    public void reAssignClasses(ScenarioState s, int peoplePerCar, int timeInterval, int dayOfWeek, int timeframe, boolean podT, boolean baseT) {

        /*
         * temporarily commented for testing current = 0; ProgressWindow
         * progressWin = new ProgressWindow(this);
         *
         * current = 5; statMessage = "Reassigning Classes..."; statusChanged =
         * true; TrafficAtCrossingPoints tacpts = new TrafficAtCrossingPoints();
         * tacpts.assignTrafficClasses(s, peoplePerCar, trafficType,
         * timeInterval, dayBool, timeframe); current = 80; statMessage =
         * "Refreshing Crossing Layer..."; statusChanged = true;
         * REPLAN.getMainFrame().getTabs().getSelectedScenario().refreshLayer(crossingPointLayer);
         * current = 100;
         *
         */
        ProgressWindow refreshProgress = new ProgressWindow(this);
        current = 0;

        try {
            current = 10;
            statMessage = "Adjusting traffic classes...";
            statusChanged = true;
            TrafficAtCrossingPoints tacpts = new TrafficAtCrossingPoints();
            tacpts.adjustTrafficClasses(c, s, peoplePerCar, timeInterval, timeframe, dayOfWeek, podT, baseT);
            current = 75;
            statMessage = "Refreshing Crossing Layer...";
            statusChanged = true;
            REPLAN.getMainFrame().getTabs().getSelectedScenario().refreshLayer(REPLAN.getMainFrame().getTabs().getSelectedScenario().getCrossingPointLayer());
            current = 100;
        } catch (SQLException ex) {
            Logger.getLogger(Replan.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    synchronized public void prepareForReexec(ScenarioState state) {
        //remove layers

        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection())) {
            s.getMapContent().removeLayer(s.getCatchmentLayer());
            s.setCatchmentLayer(null);

            try {
                wait(1000);
            } catch (java.lang.InterruptedException e) {
                Logger.getLogger(REPLAN.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.COVERAGE_SUFFIX, REPLAN.getController().getConnection())) {
            s.getMapContent().removeLayer(s.getCoverageLayer());
            s.setCoverageLayer(null);

            try {
                wait(1000);
            } catch (java.lang.InterruptedException e) {
                Logger.getLogger(REPLAN.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, REPLAN.getController().getConnection())) {
            s.getMapContent().removeLayer(s.getCrossingPointLayer());
            s.setCrossingPointLayer(null);

            try {
                wait(1000);
            } catch (java.lang.InterruptedException e) {
                Logger.getLogger(REPLAN.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        //Dropping rings table and layer
        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX, REPLAN.getController().getConnection())) {
            s.getMapContent().removeLayer(s.getRingLayer());
            s.setRingLayer(null);

            try {
                wait(1000);
            } catch (java.lang.InterruptedException e) {
                Logger.getLogger(REPLAN.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        //Deleting catchment table and catchment layer

        //s.reRender();

        //drop tables        
        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX, REPLAN.getController().getConnection())) {
            System.out.println("Dropping block_to_pods table\n");
            REPLAN.getQueries().dropTable(UserState.userId, state.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX, REPLAN.getController().getConnection());
        }

        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.COVERAGE_SUFFIX, REPLAN.getController().getConnection())) {
            REPLAN.getQueries().dropTable(UserState.userId, state.getWorkingCopyName() + DefaultConstants.COVERAGE_SUFFIX, REPLAN.getController().getConnection());
        }

        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX, REPLAN.getController().getConnection())) {
            REPLAN.getQueries().dropTable(UserState.userId, state.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX, REPLAN.getController().getConnection());
        }

        //Deleting catchment table and catchment layer
        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection())) {
            System.out.println("Dropping Catchment table\n");
            REPLAN.getQueries().dropTable(UserState.userId, state.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection());
        }

        if (REPLAN.getQueries().tableExists(UserState.userId, state.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, REPLAN.getController().getConnection())) {
            REPLAN.getQueries().dropTable(UserState.userId, state.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, REPLAN.getController().getConnection());
        }

        //Dropping rings table and layer
        //Dropping block_to_pods table 
    }
}
