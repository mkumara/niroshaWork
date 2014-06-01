/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller.action;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.RingSegmentCreator;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.traffic.TrafficAtCrossingPoints;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replanexecution.Replan;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.map.Layer;

/**
 *
 * @author sarat
 */
public class TrafficAnalysis {

    Replan task;
    DBQueriesJava dbQueries;
    Connection c;

    public TrafficAnalysis(ScenarioState state, ScenarioPanel currentScenario, Replan task) {

        c = REPLAN.getController().getConnection();
        dbQueries = new DBQueriesJava();
        this.task = task;
        //c = REPLAN.getController().getConnection();
        task.setCurrent(0);
        createRings(state, currentScenario);
        createCrossingPoints(state, currentScenario);
        manipulateCrossingPoints(state);
        generateRingSegments(state, currentScenario);
        estimateTrafficAtCrossingPoints(state, currentScenario);
        state.unsetPodsChanged();
        task.setCurrent(100);
        //    mainFrame.enableActionsAfterReplan();
        System.out.println(" ================ no error yet ====================================");

        state.setTrafficAnalysisPerformed(true);
        /**
         * REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().toggleAddButton();
         * REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().toggleDeleteButton();
         * REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().toggleOkButton();
         * REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().toggleLoadButton();
         *
         */
        REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
        currentScenario.getOptionPanel().setVisible(true);
//            try {
//                c.close();
//            } catch (SQLException e) {
//                System.out.println("Error Closing connection \n");
//            }

    }

    private void createRings(ScenarioState s, ScenarioPanel currentScenario) {
        task.setCurrent(10);
        task.setStatMessage("Calculating rings of proximity...");
        task.setStatusChanged(true);
//        if (REPLAN.getQueries().tableExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX, REPLAN.getController().getConnection())) {
//            Iterator<Layer> layersList = currentScenario.getMapContent().layers().listIterator();
//            while (layersList.hasNext()) {
//                Layer currentLayer = layersList.next();
//                if (currentLayer.getTitle().contentEquals("Rings")) {
//                    currentScenario.getMapContent().removeLayer(currentLayer);
//                    break;
//                }
//            }
//            REPLAN.getQueries().dropTable(UserState.userId, s.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX, REPLAN.getController().getConnection());
//        }


        System.out.println("Creating Rings");
        dbQueries.createRings(s, 3, 0.02, c);
        try {
            currentScenario.setRingLayer(currentScenario.createLayer(s.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX, DefaultStyles.createRingStyle(), "Rings"));
        } /*catch (CQLException ex) {
            Logger.getLogger(Replan.class.getName()).log(Level.SEVERE, null, ex);
        }*/ catch (Exception ex) {
            Logger.getLogger(Replan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createCrossingPoints(ScenarioState s, ScenarioPanel currentScenario) {
        task.setCurrent(20);
        task.setStatMessage("Calculating crossing points...");
        task.setStatusChanged(true);
//        if (REPLAN.getQueries().tableExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, REPLAN.getController().getConnection())) {
//            Iterator<Layer> layersList = currentScenario.getMapContent().layers().listIterator();
//            while (layersList.hasNext()) {
//                Layer currentLayer = layersList.next();
//                if (currentLayer.getTitle().contentEquals("Crossing Points")) {
//                    currentScenario.getMapContent().removeLayer(currentLayer);
//                    break;
//                }
//            }
//            REPLAN.getQueries().dropTable(UserState.userId, s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, REPLAN.getController().getConnection());
//        }

        System.out.println("Calculating Crossing Points");
        dbQueries.createCrossingPoints(s, c);


        currentScenario.setCrossingPointLayer(currentScenario.createLayer(s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, DefaultStyles.createCrossingStyleWithTraffic(), "Crossing Points"));
        currentScenario.getCrossingPointLayer().setVisible(false);
    }

    private void manipulateCrossingPoints(ScenarioState s) {
        task.setCurrent(40);//make some progress

        // may need to surround the following section with if (realTraffic)

        task.setStatMessage("Eliminating freeway ramps...");
        task.setStatusChanged(true);
        System.out.println("Eliminate funcl 6");
        dbQueries.crossingPointsEliminateFuncl(s, c, 6);

        task.setCurrent(45);
        task.setStatMessage("Breaking apart multipoints...");
        task.setStatusChanged(true);
        System.out.println("Break apart multipoints");
        dbQueries.crossingPointsBreakApartMultipoints(s, c);
        System.out.println("Clean crossing points");
        task.setCurrent(48);
        task.setStatMessage("Cleaning crossing points...");
        task.setStatusChanged(true);
        double buffer = 0.01;
        dbQueries.cleanCrossingPoints(s, c, buffer);

        task.setCurrent(49);
        task.setStatMessage("Eliminating duplicate points for each road...");
        task.setStatusChanged(true);
        dbQueries.crossingPointsEliminateDuplicateRoads(s, c);

        // CrossingPointCleaner cleaner = new CrossingPointCleaner();
        //   cleaner.clean(s);

    }

    private void generateRingSegments(ScenarioState state, ScenarioPanel currentScenario) {
        task.setCurrent(50);//make some progress

        // create ring segments

        task.setStatMessage("Generating ring segments...");
        task.setStatusChanged(true);
        RingSegmentCreator rsc = new RingSegmentCreator(state, c);
        int numRings = 3;
        int numPods = currentScenario.getPodList().get_number_of_pods();
        System.out.println("Number of PODs: " + numPods);
        try {
            //   int numRings = 3;
            //   int numPods = s.getScenarioState().getNumPods();
            rsc.hybridMethod(numPods, numRings);
        } catch (IOException ex) {
            Logger.getLogger(Replan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Replan.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void estimateTrafficAtCrossingPoints(ScenarioState s, ScenarioPanel currentScenario) {
        task.setCurrent(70); //make some progress

        // estimate traffic at the crossing points

        task.setStatMessage("Estimating traffic at crossing points...");
        task.setStatusChanged(true);
        //    c = DriverManager.getConnection(gisConvTools.
        //            getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        TrafficAtCrossingPoints tacpts = new TrafficAtCrossingPoints();

        task.setCurrent(80); //make some progress
        tacpts.averagePopulationBasedOnRoadClass(c, s, 3);

        task.setCurrent(90);
        ; //make some progress
        tacpts.assignTrafficClasses(c, s);
        //REPLAN.getMainFrame().getTabs().getSelectedScenario().createLayer(s.getState().getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX, DefaultStyles.createCrossingStyleWithTraffic(), "Crossing Points with traffic");
        currentScenario.refreshLayer(currentScenario.getCrossingPointLayer());
        currentScenario.getCrossingPointLayer().setVisible(true);
        //REPLAN.getMainFrame().getTabs().getSelectedScenario().getMapContent().removeLayer(crossingPointLayer);
        //REPLAN.getMainFrame().getTabs().getSelectedScenario().reRender();
        System.out.println("Deleted crossing point layer, creating new crossing point layer\n");
    }
}
