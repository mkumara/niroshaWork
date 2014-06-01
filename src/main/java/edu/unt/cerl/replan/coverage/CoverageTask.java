/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.coverage;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.SwingWorkerTask;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.optimization.*;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replanexecution.Replan;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.geotools.map.Layer;
import org.postgis.MultiPolygon;
import org.postgis.Point;

/**
 *
 * @author sarat
 */
public class CoverageTask extends SwingWorkerTask {

    private int tempPod = -1;
    private int pod = -1;
    private double distance;
    private DBInteractions db;
    private MultiPolygon[] podsArray = null;
    //private ScenarioState s;
    private int population = -1;
    private Map<Integer, Integer> pop;
    private Map<Integer, Integer> finalDistro;
    //private Replan task;
    DBQueriesJava dbQueries;
    Connection c;
    private ScenarioPanel currentScenario;
    private String progressString;
    int count = 0;

    public CoverageTask(ScenarioPanel sc) {
        this.currentScenario = sc;
    }

    @Override
    protected Object doInBackground() throws Exception {
        //throw new UnsupportedOperationException("Not supported yet.");
        int progress = 0;
        setProgress(0);
        System.out.println("Do in background: progress = " + progress + "\n");
//        while (progress < 100) {
            count++;
            if (count > 1) {
                JOptionPane.showMessageDialog(REPLAN.getMainFrame(),
                        "Erorr: Entering task for the 2nd time",
                        "Threading Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            int coverageOptions[] = currentScenario.getCoverageOptions();
            ScenarioState state = currentScenario.getState();
            //Replan task = currentScenario.getTask();

            if (REPLAN.getQueries().tableExists(UserState.userId,
                    state.getWorkingCopyName() + DefaultConstants.COVERAGE_SUFFIX,
                    REPLAN.getController().getConnection())) {
                currentScenario.getMapContent().removeLayer(currentScenario.getCoverageLayer());
                currentScenario.setCoverageLayer(null);
            }

            Map<String, String> dummyMap = new HashMap<String, String>();
            Map<String, String> tableMap = new HashMap<String, String>();
            MainFrame owner = REPLAN.getMainFrame();
            c = REPLAN.getController().getConnection();
            dbQueries = new DBQueriesJava();
            dbQueries.dropCoverageTable(state, c);

            progressString = "Initializing Coverage.";
            progress += 1;
            setProgress(Math.min(progress, 100));

//        task.setCurrent(1);
//        task.setStatMessage("Initializing Coverage.");
//        task.setStatusChanged(true);
//        System.out.println("Initializing Coverage.");

            String schema = UserState.userId;
            String workingCopy = owner.getTabs().getSelectedScenario().getState().getWorkingCopyName();
            String centroid = workingCopy + DefaultConstants.CENTROID_SUFFIX;
            String base = state.getWorkingCopyName();
            String blockTable = base + DefaultConstants.BLOCK_SUFFIX;
            String coverageTable = state.getWorkingCopyName() + DefaultConstants.COVERAGE_SUFFIX;
            String populationTable = base + DefaultConstants.POPULATION_SUFFIX;
            String podTable = UserState.userId + "." + base + DefaultConstants.POD_SUFFIX;
            tableMap.put(DefaultConstants.SCHEMA, schema);
            tableMap.put(DefaultConstants.BLOCK_TABLE, blockTable);
            tableMap.put(DefaultConstants.POPULATION_TABLE, populationTable);
            tableMap.put(DefaultConstants.CENTROID_TABLE, centroid);

            db = new DBInteractions(tableMap, REPLAN.getController().getPostGIS(), "");
            db.establishNewConnection();
            progress += 9;
            setProgress(Math.min(progress, 100));

            //task.setCurrent(10);

            boolean cbAvailable[] = null;
            boolean firstCensusBlock[] = null;
            boolean podAvailable[] = null;

            CensusBlockDistance xyz = new CensusBlockDistance();

            Comparator<PodPercentFull> percentFullComparator = null;

            double censusBlockPopulation = (double) this.population;
            double optimalValue[] = null;
            double percentFull = 0.0;

            int availableCBCount = -1;
            int blockId = -1;
            int blockPop = -1;
            int catchmentPop = 0;
            int cbId = 0;
            int censusBlockCount = -1;
            int chosenPOD = -1;
            int podCensusBlock[] = null;
            int podPopulation[] = null;
            int podId = -1;
            int podIds[] = null;
            int popCBMap[] = null;
            int podCount = -1;
            int podsSelected = 0;
            int selectedCensusBlock = -1;

            List catchment[] = null;

            Map<String, Integer> cbIdHash = null;
            Map<Integer, Integer> podIdMap = null;

            PodPercentFull leastFullPOD = null;

            POD podInfoArray[];

            Point cbCentroids[] = null;

            PopulationQueue contiguousQueue[] = null;

            PriorityQueue<PodPercentFull> percentFullQueue = null;

            ResultSet rsCensusBlockIds = null;
            ResultSet rsPODInfoArray = null;

            String censusBlockIds[] = null;

            TreeSet<CensusBlockDistance> cbAdjacency[] = null;
            ContiguousCensusBlocks selectableCensusBlocks[] = null;

            System.out.println("Population     = " + population);
            progressString = "Getting census block information.";
            progress += 10;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(20);
//            task.setStatMessage("Getting census block information.");
//            task.setStatusChanged(true);
            System.out.println("Getting census block information.");

            rsCensusBlockIds = db.getCensusBlockIds(blockTable);
            cbIdHash = db.getMapOfCensusBlockIds(rsCensusBlockIds);
            censusBlockCount = cbIdHash.size();
            censusBlockIds = db.getCensusBlockIdsAsArray(rsCensusBlockIds,
                    censusBlockCount, cbIdHash);
            progressString = "Determining census block adjacency.";
            progress += 10;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(30);
//            task.setStatMessage("Determining census block adjacency.");
//            task.setStatusChanged(true);
            System.out.println("Determining census block adjacency.");

            try {
                cbAdjacency = db.getCBAdjDistAsTreeSetArray(blockTable,
                        censusBlockCount, cbIdHash);
            } catch (Exception ex) {
                Logger.getLogger(
                        DBInteractions.class.getName()).log(Level.SEVERE,
                        null, ex);
            }

            progressString = "Getting POD information.";
            progress += 10;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(40);
//            task.setStatMessage("Getting POD information.");
//            task.setStatusChanged(true);
            System.out.println("Getting POD information.");

            rsPODInfoArray = db.getPods(podTable);
            podIdMap = db.getPODIndexMap(rsPODInfoArray);
            podCount = podIdMap.size();
            podIds = db.getPodIds(rsPODInfoArray, podIdMap);
            podInfoArray = db.getPODInfoArray(rsPODInfoArray, podIdMap);
            /*
            for( int index=0; index<podInfoArray.length; index++ )
            {
            System.out.println( "POD\t" + index
            + "\tId\t" + podInfoArray[index].getId()
            + "\tBooths\t" + podInfoArray[index].getNumBooths()
            + "\tx\t" + podInfoArray[index].getlocation().x
            + "\ty\t" + podInfoArray[index].getlocation().y
            );
            }
             */

            progressString = "Getting POD and census block mappings.";
            progress += 10;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(50);
//            task.setStatMessage("Getting POD and census block mappings.");
//            task.setStatusChanged(true);
            System.out.println("Getting POD and census block mappings.");

            try {
                podCensusBlock = db.getPODCensusBlock(blockTable,
                        podTable, cbIdHash, podIdMap);
            } catch (Exception ex) {
                Logger.getLogger(
                        DBInteractions.class.getName()).log(Level.SEVERE,
                        null, ex);
            }

            progressString = "Getting census block centroids and populations.";
            progress += 10;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(60);
//            task.setStatMessage("Getting census block centroids and populations.");
//            task.setStatusChanged(true);
            System.out.println("Getting census block centroids and populations.");

            cbCentroids = db.getCentroidsAsArray(cbIdHash);
            popCBMap = db.getPopulationAsArray(populationTable, cbIdHash);
            /*
            for( int index=0; index<cbCentroids.length; index++ )
            {
            System.out.println( "index\t" + index
            + "\tcensus block\t" + censusBlockIds[index]
            + "\tx\t" + cbCentroids[index].x
            + "\ty\t" + cbCentroids[index].y
            + "\tpopulation\t" + popCBMap[index]
            );
            }
             */

            ResultSet rsPODcensusBlockDist = null;

            // coverageOptions[0] determines the point from which distance is calculated:
            //  true - calculate distance from the centroid
            // false - calculate distance from the closest point
            if (coverageOptions[0] == 0) {
                rsPODcensusBlockDist =
                        db.getPODcensusBlockDist(podTable, populationTable, centroid);
            } else {
                rsPODcensusBlockDist =
                        db.getPODcensusBlockShortestDist(podTable, populationTable,
                        blockTable);
            }

            CensusBlockDistance podCensusBlockDist[][] =
                    db.getPODcensusBlockDistArray(podCount,
                    rsPODcensusBlockDist, cbIdHash);
            /*
            for( int i=0; i<podCensusBlockDist.length; i++ )
            {
            for( int j=0; j<podCensusBlockDist[i].length; j++ )
            {
            System.out.println( "pod\t" + i + "\tcensus block\t" + j
            + "\tdistance\t"+ podCensusBlockDist[i][j].distance );
            }
            }
             */
            podAvailable = new boolean[podCount];
            optimalValue = new double[podCount];
            podPopulation = new int[podCount];

            percentFullComparator = new PercentFullComparator();
            percentFullQueue = new PriorityQueue<PodPercentFull>(podCount, percentFullComparator);

            contiguousQueue = new PopulationQueue[podCount];

            availableCBCount = cbIdHash.size();
            cbAvailable = new boolean[availableCBCount];
            progressString = "Determining available census blocks.";
            progress += 5;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(65);
//            task.setStatMessage("Determining available census blocks.");
//            task.setStatusChanged(true);
            System.out.println("Determining available census blocks.");

//        System.out.println( "Census block count is " + cbIdHash.size() + "." );

            for (int i = 0; i < cbIdHash.size(); i++) {
                if (0 <= popCBMap[i]) {
                    cbAvailable[i] = true;
                } else {
//                System.out.println( "Census block " + i
//                + " has a popCBMap of " + popCBMap[i] + "." );
                    cbAvailable[i] = false;
                }
            }

            catchment = new LinkedList[podCount];
            selectableCensusBlocks = new ContiguousCensusBlocks[podCount];
            progressString = "Establishing the initial POD census block.";
            progress += 5;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(70);
//            task.setStatMessage("Establishing the initial POD census block.");
//            task.setStatusChanged(true);
            System.out.println("Establishing the initial POD census block.");

            double timeInterval = state.getTimeInterval() - 1;
            double timePerIndividual = state.getTimePerIndividual();
            double peoplePerInterval;

            // CoverageOptions[3] determine whether coverage or partitioning is being calculated
            //  0 - calculate coverage
            // >0 - calculate partitioning
            if (coverageOptions[3] == 0) {
                peoplePerInterval = 3600 * timeInterval / timePerIndividual;
            } else // coverageOptions[3] ! 0
            {
                // timeInterval is in hours while timePerIndividual is in minutes;
                // hence the factor of 3600.
                peoplePerInterval = db.getTotalPopulationSize();
            }

            System.out.println("timeInterval is " + timeInterval + ".");
            System.out.println("timePerIndividual is " + timePerIndividual + ".");
            System.out.println("peoplePerInterval is " + peoplePerInterval + ".");

            for (int i = 0; i < podCount; i++) {
                podId = podInfoArray[i].getId() - 1;
                podAvailable[podId] = false;
                podPopulation[podId] = 0;
                contiguousQueue[podId] = new PopulationQueue(cbIdHash.size());
                catchment[podId] = new LinkedList();
                optimalValue[podId] = podInfoArray[i].getNumBooths()
                        * peoplePerInterval;

                System.out.println("For POD " + (podId + 1) + ", NumBooths is " + podInfoArray[i].getNumBooths() + ".");
                System.out.println("For POD " + (podId + 1) + ", optimalValue is " + optimalValue[podId] + ".");

                // If available, place the POD in its census block.
                // If there are multiple PODs in the same census block, only the POD
                // with the smallest id will be used.
                cbId = podCensusBlock[podId];
                /*
                System.out.println( "POD\tindex\t" + i + "\tpodId\t" + (podId+1)
                + "\tbelongs in census block\t" + cbId + "\tid\t"
                + censusBlockIds[cbId] + "\tAvailable:\t" + cbAvailable[cbId] );
                 */
                if (cbAvailable[cbId]) {
                    cbAvailable[cbId] = false;
                    podAvailable[podId] = true;
                    podPopulation[podId] = popCBMap[cbId];
                    catchment[podId].add(cbId);

//                System.out.println( "popCBMap[" + cbId + "] is " + popCBMap[cbId] + "." );
//                System.out.println( "For POD " + (podId+1) + ", percentFull is " + percentFull + "." );

                    percentFull = podPopulation[podId] / optimalValue[podId];
                    /**/
                    System.out.println(
                            "podId\t" + (podId + 1)
                            + "\tpercentFull\t" + percentFull
                            + "\tpodPopulation\t" + podPopulation[podId]
                            + "\toptimalValue\t" + optimalValue[podId]);
                    /**/
                    // Fill the POD queue based on the size of the initial census
                    // block as long as the initial census block did not fill the POD.
                    if (percentFull < 1.0) {
                        percentFullQueue.add(new PodPercentFull(podId, percentFull));

                        // Create the contiguous census block list based upon the
                        // initial census block.
//                    System.out.println( "For POD\tindex\t" + i
//                            + "\tpodId\t" + (podId+1)
//                            + "\tcreating initialSelectableCBs." );

                        selectableCensusBlocks[podId] =
                                initialSelectableCBs(cbAdjacency,
                                podCensusBlockDist[podId],
                                cbId, cbAvailable);

                        Iterator it = selectableCensusBlocks[podId].iterator();
                        while (it.hasNext()) {
                            int current = ((CensusBlockDistance) it.next()).target;

                            // coverageOptions[1] determines the type of priority used for adding new blocks:
                            //  0 - pure distance (closer equals higher priority).
                            // >0 - distance divided by population.
                            // <0 - population only (larger equals higher priority).
                            if (coverageOptions[1] > 0) {
                                double priority;

                                if (1 < popCBMap[current]) {
                                    priority = podCensusBlockDist[podId][current].distance
                                            / popCBMap[current];
                                } else {
                                    priority = podCensusBlockDist[podId][current].distance;
                                }

                                contiguousQueue[podId].add(current,
                                        popCBMap[current], priority);
                            } else if (coverageOptions[1] < 0) {
                                contiguousQueue[podId].add(current,
                                        popCBMap[current], -popCBMap[current]);
                            } else // coverageOptions[1] == 0
                            {
                                contiguousQueue[podId].add(current, popCBMap[current],
                                        podCensusBlockDist[podId][current].distance);
                            }
                            /*
                            System.out.println( "POD\t" + (podId+1)
                            + "\tcensus block\t" + current
                            + "\tdistance\t" + podCensusBlockDist[podId][current].distance
                            + "\tpopulation\t" + popCBMap[current] );
                             */
                        }
                    }
                }
            }
            progressString = "Building coverage zones.";
            progress += 5;
            setProgress(Math.min(progress, 100));
//            task.setCurrent(75);
//            task.setStatMessage("Building coverage zones.");
//            task.setStatusChanged(true);
            System.out.println("Building coverage zones.");

            while (!percentFullQueue.isEmpty()) {
                // Always select the POD which is least full
                leastFullPOD = percentFullQueue.remove();
                podId = leastFullPOD.pod;

                // Initialize the census block distance to zero in case there are no
                // available census blocks.
                xyz = new CensusBlockDistance();

                cbId = -1;

                // While there are still PODs that are not full.
                while (!contiguousQueue[podId].isEmpty()) {
                    // Get the next census block closest to the POD.
                    xyz = contiguousQueue[podId].poll();

                    // Find the global census block index.
//                cbId = cbIdHash.get( new Integer(xyz.censusBlock).toString() );
                    cbId = xyz.target;

                    // Make certain that the census block is available.  It could
                    // have been assign to a POD already.
                    if (cbAvailable[cbId]) {
                        // Leave this while loop as soon as an available, contiguous
                        // census block is found.
                        break;
                    }
                }

                // There are no census blocks available for this POD.
                if (cbId < 0 || !cbAvailable[cbId]) {
                    // Go to the next POD.
                    continue;
                }

                cbAvailable[cbId] = false;
                catchment[podId].add(cbId);

                podPopulation[podId] += xyz.population;

//            System.out.println( "POD\t" + (podId+1) + "\tadding census block\t" + cbId
//                    + "\twith population:\t" + xyz.population + "\tnew total:\t"
//                    + podPopulation[podId] );

                percentFull = podPopulation[podId] / optimalValue[podId];
                /**/
                System.out.println(
                        "podId\t" + (podId + 1)
                        + "\tpercentFull\t" + percentFull
                        + "\tpodPopulation\t" + podPopulation[podId]
                        + "\toptimalValue\t" + optimalValue[podId]);
                /**/
                // If the POD is not full, add it back into the queue with the current
                // percent full.
                if (percentFull < 1.0) {
                    percentFullQueue.add(new PodPercentFull(podId, percentFull));

                    selectableCensusBlocks[podId] =
                            initialSelectableCBs(cbAdjacency,
                            podCensusBlockDist[podId],
                            cbId, cbAvailable);

                    Iterator it = selectableCensusBlocks[podId].iterator();
                    while (it.hasNext()) {
                        int current = ((CensusBlockDistance) it.next()).target;

                        // coverageOptions[1] determines the type of priority used for adding new blocks:
                        //  0 - pure distance (closer equals higher priority).
                        // >0 - distance divided by population.
                        // <0 - population only (larger equals higher priority).
                        if (coverageOptions[1] > 0) {
                            double priority;

                            if (1 < popCBMap[current]) {
                                priority = podCensusBlockDist[podId][current].distance
                                        / popCBMap[current];
                            } else {
                                priority = podCensusBlockDist[podId][current].distance;
                            }

                            contiguousQueue[podId].add(current,
                                    popCBMap[current], priority);
                        } else if (coverageOptions[1] < 0) {
                            contiguousQueue[podId].add(current,
                                    popCBMap[current], -popCBMap[current]);
                        } else // coverageOptions[1] == 0
                        {
                            contiguousQueue[podId].add(current, popCBMap[current],
                                    podCensusBlockDist[podId][current].distance);
                        }

                        /*
                        System.out.println( "POD\t" + (podId+1)
                        + "\tcensus block\t" + current
                        + "\tdistance\t" + podCensusBlockDist[podId][current].distance
                        + "\tpopulation\t" + popCBMap[current] );
                         */
                    }
                }
            }

            progress += 20;
            setProgress(Math.min(progress, 100));

            // coverageOptions[3] determine whether coverage or partitioning is being calculated
            //  0 - calculate coverage
            //  0 - calculate partitioning
            if (coverageOptions[3] == 0) {
                //task.setStatMessage("Coloring covered zones.");
                System.out.println("Coloring covered zones.");
                dbQueries.createCoverageZones(state, podCount, podInfoArray,
                        catchment, censusBlockIds, c);

                // Catchment areas have been calculated. Next, the catchment area
                // boundaries need to be formed.
                try {
                    Layer coverageLayer = currentScenario.createLayer(
                            coverageTable,
                            DefaultStyles.createCoverageStyle(),
                            DefaultConstants.COVERAGE);

                    currentScenario.setCoverageLayer(coverageLayer);
                } catch (Exception ex) {
                    Logger.getLogger(Replan.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //task.setStatMessage("Map blocks to pods.");
                System.out.println("Map blocks to pods.");
                dbQueries.createProportionalPartitioningMapping(state, podCount,
                        podInfoArray, catchment, censusBlockIds, c);

                System.out.println("Creating Catchment Area Polygons");
                dbQueries.createCatchmentAreaPolygons(state, c);

                currentScenario.setCatchmentLayer(currentScenario.createLayer(
                        state.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX,
                        DefaultStyles.createDefaultAreaStyle(), "Catchment Areas"));

                state.setCatchmentAreasGiven(true);
                REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
            }

            currentScenario.getState().setCoverageAnalysisPerformed(true);
            progressString = "Done.";
            progress += 5;
            setProgress(Math.min(progress, 100));
            //task.setCurrent(100);
            //progress += random.nextInt(10);
            //setProgress(Math.min(progress, 100));
//        }
        return null;
    }

    public ContiguousCensusBlocks initialSelectableCBs(
            TreeSet<CensusBlockDistance> cbAdjacency[],
            CensusBlockDistance podCensusBlockDist[],
            int cbId,
            boolean cbAvailable[]) {
        CensusBlockDistance temp = null;

        Iterator it;

        ContiguousCensusBlocks result = new ContiguousCensusBlocks();

        it = cbAdjacency[cbId].iterator();

        while (it.hasNext()) {
            temp = (CensusBlockDistance) it.next();
            int index = temp.target;

            if (cbAvailable[index]) {
//                System.out.println( "initialSelectableCBs: Adding census block\t"
//                        + index );
                //  void add( int id, int pop, double dist );
                result.add(index, temp.population,
                        podCensusBlockDist[index].distance);
            }
        }

        return result;
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
