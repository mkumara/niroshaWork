/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.equalpopulation;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.SwingWorkerTask;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.controller.db.PODQueries;
import edu.unt.cerl.replan.pod.ReadPODsFromDB;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.filter.text.cql2.CQLException;

/**
 *
 * @author sarat
 */
public class EqualPopulationTask extends SwingWorkerTask {

    Map<String, String> tableMap;
    ScenarioState scenarioState;
    String progressString;

    public EqualPopulationTask(Map<String, String> tableMap, ScenarioState scenarioState) {
        this.tableMap = tableMap;
        this.scenarioState = scenarioState;

    }

    @Override
    synchronized protected Object doInBackground() throws Exception {
        ScenarioPanel scenarioPanel = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        int progress = 0;
        setProgress(0);
//        while (progress < 100) {
        progressString = "Initializing Equal Population Partitioning.";
        progress += 1;
        setProgress(Math.min(progress, 100));
        try {

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
            progressString = "Establishing connections, preparing tables.";
            progress += 19;
            setProgress(Math.min(progress, 100));
//                task.setCurrent(20);
//                task.setStatMessage("Establishing connections, preparing tables...");
//                task.setStatusChanged(true);

            DBInteractions db = new DBInteractions(tableMap, REPLAN.getController().getPostGIS());
            db.establishNewConnection();
            db.prepTables(scenarioState);

            progressString = "Running Partitioner.";
            progress += 30;
            setProgress(Math.min(progress, 100));
//                task.setCurrent(50);
//                task.setStatMessage("Running Partitioner..");
//                task.setStatusChanged(true);
            tableMap.put(DefaultConstants.B2P_TABLE, UserState.userId + "." + scenarioState.getWorkingCopyName());
            Partitioner p = new Partitioner(db, tableMap, scenarioState);
//System.out.println("Return value 0: "+p.getReturnValue()+"\n");
//                while(p.getReturnValue() != 1) {
//                    System.out.println("Waiting\n");
//                    Thread.sleep(100);
//                }
            
            
            // No longer addign the layers here, waiting until the updated is called in the
            // scenario, then the layers are added at this point 
            
            /*
            try {
                scenarioPanel.setPodLayer(scenarioPanel.createLayer(
                        scenarioState.getWorkingCopyName() + DefaultConstants.POD_SUFFIX,
                        DefaultStyles.createPODStyle(), "PODs"));
//                scenarioPanel.setPodListFromDB();

            } catch (NullPointerException ex) {
                Logger.getLogger(EqualPopulation.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            wait(3000);
            
            try {
                scenarioPanel.setCatchmentLayer(scenarioPanel.createLayer(
                        scenarioState.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX,
                        DefaultStyles.createDefaultAreaStyle(), "Catchment Areas"));
            } catch (NullPointerException ex) {
                Logger.getLogger(EqualPopulation.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            */
            
            adjustBooths(tableMap);
            
            
            new ReadPODsFromDB(scenarioState);
            scenarioPanel.getPODEditor_View().updatePODTracker();
            
            scenarioState.setPodsSelected(true);
            scenarioState.setCatchmentAreasGiven(true);
            
            
            REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
            REPLAN.getMainFrame().getTabs().getSelectedScenario().reRender();
        
            
            progressString = "Done.";
            progress += 50;
            setProgress(Math.min(progress, 100));
            firePropertyChange("progress", 99, progress);       
            

            
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
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

    @Override
    public String getProgressString() {
        return progressString;
    }

    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        System.out.println("Done\n");
        
        //startButton.setEnabled(true);
        //setCursor(null); //turn off the wait cursor
        //taskOutput.append("Done!\n");
    }
}
