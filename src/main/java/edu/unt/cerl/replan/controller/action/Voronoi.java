/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller.action;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replanexecution.Replan;
import java.sql.Connection;
import java.util.Iterator;
import org.geotools.map.Layer;

/**
 *
 * @author sarat
 */
public class Voronoi {

    Replan task;
    DBQueriesJava dbQueries;
    Connection c;
    int flag = 0;

    public Voronoi(ScenarioState state, ScenarioPanel currentScenario, Replan task) {
        c = REPLAN.getController().getConnection();
        this.task = task;
        dbQueries = new DBQueriesJava();
        //prepareForVoronoi(state, currentScenario);
        generateCatchmentAreas(state, currentScenario);
        state.setCatchmentAreasGiven(true);
        REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
        task.setCurrent(100);

//            try {
//                c.close();
//            } catch (SQLException e) {
//                System.out.println("Error Closing connection \n");
//            }

//            s.getState().unsetPodsChanged();
        task.setCurrent(100);
    }



//     private void prepareForVoronoi(ScenarioState s, ScenarioPanel currentScenario) {
//
//         //Drop layers first 
//         
//         // Then drop tables
//        //Dropping crossing points table and layer
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
//            try{
//            wait(1000);
//            } catch(java.lang.InterruptedException e) {
//            }
//        }
//
//        //Dropping rings table and layer
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
//                    try{
//            wait(1000);
//            } catch(java.lang.InterruptedException e) {
//            }
//        }
//
//        //Deleting catchment table and catchment layer
//        if (REPLAN.getQueries().tableExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection())) {
//            System.out.println("Dropping Catchment table\n");
//            Iterator<Layer> layersList = currentScenario.getMapContent().layers().listIterator();
//            while (layersList.hasNext()) {
//                Layer currentLayer = layersList.next();
//                if (currentLayer.getTitle().contentEquals("Catchment Areas")) {
//                    currentScenario.getMapContent().removeLayer(currentLayer);
//                    break;
//                }
//            }
//            REPLAN.getQueries().dropTable(UserState.userId, s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection());
//                    try{
//            wait(1000);
//            } catch(java.lang.InterruptedException e) {
//            }
//        }
//
//        //Dropping block_to_pods table 
//        if (REPLAN.getQueries().tableExists(UserState.userId, s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX, REPLAN.getController().getConnection())) {
//            System.out.println("Dropping block_to_pods table\n");
//            REPLAN.getQueries().dropTable(UserState.userId, s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX, REPLAN.getController().getConnection());
//                    try{
//            wait(1000);
//            } catch(java.lang.InterruptedException e) {
//            }
//        }
//        flag = 1;
//        //notifyAll();
//    }

    private void generateCatchmentAreas(ScenarioState s, ScenarioPanel currentScenario) {

        task.setStatMessage("Executing RE-PLAN...");
        task.setStatusChanged(true);

//voronoi start
        task.setCurrent(5);
        task.setStatMessage("Calculating catchment areas...");
        task.setStatusChanged(true);
        System.out.println("Calculating Block2POD Mapping");
        dbQueries.createBlock2PodMapping(s, c);
        task.setCurrent(50);
        System.out.println("Creating Catchment Area Polygons");
        dbQueries.createCatchmentAreaPolygons(s, c);
        task.setCurrent(90);
//        try {
//            wait();
//        } catch (java.lang.InterruptedException e) {
//            e.printStackTrace();
//        }

        currentScenario.setCatchmentLayer(currentScenario.createLayer(s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, DefaultStyles.createDefaultAreaStyle(), "Catchment Areas"));

        //voronoi end
        //traffic analysis start

    }
}
