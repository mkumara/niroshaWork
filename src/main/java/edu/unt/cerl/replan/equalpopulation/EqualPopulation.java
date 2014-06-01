/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.equalpopulation;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.controller.db.PODQueries;
import edu.unt.cerl.replan.pod.ReadPODsFromDB;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replan.view.windows.ProgressWindow;
import edu.unt.cerl.replanexecution.Replan;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class EqualPopulation {

    Replan task;

    public EqualPopulation(Map<String, String> tableMap, ScenarioState scenarioState, Replan task) {
        //GISConversionTools gisConvTools = new GISConversionTools();
        this.task = task;
        ProgressWindow progressWin = new ProgressWindow(task);
        task.setCurrent(0);
        try {
            ScenarioPanel scenarioPanel = REPLAN.getMainFrame().getTabs().getSelectedScenario();
            Connection connection = REPLAN.getController().getConnection();


            if (REPLAN.getQueries().tableExists(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) {
                scenarioPanel.getMapContent().removeLayer(scenarioPanel.getPodLayer());
                scenarioPanel.setPodLayer(null);
            }


            if (REPLAN.getQueries().tableExists(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) {
                //wait(1000);
                System.out.println("Dropping pods table\n");
                REPLAN.getQueries().dropTable(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection());
            }


            PrepareTables pt = new PrepareTables();
            pt.createCentroidTable(connection, tableMap);

//            DBInteractions db = new DBInteractions( tableMap, gisConvTools.getPOSTGIS(),
//                    gisConvTools.getJDBC_CONNECTION_STRING());
            task.setCurrent(20);
            task.setStatMessage("Establishing connections, preparing tables...");
            task.setStatusChanged(true);

            DBInteractions db = new DBInteractions(tableMap, REPLAN.getController().getPostGIS());
            db.establishNewConnection();
            db.prepTables(scenarioState);

            task.setCurrent(50);
            task.setStatMessage("Running Partitioner..");
            task.setStatusChanged(true);
            tableMap.put(DefaultConstants.B2P_TABLE, UserState.userId + "." + scenarioState.getWorkingCopyName());
            new Partitioner(db, tableMap, scenarioState);

            try {
                scenarioPanel.setPodLayer(scenarioPanel.createLayer(
                        scenarioState.getWorkingCopyName() + DefaultConstants.POD_SUFFIX,
                        DefaultStyles.createPODStyle(), "PODs"));
//                scenarioPanel.setPodListFromDB();

            } catch (NullPointerException ex) {
                Logger.getLogger(EqualPopulation.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            try {
                scenarioPanel.setCatchmentLayer(scenarioPanel.createLayer(
                        scenarioState.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX,
                        DefaultStyles.createDefaultAreaStyle(), "Catchment Areas"));
            } catch (NullPointerException ex) {
                Logger.getLogger(EqualPopulation.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

                            adjustBooths(tableMap);
                
             new ReadPODsFromDB(scenarioState);
            /*
            System.out.println("STARTING NEW REPLACEMENT CODE HERE:*****");
           
            DBQueriesJava dbQuery = new DBQueriesJava();
            REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().getPodList().setList(
                    //dbQuery.readPODsFromDB(scenarioState, REPLAN.getController().getConnection())
                    dbQuery.readWrkCpyPODsFromDB(scenarioState, REPLAN.getController().getConnection())
            );
            System.out.println("**1**");
            REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().displayPodList();
            System.out.println("**2**");
            REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodEditor().getPodList().setListChanged();
            System.out.println("**3**");
            REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setPodsChanged();
            System.out.println("**4**");
            REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setChangedAndNotify();
            
            System.out.println("ENDING NEW REPLACEMENT CODE HERE:*****");
            */
                
                scenarioState.setPodsSelected(true);
                scenarioState.setCatchmentAreasGiven(true);

            REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
            REPLAN.getMainFrame().getTabs().getSelectedScenario().reRender();

        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        task.setCurrent(100);
    }

    private void adjustBooths(Map<String, String> tableMap) {
        for (int i = 0; i < Integer.parseInt(tableMap.get(DefaultConstants.NUM_PODS)); i++) {
            try {
                PODQueries.update_POD(i, "numBooths", tableMap.get("NumBooths"));
            } catch (SQLException e) {
                e.printStackTrace();
                ;
            }

        }

    }
}
