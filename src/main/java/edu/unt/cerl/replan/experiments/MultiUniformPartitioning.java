/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.experiments;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.ProgressBar;
import edu.unt.cerl.replan.equalpopulation.DBInteractions;
import edu.unt.cerl.replan.equalpopulation.EqualPopulation;
import edu.unt.cerl.replan.equalpopulation.EqualPopulationTask;
import edu.unt.cerl.replan.equalpopulation.Partitioner;
import edu.unt.cerl.replan.equalpopulation.PrepareTables;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.pod.ReadPODsFromDB;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martyo
 */
public class MultiUniformPartitioning {

    Map<String, String> tableMap;

    public MultiUniformPartitioning() throws InterruptedException, SQLException {
        tableMap = new HashMap<String, String>();

        //generatePartitions(8016);
        generatePartitions(10000);
        
        /*
        for (int i = 279; i < 329; i += 2) {
            generatePartitions(i);
            System.out.println("Finished with " + i + " partitions!!!!!!!!!!!!!!!!!!!!!!!!");
        }
         * */
         

    }

    public synchronized void generatePartitions(int desiredPopulationSize) throws InterruptedException, SQLException {
        Connection c = REPLAN.getController().getConnection();
        String schema = ScenarioState.getAuthor(); //UserState.userId;


        edu.unt.cerl.replan.view.mainframe.MainFrame owner = REPLAN.getMainFrame();

        String workingCopy = owner.getTabs().getSelectedScenario().getState().getWorkingCopyName();


        // Create temporary tables to work with
        String newTableQuery = "SELECT * INTO " + schema + "." + workingCopy + DefaultConstants.BLOCK_SUFFIX + "_temp FROM " + schema + "." + workingCopy + DefaultConstants.BLOCK_SUFFIX;
        System.out.println(newTableQuery);
        c.createStatement().executeUpdate(newTableQuery);

        newTableQuery = "SELECT * INTO " + schema + "." + workingCopy + DefaultConstants.POPULATION_SUFFIX + "_temp FROM " + schema + "." + workingCopy + DefaultConstants.POPULATION_SUFFIX;
        System.out.println(newTableQuery);
        c.createStatement().executeUpdate(newTableQuery);

        newTableQuery = "SELECT * INTO " + schema + "." + workingCopy + DefaultConstants.CENTROID_SUFFIX + "_temp FROM " + schema + "." + workingCopy + DefaultConstants.CENTROID_SUFFIX;
        System.out.println(newTableQuery);
        c.createStatement().executeUpdate(newTableQuery);


        String block =  workingCopy + DefaultConstants.BLOCK_SUFFIX + "_temp";
        String population =  workingCopy + DefaultConstants.POPULATION_SUFFIX + "_temp";
        String centroid =  workingCopy + DefaultConstants.CENTROID_SUFFIX + "_temp";




        // delete all blocks with pop >= desiredPopulationSize from centroid table
         String query = "DELETE FROM " + schema + "." + centroid + " AS cen USING " + schema + "." + population + " AS pop WHERE cen.logrecno=pop.logrecno AND p0010001>=0.25*" + desiredPopulationSize + ";";
         //String query = "UPDATE " + schema + "." + population + " AS pop SET p0010001=0 WHERE  pop.p0010001>=0.25*" + desiredPopulationSize + ";";
        // This query may create disconnected regions, but I think the algorithm should still work since it really uses centroids.
        System.out.println(query);
        c.createStatement().executeUpdate(query);

        query = "DELETE FROM " + schema + "." + block + " AS block USING " + schema + "." + population + " AS pop WHERE block.logrecno=pop.logrecno AND p0010001>=0.25*" + desiredPopulationSize + ";";
        System.out.println(query);
        c.createStatement().executeUpdate(query);

        // Determine new regional population P'
        //query = "SELECT sum(p0010001) AS pop FROM " + schema + "." + population + ";"; //no longer need conditions here since we are now assigning to zero
        query = "SELECT sum(p0010001) AS pop FROM " + schema + "." + population + " WHERE p0010001<0.25*"+desiredPopulationSize+";"; //no longer need conditions here since we are now assigning to zero
        System.out.println(query);
        ResultSet results = c.createStatement().executeQuery(query);
        results.next(); // prime for first use
        int newTotalPop = results.getInt("pop");

        // Determine number of partitions k needed to achieve desiredPopulationSize
        int numPartitions = newTotalPop / desiredPopulationSize;
        // run uniform partitioning algorithm
        // separate out all blocks we had set to 0 and restore populations





        System.out.println("okButtonActionPerformed\t" + block);
        System.out.println("okButtonActionPerformed\t" + population);
        System.out.println("okButtonActionPerformed\t" + centroid);
//        task.setCurrent(10);
//        task.setStatMessage("Equal Population Partionining in progress..");
//        task.setStatusChanged(true);
        tableMap.put(DefaultConstants.SCHEMA, schema);
        tableMap.put(DefaultConstants.BLOCK_TABLE, block);
        tableMap.put(DefaultConstants.POPULATION_TABLE, population);
        tableMap.put(DefaultConstants.CENTROID_TABLE, centroid);
        tableMap.put(DefaultConstants.NUM_PODS, Integer.toString(numPartitions));
        tableMap.put("NumBooths", "1");
        owner.setEnabled(true);

        doTheWork();


        ScenarioState s = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();

        //need to limit following query to add only blocks with pop geq...
        //query = "SELECT * INTO " + ScenarioState.getAuthor() + "." + s.getName() + "_" + numPartitions + "_partitions_" + desiredPopulationSize + "_desiredpop FROM (SELECT the_geom, id FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX + " UNION SELECT the_geom, logrecno AS id FROM "+ ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + ";";
        query="SELECT * INTO "
                + ScenarioState.getAuthor() + "." + s.getName() + "_" + numPartitions + "_partitions_" + desiredPopulationSize + "_desiredpop "
                + "FROM ( "
                + "SELECT the_geom, id "
                + "FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX + " " 
                //+ "martyo.workingcpy_m20130606a_catchment "
                + "UNION  "
                + "SELECT block.the_geom, block.logrecno AS id  "
                + "FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS block, "
                //+ "martyo.workingcpy_m20130606a_census_blocks AS block, "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POPULATION_SUFFIX + " AS pop "
                //+ "martyo.workingcpy_m20130606a_population AS pop "
                + "WHERE block.logrecno=pop.logrecno AND p0010001>=0.25*"+desiredPopulationSize+") AS all2;";
        System.out.println(query);
        c.createStatement().executeUpdate(query);

        // Finally, put all the blocks with pop > desiredPopulationSize back into the list of catchment areas. We need to assign each CA an appropriate id and match the columns up, so the following commented-out query needs to be modified
        //query = "SELECT * INTO " + ScenarioState.getAuthor() + "." + s.getName() + "_" + numPartitions + "_partitions_" + desiredPopulationSize + "_desiredpop FROM " + schema + "." + workingCopy + DefaultConstants.BLOCK_SUFFIX + " WHERE p0010001>" + desiredPopulationSize + ";";
        //System.out.println(query);
        //c.createStatement().executeUpdate(query);

        //  EqualPopulationTask task = new EqualPopulationTask(tableMap, owner.getTabs().getSelectedScenario().getState());
        //  ProgressBar pb = new ProgressBar();
        // pb.setTask(task);
        //  task.addPropertyChangeListener(pb);
        // task.execute();




        // this.dispose();

        // DROP temporary tables
        String dropTableQuery = "DROP TABLE " + schema + "." + block;
        System.out.println(dropTableQuery);
        c.createStatement().executeUpdate(dropTableQuery);

        dropTableQuery = "DROP TABLE " + schema + "." + population;
        System.out.println(dropTableQuery);
        c.createStatement().executeUpdate(dropTableQuery);

        dropTableQuery = "DROP TABLE " + schema + "." + centroid;
        System.out.println(dropTableQuery);
        c.createStatement().executeUpdate(dropTableQuery);


    }

    public synchronized void generatePartitions_old(int numPartitions) throws InterruptedException, SQLException {

        String schema = UserState.userId;


        edu.unt.cerl.replan.view.mainframe.MainFrame owner = REPLAN.getMainFrame();

        String workingCopy = owner.getTabs().getSelectedScenario().getState().getWorkingCopyName();
        System.out.println("workingCopy = " + workingCopy);

        String block = workingCopy + DefaultConstants.BLOCK_SUFFIX;
        String population = workingCopy + DefaultConstants.POPULATION_SUFFIX;
        String centroid = workingCopy + DefaultConstants.CENTROID_SUFFIX;
        System.out.println("okButtonActionPerformed\t" + block);
        System.out.println("okButtonActionPerformed\t" + population);
        System.out.println("okButtonActionPerformed\t" + centroid);
//        task.setCurrent(10);
//        task.setStatMessage("Equal Population Partionining in progress..");
//        task.setStatusChanged(true);
        tableMap.put(DefaultConstants.SCHEMA, schema);
        tableMap.put(DefaultConstants.BLOCK_TABLE, block);
        tableMap.put(DefaultConstants.POPULATION_TABLE, population);
        tableMap.put(DefaultConstants.CENTROID_TABLE, centroid);
        tableMap.put(DefaultConstants.NUM_PODS, Integer.toString(numPartitions));
        tableMap.put("NumBooths", "1");
        owner.setEnabled(true);

        doTheWork();


        ScenarioState s = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();

        String query = "SELECT * INTO " + ScenarioState.getAuthor() + "." + s.getName() + "_" + numPartitions + "_partitions FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX + ";";
        System.out.println(query);
        Connection c = REPLAN.getController().getConnection();
        c.createStatement().executeUpdate(query);
        //  EqualPopulationTask task = new EqualPopulationTask(tableMap, owner.getTabs().getSelectedScenario().getState());
        //  ProgressBar pb = new ProgressBar();
        // pb.setTask(task);
        //  task.addPropertyChangeListener(pb);
        // task.execute();




        // this.dispose();


    }

    public synchronized void doTheWork() {
        ScenarioPanel scenarioPanel = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        ScenarioState scenarioState = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        //   int progress = 0;
        //  setProgress(0);
//        while (progress < 100) {
        //  progressString = "Initializing Equal Population Partitioning.";
        //  progress += 1;
        //  setProgress(Math.min(progress, 100));
        try {

            Connection connection = REPLAN.getController().getConnection();

            /*
            if (REPLAN.getQueries().tableExists(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) {
            scenarioPanel.getMapContent().removeLayer(scenarioPanel.getPodLayer());
            scenarioPanel.setPodLayer(null);
            scenarioPanel.getMapContent().removeLayer(scenarioPanel.getCatchmentLayer());
            scenarioPanel.setCatchmentLayer(null);

            }
             */

            if (REPLAN.getQueries().tableExists(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection())) {
                //wait(1000);
                System.out.println("Dropping pods table\n");
                REPLAN.getQueries().dropTable(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection());
                REPLAN.getQueries().dropTable(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection());
                REPLAN.getQueries().dropTable(UserState.userId, scenarioState.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX, REPLAN.getController().getConnection());

            }


            PrepareTables pt = new PrepareTables();
            pt.createCentroidTable(connection, tableMap);

//            DBInteractions db = new DBInteractions( tableMap, gisConvTools.getPOSTGIS(),
//                    gisConvTools.getJDBC_CONNECTION_STRING());
            //   progressString = "Establishing connections, preparing tables.";
            //   progress += 19;
            //   setProgress(Math.min(progress, 100));
//                task.setCurrent(20);
//                task.setStatMessage("Establishing connections, preparing tables...");
//                task.setStatusChanged(true);

            DBInteractions db = new DBInteractions(tableMap, REPLAN.getController().getPostGIS());
            db.establishNewConnection();
            db.prepTables(scenarioState);

            //  progressString = "Running Partitioner.";
            //   progress += 30;
            //  setProgress(Math.min(progress, 100));
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
            // wait(3000);
            try {
            scenarioPanel.setCatchmentLayer(scenarioPanel.createLayer(
            scenarioState.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX,
            DefaultStyles.createDefaultAreaStyle(), "Catchment Areas"));
            } catch (NullPointerException ex) {
            Logger.getLogger(EqualPopulation.class.getName()).
            log(Level.SEVERE, null, ex);
            }


             */
            //        adjustBooths(tableMap);
            new ReadPODsFromDB(scenarioState);
            scenarioState.setPodsSelected(true);
            scenarioState.setCatchmentAreasGiven(true);

            
            REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
            //REPLAN.getMainFrame().getTabs().getSelectedScenario().reRender();

            //       progressString = "Done.";
            //     progress += 50;
            //    setProgress(Math.min(progress, 100));
            //    firePropertyChange("progress", 99, progress);

        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        //    }
        //    return null;
    }
}
