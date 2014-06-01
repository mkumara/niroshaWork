/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.coverage;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.db.DBQueriesJava;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.optimization.*;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replan.view.windows.ProgressWindow;
import edu.unt.cerl.replanexecution.Replan;
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
import org.geotools.map.Layer;
import org.postgis.MultiPolygon;
import org.postgis.Point;

/**
 *
 * @author rag0122
 */
public class Coverage {

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

    public Coverage(
            ScenarioPanel currentScenario) {
        this.currentScenario = currentScenario;
        CoverageOptions coverageOptions = new CoverageOptions(REPLAN.getMainFrame(), true);
        //coverageOptions.setScenario(s);
        currentScenario.setCoverageOptions(coverageOptions.returnPartitioningOptions());
        coverageOptions.setVisible(true);
    }

    public void performCoverage() {
        int coverageOptions[] = currentScenario.getCoverageOptions();
        ScenarioState state = currentScenario.getState();
        Replan task = currentScenario.getTask();

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
        task.setCurrent(1);
        task.setStatMessage("Initializing Coverage.");
        task.setStatusChanged(true);
        System.out.println("Initializing Coverage.");

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
        task.setCurrent(10);

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
        task.setCurrent(20);
        task.setStatMessage("Getting census block information.");
        task.setStatusChanged(true);
        System.out.println("Getting census block information.");

        rsCensusBlockIds = db.getCensusBlockIds(blockTable);
        cbIdHash = db.getMapOfCensusBlockIds(rsCensusBlockIds);
        censusBlockCount = cbIdHash.size();
        censusBlockIds = db.getCensusBlockIdsAsArray(rsCensusBlockIds,
                censusBlockCount, cbIdHash);
        task.setCurrent(30);
        task.setStatMessage("Determining census block adjacency.");
        task.setStatusChanged(true);
        System.out.println("Determining census block adjacency.");

        try {
            cbAdjacency = db.getCBAdjDistAsTreeSetArray(blockTable,
                    censusBlockCount, cbIdHash);
        } catch (Exception ex) {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        task.setCurrent(40);
        task.setStatMessage("Getting POD information.");
        task.setStatusChanged(true);
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
        task.setCurrent(50);
        task.setStatMessage("Getting POD and census block mappings.");
        task.setStatusChanged(true);
        System.out.println("Getting POD and census block mappings.");

        try {
            podCensusBlock = db.getPODCensusBlock(blockTable,
                    podTable, cbIdHash, podIdMap);
        } catch (Exception ex) {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        task.setCurrent(60);
        task.setStatMessage("Getting census block centroids and populations.");
        task.setStatusChanged(true);
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
        task.setCurrent(65);
        task.setStatMessage("Determining available census blocks.");
        task.setStatusChanged(true);
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
        task.setCurrent(70);
        task.setStatMessage("Establishing the initial POD census block.");
        task.setStatusChanged(true);
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
        task.setCurrent(75);
        task.setStatMessage("Building coverage zones.");
        task.setStatusChanged(true);
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
        task.setCurrent(95);
        task.setStatusChanged(true);

        // coverageOptions[3] determine whether coverage or partitioning is being calculated
        //  0 - calculate coverage
        //  0 - calculate partitioning
        if (coverageOptions[3] == 0) {
            task.setStatMessage("Coloring covered zones.");
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
            task.setStatMessage("Map blocks to pods.");
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

        task.setCurrent(100);
//        System.out.println(" ================ no error in coverage ====================================");

        /*
        for( int i=0; i<podCount; i++ )
        {
        podId = podInfoArray[i].getId() - 1;

        System.out.println( "POD\t" + (podId+1)
        + "\tcontains the following\t" + catchment[podId].size()
        + "\tcensus blocks:" );

        Iterator it = catchment[podId].iterator();
        while( it.hasNext() )
        {
        int current = ( (Integer) it.next() ).intValue();
        System.out.println( "census block\t" + current
        + "\tpopulation:\t" + popCBMap[current] );
        }
        }
         */

    }
    // coverageOptions[0] determines the point from which distance is calculated:
    //  true - calculate distance from the centroid
    // false - calculate distance from the closest point
    // coverageOptions[1] determines the type of priority used for adding new blocks:
    //  0 - pure distance (closer equals higher priority).
    // >0 - distance divided by population.
    // <0 - population only (larger equals higher priority).
    // coverageOptions[2] determines the type of geographic blocks which are used:
    //  0 - use census blocks
    // >0 - use square tiles approximated from census blocks
    // <0 - use hexagonal tiles approximated from census blocks
    // coverageOptions[3] determine whether coverage or partitioning is being calculated
    //  0 - calculate coverage
    // >0 - calculate partitioning

    public int getPopulation() {
        return this.population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
    /*
    private double calculateDistance( Point p1, Point p2 )
    {
    double retVal = 0.0;

    // http://www.meridianworlddata.com/Distance-Calculation.asp
    // 3963 * ACOS(SIN(RADIANS(E2))*SIN(RADIANS(H2))+COS(RADIANS(E2))*COS(RADIANS(H2))*COS(RADIANS(G2)-RADIANS(D2)))
    // D2   p1.x
    // E2   p1.y
    // G2   p2.x
    // H2   p2.y
    retVal = 3963.0 * Math.acos(
    Math.sin(Math.toRadians(p1.y))
     * Math.sin(Math.toRadians(p2.y))
    + Math.cos(Math.toRadians(p1.y))
     * Math.cos(Math.toRadians(p2.y))
     * Math.cos(Math.toRadians(p2.x)
    - Math.toRadians(p1.x)
    ));

    System.out.println( "calculateDistance\tp1\t" + p1.x + "\t" + p1.y + "\tp2\t" + p2.x + "\t" + p2.y + "\tdistance\t" + retVal );

    return retVal;
    }
     */
    // Find the list of PODs adjacent to the current allocation.

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

    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    /*
     * CoverageOptions.java
     *
     * Created on Oct 22, 2012, 2:29:30 PM
     */
    /**
     *
     * @author rag0122
     */
    public class CoverageOptions extends javax.swing.JDialog {

        boolean done = false;
        int blockOption = 0;
        int distanceOption = 0;
        int priorityOption = 0;
        int coverageOptions[] = null;
//    ScenarioState state;
//    ScenarioPanel currentScenario;
//    Replan task;

        /** Creates new form CoverageOptions */
        public CoverageOptions(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
            this.addWindowListener(new java.awt.event.WindowAdapter() {

                public void windowClosing(java.awt.event.WindowEvent e) {
                    CoverageOptions.this.setVisible(false);
                }
            });
        }

//    public CoverageOptions(java.awt.Frame parent, boolean modal, ScenarioState state, ScenarioPanel currentScenario, Replan task) {
//        super(parent, modal);
//        this.state = state;
//        this.currentScenario = currentScenario;
//        this.task = task;
//        initComponents();
//        this.addWindowListener(new java.awt.event.WindowAdapter() {
//
//            public void windowClosing(java.awt.event.WindowEvent e) {
//                CoverageOptions.this.setVisible(false);
//            }
//        });
//    }
        public int returnBlockOption() {
            return blockOption;
        }

        public int[] returnCoverageOptions() {
            coverageOptions = new int[4];

            coverageOptions[0] = distanceOption;
            coverageOptions[1] = priorityOption;
            coverageOptions[2] = blockOption;
            coverageOptions[3] = 0;

            return coverageOptions;
        }

        public int[] returnPartitioningOptions() {
            coverageOptions = new int[4];

            coverageOptions[0] = distanceOption;
            coverageOptions[1] = priorityOption;
            coverageOptions[2] = blockOption;
            coverageOptions[3] = 1;

            return coverageOptions;
        }

        public int returnDistanceOption() {
            return distanceOption;
        }

        public int returnPriorityOption() {
            return priorityOption;
        }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            calculateDistanceFromPanel = new javax.swing.JPanel();
            centroidRadioButton = new javax.swing.JRadioButton();
            closestPointRadioButton = new javax.swing.JRadioButton();
            priorityStylePanel = new javax.swing.JPanel();
            distanceRadioButton = new javax.swing.JRadioButton();
            distanceOverPopulationRadioButton = new javax.swing.JRadioButton();
            populationRadioButton = new javax.swing.JRadioButton();
            coverageOptionsDoneButton = new javax.swing.JButton();
            blockTypePanel = new javax.swing.JPanel();
            censusBlocksRadioButton = new javax.swing.JRadioButton();
            hexagonalTilesRadioButton = new javax.swing.JRadioButton();
            squareTilesRadioButton = new javax.swing.JRadioButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            calculateDistanceFromPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Distance Type"));
            calculateDistanceFromPanel.setName(""); // NOI18N

            centroidRadioButton.setSelected(true);
            centroidRadioButton.setText("Block Centroid");
            centroidRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    centroidRadioButtonActionPerformed(evt);
                }
            });

            closestPointRadioButton.setText("Closest Point of Block");
            closestPointRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    closestPointRadioButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout calculateDistanceFromPanelLayout = new javax.swing.GroupLayout(calculateDistanceFromPanel);
            calculateDistanceFromPanel.setLayout(calculateDistanceFromPanelLayout);
            calculateDistanceFromPanelLayout.setHorizontalGroup(
                    calculateDistanceFromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(calculateDistanceFromPanelLayout.createSequentialGroup().addContainerGap().addGroup(calculateDistanceFromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(closestPointRadioButton).addComponent(centroidRadioButton)).addContainerGap(38, Short.MAX_VALUE)));
            calculateDistanceFromPanelLayout.setVerticalGroup(
                    calculateDistanceFromPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, calculateDistanceFromPanelLayout.createSequentialGroup().addComponent(centroidRadioButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(closestPointRadioButton)));

            priorityStylePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Priority Type"));

            distanceRadioButton.setSelected(true);
            distanceRadioButton.setText("Distance Only");
            distanceRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    distanceRadioButtonActionPerformed(evt);
                }
            });

            distanceOverPopulationRadioButton.setText("Distance / Population");
            distanceOverPopulationRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    distanceOverPopulationRadioButtonActionPerformed(evt);
                }
            });

            populationRadioButton.setText("Population Only");
            populationRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    populationRadioButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout priorityStylePanelLayout = new javax.swing.GroupLayout(priorityStylePanel);
            priorityStylePanel.setLayout(priorityStylePanelLayout);
            priorityStylePanelLayout.setHorizontalGroup(
                    priorityStylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(priorityStylePanelLayout.createSequentialGroup().addContainerGap().addGroup(priorityStylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(populationRadioButton).addComponent(distanceRadioButton).addComponent(distanceOverPopulationRadioButton)).addContainerGap(38, Short.MAX_VALUE)));
            priorityStylePanelLayout.setVerticalGroup(
                    priorityStylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(priorityStylePanelLayout.createSequentialGroup().addComponent(distanceRadioButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(populationRadioButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE).addComponent(distanceOverPopulationRadioButton)));

            coverageOptionsDoneButton.setText("Done");
            coverageOptionsDoneButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    coverageOptionsDoneButtonActionPerformed(evt);
                }
            });

            blockTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Block Type"));

            censusBlocksRadioButton.setSelected(true);
            censusBlocksRadioButton.setText("Census Blocks");
            censusBlocksRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    censusBlocksRadioButtonActionPerformed(evt);
                }
            });

            hexagonalTilesRadioButton.setText("Hexagonal Tiles");
            hexagonalTilesRadioButton.setEnabled(false);
            hexagonalTilesRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    hexagonalTilesRadioButtonActionPerformed(evt);
                }
            });

            squareTilesRadioButton.setText("Square Tiles");
            squareTilesRadioButton.setEnabled(false);
            squareTilesRadioButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    squareTilesRadioButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout blockTypePanelLayout = new javax.swing.GroupLayout(blockTypePanel);
            blockTypePanel.setLayout(blockTypePanelLayout);
            blockTypePanelLayout.setHorizontalGroup(
                    blockTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(blockTypePanelLayout.createSequentialGroup().addContainerGap().addGroup(blockTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(censusBlocksRadioButton).addComponent(squareTilesRadioButton).addComponent(hexagonalTilesRadioButton)).addContainerGap(64, Short.MAX_VALUE)));
            blockTypePanelLayout.setVerticalGroup(
                    blockTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(blockTypePanelLayout.createSequentialGroup().addComponent(censusBlocksRadioButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(squareTilesRadioButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, Short.MAX_VALUE).addComponent(hexagonalTilesRadioButton)));

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(60, 60, 60).addComponent(coverageOptionsDoneButton)).addComponent(priorityStylePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(calculateDistanceFromPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(blockTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(calculateDistanceFromPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(priorityStylePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(blockTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(coverageOptionsDoneButton).addContainerGap()));

            pack();
        }// </editor-fold>

        private void centroidRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            closestPointRadioButton.setSelected(false);
            centroidRadioButton.setSelected(true);
            distanceOption = 0;
        }

        private void closestPointRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            centroidRadioButton.setSelected(false);
            closestPointRadioButton.setSelected(true);
            distanceOption = 1;
        }

        private void distanceRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            distanceOverPopulationRadioButton.setSelected(false);
            distanceRadioButton.setSelected(true);
            populationRadioButton.setEnabled(false);
            priorityOption = 0;
        }

        private void distanceOverPopulationRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            distanceRadioButton.setSelected(false);
            distanceOverPopulationRadioButton.setSelected(true);
            populationRadioButton.setEnabled(false);
            priorityOption = 1;
        }

        private void censusBlocksRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            censusBlocksRadioButton.setSelected(true);
            hexagonalTilesRadioButton.setSelected(false);
            squareTilesRadioButton.setEnabled(false);
            blockOption = 0;
        }

        private void hexagonalTilesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            censusBlocksRadioButton.setSelected(false);
            hexagonalTilesRadioButton.setSelected(true);
            squareTilesRadioButton.setEnabled(false);
            blockOption = 1;
        }

        private void coverageOptionsDoneButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            boolean done = true;
            this.setVisible(false);
            performCoverage();
//                javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                //ProgressWindow progressWin = new ProgressWindow(REPLAN.getMainFrame().getTabs().getSelectedScenario().getTask());
//               //ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
//                //new Coverage(REPLAN.getMainFrame().getTabs().getSelectedScenario());
//
//            }
//        });




            //new Coverage(state, currentScenario, task);
        }

        private void populationRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            distanceRadioButton.setSelected(false);
            distanceOverPopulationRadioButton.setSelected(false);
            populationRadioButton.setEnabled(true);
            priorityOption = -1;
        }

        private void squareTilesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            censusBlocksRadioButton.setSelected(false);
            hexagonalTilesRadioButton.setSelected(false);
            squareTilesRadioButton.setEnabled(true);
            blockOption = -1;
        }
        /**
         * @param args the command line arguments
         */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
////                CoverageOptions dialog = new CoverageOptions(new javax.swing.JFrame(), true);
////                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
////
////                    public void windowClosing(java.awt.event.WindowEvent e) {
////                        System.exit(0);
////                    }
////                });
////                dialog.setVisible(true);
//            }
//        });
//    }
        // Variables declaration - do not modify
        private javax.swing.JPanel blockTypePanel;
        private javax.swing.JPanel calculateDistanceFromPanel;
        private javax.swing.JRadioButton censusBlocksRadioButton;
        private javax.swing.JRadioButton centroidRadioButton;
        private javax.swing.JRadioButton closestPointRadioButton;
        private javax.swing.JButton coverageOptionsDoneButton;
        private javax.swing.JRadioButton distanceOverPopulationRadioButton;
        private javax.swing.JRadioButton distanceRadioButton;
        private javax.swing.JRadioButton hexagonalTilesRadioButton;
        private javax.swing.JRadioButton populationRadioButton;
        private javax.swing.JPanel priorityStylePanel;
        private javax.swing.JRadioButton squareTilesRadioButton;
        // End of variables declaration
    }
}
