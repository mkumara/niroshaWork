package edu.unt.cerl.replan.optimization;

import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.pod.GISTools;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgis.MultiPolygon;
import org.postgis.Point;

public class Partitioner {

    private int tempPod = -1;
    private int pod = -1;
    private double distance;
    private DBInteractions db;
    private MultiPolygon[] podsArray = null;
    private ScenarioState s;
    private Map<String, String> m;
    private int population = -1;
    private List pods;
    private Map<Integer, Integer> pop;
    private Map<Integer, Integer> finalDistro;

    public Partitioner(DBInteractions db, Map<String, String> m, ScenarioState s) {
        long start = System.currentTimeMillis();
        this.s = s;
        this.db = db;
        this.m = m;
        pods = db.getPodsAsArray();
        //this.m.put(Tables.NUM_PODS, "" + pods.size());
        this.population = db.getTotalPopulationSize();
        pop = db.getPopulationAsMap();
        String values = "(";
        Iterator<POD> it = pods.iterator();
        boolean first = true;
        while (it.hasNext()) {
            POD temp = it.next();
            if (!first) {
                values += ",";
            }
            first = false;
            values += temp.getId();
        }
        values += ")";
        System.out.println("A. Remaining PODs = " + values);

        Map<Integer, Integer> distro =
                db.populationDistribution(m, pop, values);

        this.finalDistro = distro;
        this.printDistro();

        /**
         * exhaustive search or reverse hill climbing
         */
        if (m.get(Tables.CRITERION).equals(Tables.EXHAUSTIVE)) {
            System.out.println("Exhaustive search");
        } else {
            if (m.get(Tables.CRITERION).equals(Tables.FIXED)) {
                System.out.println("Fixed optimum");
                while (pods.size() > new Integer(m.get(Tables.NUM_PODS))) {
                    this.removePODWithOptima();
                }
            } else if (m.get(Tables.CRITERION).equals(
                    Tables.ADJUSTING_OPTIMA)) {
                System.out.println("Adjusting optima");
                while (pods.size() > new Integer(m.get(Tables.NUM_PODS))) {
                    this.removePODWithOptima();
                }
            } else if (m.get(Tables.CRITERION).equals(Tables.STD_DEV)) {
                System.out.println("Standard deviation");
                while (pods.size() > new Integer(m.get(Tables.NUM_PODS))) {
                    this.removePODBasedOnStandardDeviation();
                }
            } else if (m.get(Tables.CRITERION).equals(Tables.STD_DEV_FIXED)) {
                System.out.println("Fixed standard deviation");
                while (pods.size() > new Integer(m.get(Tables.NUM_PODS))) {
                    this.removePODBasedOnStandardDeviation();
                }
            } else if (m.get(Tables.CRITERION).equals(Tables.MINBLACKOUT)) {
                System.out.println("Greedy POD by POD - Minmum Blackout");
                    this.min_black_out();
            }

            /* String */ values = "(";
            /* boolean */ first = true;
            /* Iterator<POD> */ it = pods.iterator();

            while (it.hasNext()) {
                POD temp = it.next();
                if (!first) {
                    values += ",";
                }
                first = false;
                values += temp.getId();
            }
            values += ")";
            System.out.println("B. Remaining PODs = " + values);

            db.turnPODsOff(values);
            this.printDistro();
            long stop = System.currentTimeMillis();
            long elapsed = stop - start;
            System.out.println("Start\t" + start + "\tstop\t" + stop +
                    "\telapsed\t" +elapsed );
        }
    }

    private void removePODBasedOnStandardDeviation() {
        double optimalValue = 0;
        if (m.get(Tables.CRITERION).equals(Tables.STD_DEV)) {
            optimalValue = (double) ((double) this.population / (double) (pods.
                    size() - 1));
        } else if (m.get(Tables.CRITERION).equals(Tables.STD_DEV_FIXED)) {
            optimalValue = (double) ((double) this.population / (double) (new Integer(m.
                    get(Tables.NUM_PODS))));
        }

        System.out.println("optimal value = " + optimalValue);
        Iterator<POD> it = pods.iterator();
        double minMaxDeviation = Double.MAX_VALUE;
        POD toRemove = null;
        while (it.hasNext()) {
            double deviation = 0;
            // this is the POD that will be excluded
            POD pod = it.next();
            String values = "(";
            Iterator<POD> it2 = pods.iterator();
            boolean first = true;
            while (it2.hasNext()) {
                POD temp = it2.next();
                if (temp.getId() != pod.getId()) {
                    if (!first) {
                        values += ",";
                    }
                    first = false;
                    values += temp.getId();
                }
            }
            values += ")";

            Map<Integer, Integer> distro = db.populationDistribution(m, pop,
                    values);

            it2 = pods.iterator();
            while (it2.hasNext()) {
                POD temp = it2.next();
                if (temp.getId() != pod.getId()) {
                    double currPop = (double) distro.get(temp.getId());
                    double newDeviation = Math.pow(optimalValue - currPop, 2);
                    deviation += newDeviation;
                }
            }
            deviation /= (pods.size() - 1);
            if (deviation < minMaxDeviation) {
                this.finalDistro = distro;
                toRemove = pod;
                minMaxDeviation = deviation;
            }
            System.out.println("deviation " + deviation + "for POD "
                    + pod.getId());
            System.out.println("=======");
        }
        System.out.println("Removing POD " + toRemove.getId()
                + " with deviation of " + minMaxDeviation);
        pods.remove(toRemove);
    }

    private void min_black_out() {
        CensusBlockDistance predecessor[] = null;

        boolean cbAvailable[]   = null;
        boolean podsAvailable[] = null;

        CensusBlockPopulation cbp = null;

        Comparator<CensusBlockPopulation> populationComparator = null;

        double censusBlockPopulation = (double) this.population;

        GISTools gisTools = new GISTools();

        int availableCBCount = -1;
        int blockId = -1;
        int blockPop = -1;
        int catchmentPop = 0;
        int censusBlockCount = -1;
        int chosenPOD = -1;
        int numPods = (int) (new Integer(m.get(Tables.NUM_PODS) ));
        int path[] = null;
        int podCensusBlock[] = null;
        int podIds[] = null;
        int popCBMap[] = null;
        int podsRemaining = -1;
        int podsSelected = 0;
        int selectedCensusBlock = -1;

        List catchment = null;
        List<POD> result = new LinkedList<POD>();

        Map< String, Integer> cbIdHash = null;
        Map<Integer, Integer> podIdMap = null;

        POD podIdLocation[];

        Point cbCentroids[] = null;

        PriorityQueue<CensusBlockPopulation> populationQueue = null;

        ResultSet rsCensusBlockIds = null;
        ResultSet rsPODIdLocation  = null;

        String censusBlockIds[] = null;

        TreeSet<CensusBlockDistance> cbAdjacency[] = null;
        TreeSet<Integer> selectableCensusBlocks = null;

        double optimalValue = (double) ( censusBlockPopulation / numPods );

        if (!m.get(Tables.CRITERION).equals(Tables.MINBLACKOUT)) {
            System.out.println("Unacceptable CRITERION\t" +
                    m.get(Tables.CRITERION) );
        }

        System.out.println("Population     = " + population);
        System.out.println("POD Count      = " + pods.size() );
        System.out.println("Requested PODs = " + numPods);
        System.out.println("Optimal value  = " + optimalValue);

        rsCensusBlockIds = db.getCensusBlockIds( m.get(Tables.BLOCKS) );
        cbIdHash         = db.getMapOfCensusBlockIds( rsCensusBlockIds );
        censusBlockCount = cbIdHash.size();
        censusBlockIds   = db.getCensusBlockIdsAsArray( rsCensusBlockIds,
                                censusBlockCount, cbIdHash );

        try {
            cbAdjacency = db.getCBAdjDistAsTreeSetArray( m.get(Tables.BLOCKS),
                                censusBlockCount, cbIdHash );
        } catch (Exception ex) {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex );
        }

        cbCentroids = db.getCentroidsAsArray( cbIdHash );

        popCBMap = db.getPopulationAsArray( m.get(Tables.POPULATION), cbIdHash );

        populationComparator = new CensusBlockPopulationComparator();

        populationQueue = new PriorityQueue<CensusBlockPopulation>
                            ( cbIdHash.size(), populationComparator );

        availableCBCount = cbIdHash.size();
        cbAvailable = new boolean[availableCBCount];

        availableCBCount = 0;
        for( int i=0; i<cbIdHash.size(); i++ )
        {
            if( 0 <= popCBMap[i] )
            {
                cbAvailable[i] = true;
                availableCBCount++;
                populationQueue.add(
                        new CensusBlockPopulation( i, popCBMap[i]) );
            }
            else
            {
                cbAvailable[i] = false;
            }
        }

        rsPODIdLocation = db.getPods( m.get(Tables.PODS) );
        podIdMap        = db.getPODIndexMap( rsPODIdLocation );
        podsRemaining   = podIdMap.size();
        podIds          = db.getPodIds( rsPODIdLocation, podIdMap );
        podIdLocation   = db.getPODIdLocationArray( rsPODIdLocation, podIdMap );
        podsAvailable   = new boolean[podsRemaining];

        try
        {
            podCensusBlock = db.getPODCensusBlock( m.get(Tables.BLOCKS),
                    m.get(Tables.PODS), cbIdHash, podIdMap );
        }
        catch( Exception ex )
        {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex );
        }

        for( int i=0; i<podsRemaining; i++ )
        {
            podsAvailable[i] = true;
        }

        // Until k PODs have been assigned or no PODs remain.
        do {
            catchmentPop = 0;
            catchment = new ArrayList<Integer>();

            // Find the remaining census block with the highest population.
            blockId = blockPop = -1;
            cbp = null;

            do{
                int index = 0;

                cbp = null;

                if( populationQueue.isEmpty() )
                {
                    break;
                }
                else
                {
                    cbp = populationQueue.remove();
                }

                if( cbp != null )
                {
                    if( cbAvailable[cbp.censusBlock] )
                    {
                        blockId  = cbp.censusBlock;
                        blockPop = cbp.population;
                        cbAvailable[cbp.censusBlock] = false;
                    }
                }
            } while( (cbp != null) && (blockId < 0) );

            // Are we out of blocks with population?
            if( blockId < 0 ) break;

            // No, we still have at least one block with population.
            // Find the POD which is at the shortest travel distance.
            predecessor = Dijkstra( cbAdjacency,
                    cbAvailable, blockId );

            path = findPathToCensusBlockHoldingClosestPOD(
                    predecessor, blockId,
                    podCensusBlock, podsAvailable );

            chosenPOD = -1;

            if( path != null )
            {
                int x[ ]  = new int[1];
                    x[0]  = podsRemaining;
                chosenPOD = chosePod( podCensusBlock, path[0],
                                podsAvailable, x );

                if( chosenPOD < 0 ) break;

                podsRemaining = x[0];
                podsSelected++;

                result.add( podIdLocation[chosenPOD] );

                // The travel path may not be a straight line due to the removal
                // of census blocks already assigned to other pods.  Assigning
                // or allocating a census block results in its removal from the
                // available status.

                for( int i=0; i<path.length; i++ )
                {
                    catchmentPop += popCBMap[ path[i] ];
                    catchment.add( path[i] );
                    cbAvailable[ path[i] ] = false;
                }
            }

            if( chosenPOD < 0 ) break;

            selectableCensusBlocks = null;

            // Catchment area may already equal or exceed goal.
            if( catchmentPop < optimalValue )
            {
                // Recompute the shortest path now from the census block of
                // the selected POD.
                predecessor = Dijkstra( cbAdjacency, cbAvailable, path[0] );

                // Find the list of PODs adjacent to the current allocation.
                selectableCensusBlocks =
                        initialSelectableCBs( cbAdjacency, path, cbAvailable );

                // Until the catchment area has at least the required population
                // (or all census blocks have been assigned).
                do
                {
                    // Select the adjacent census block with the highest
                    // population/travel distance ratio.
                    selectedCensusBlock =
                            selectCensusBlockByRatio( popCBMap,
                            predecessor, selectableCensusBlocks );

                    if( selectedCensusBlock < 0 ) break;

                    cbAvailable[selectedCensusBlock] = false;
                    catchment.add( selectedCensusBlock );
                    catchmentPop += popCBMap[ selectedCensusBlock ];

                    // If possible, increase the adjacency list related to the
                    // newest census block added.
                    selectableCensusBlocks =
                        extendSelectableCBs( selectedCensusBlock,
                            selectableCensusBlocks,
                            cbAdjacency, path, cbAvailable );

                } while( catchmentPop < optimalValue );

                System.out.println("Chosen POD\t"+podIds[chosenPOD]+
                        "\tCatchment Population\t"+catchmentPop+
                        "\tOptimal Value\t"+optimalValue);
                continue;
            }
            continue;
        } while ( (0 < podsRemaining) && (podsSelected < numPods) );

        String values = "(";
        Iterator<POD> it = result.iterator();
        boolean first = true;
        while (it.hasNext()) {
            POD temp = it.next();
            if (!first) {
                values += ",";
            }
            first = false;
            values += temp.getId();
        }
        values += ")";

        Map<Integer, Integer> distro =
                db.populationDistribution(m, pop, values);

        this.finalDistro = distro;
        this.pods = result;

        return;
    }

    public int chosePod( int podCensusBlock[], int cb,
            boolean podsAvailable[], int podCount[] )
    {
        int result = -1;

        for( int i=0; i<podCensusBlock.length; i++ )
        {
            if( podsAvailable[i] )
                if( podCensusBlock[i] == cb )
                {
                    result = i;
                    podCount[0]--;
                    podsAvailable[i] = false;

                    for( int j=i+1; j<podCensusBlock.length; j++ )
                    {
                        if( podCensusBlock[j] == cb )
                        {
                            podCount[0]--;
                            podsAvailable[j] = false;
                        }
                    }

                    break;
                }
        }

        return result;
    }

    public CensusBlockDistance[] Dijkstra(
            TreeSet cbAdjacency[],
            boolean cbAvailable[],
            int blockId )
    {
        /*
         *  1  function Dijkstra(Graph, source):
         *         // Initializations
         *  2      for each vertex v in Graph:
         *             // Unknown distance function from source to v
         *  3          dist[v] := infinity ;
         *             // Previous node in optimal path from source
         *  4          previous[v] := undefined ;
         *  5      end for ;
         *         // Distance from source to source
         *  6      dist[source] := 0 ;
         */

        CensusBlockDistance result[] =
                new CensusBlockDistance[ cbAvailable.length ];

        for( int i=0; i<cbAvailable.length; i++ )
        {
            result[i] = new CensusBlockDistance( -1, -1, Double.MAX_VALUE);
        }

        result[blockId].distance = 0;

        /*
         *  7      Q := the set of all nodes in Graph ;
         *         // All nodes in the graph are unoptimized - thus are in Q
         */

        Comparator<CensusBlockDistance> comparator =
                new DistanceAdjacencyComparator();

        PriorityQueue<CensusBlockDistance> Q =
                new PriorityQueue<CensusBlockDistance>
                        ( cbAvailable.length, comparator );

        for( int i=0; i<cbAvailable.length; i++ )
        {
            Q.add( new CensusBlockDistance( i, -1, result[i].distance ) );
        }

        /*
         *         // The main loop
         *  8      while Q is not empty:
         */
        while( !Q.isEmpty() )
        {
        /*
         *  9          u := vertex in Q with smallest distance in dist[] ;
         * 13          remove u from Q ;
         */

            CensusBlockDistance u = Q.poll();

        /*
         * 10          if dist[u] = infinity:
             *             // all remaining vertices are
             *             // inaccessible from source
         * 11              break ;
         * 12          end if ;
         */

            if( u.distance == Double.MAX_VALUE ) break;

        /*
             *         // where v has not yet been removed from Q.
         * 14          for each neighbor v of u:
         */
//            CensusBlockDistance ua[] =
//                    (CensusBlockDistance[]) cbAdjacency[u.target].toArray();
//            for( int i=0; i<ua.length; i++ )
            Iterator it = cbAdjacency[u.target].iterator();

            while( it.hasNext() )
            {
                CensusBlockDistance ua = (CensusBlockDistance) it.next();
        /*
         * 15              alt := dist[u] + dist_between(u, v) ;
                 *         // Relax (u,v,a)
         * 16              if alt < dist[v]:
         */
                double alt = result[u.target].distance + ua.distance;
                if( alt < result[ua.target].distance )
                {
        /*
         * 17                  dist[v] := alt ;
         * 18                  previous[v] := u ;
                     *         // Reorder v in the Queue
         * 19                  decrease-key v in Q;
         * 20              end if ;
         */
                    Q.remove( new CensusBlockDistance( ua.target, -1,
                            result[ua.target].distance ) );
                    result[ua.target].distance = alt;
                    result[ua.target].target = u.target;
                    Q.add(  new CensusBlockDistance( ua.target, -1,
                            result[ua.target].distance ) );
                }

        /*
         * 21          end for ;
         */
            }

        /*
         * 22      end while ;
         */
        }

        /*
         * 23      return dist[] ;
         * 24  end Dijkstra.
         *
         */
        return result;
    }

    private void trashOutput()
    {
/*
        FileOutputStream xyzzy = null;
        try {
            xyzzy = new FileOutputStream( "xyzzy.prn" );
        } catch( Exception ex ) {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex );
        }

        String plugh =
            "Index\tID\tCentroid\tPopulation\tAvailable\tAdjacency\n";

        try {
            xyzzy.write( plugh.getBytes() );
        } catch (Exception ex ) {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex );
        }

        for( int i=0; i<cbIdHash.size(); i++ )
        {
            plugh = i + "\t" + censusBlockIds[i] + "\t" + cbCentroids[i] + "\t"
                    + popCBMap[i] + "\t" + cbAvailable[i];

            CensusBlockDistance ua[] =
                new CensusBlockDistance[ cbAdjacency[i].size() ];

//            ua = cbAdjacency[i].toArray( ua );
            for( int j=0; j<ua.length; j++ )
            {
                ua[j] = cbAdjacency[i].pollFirst();
            }

            for( int j=0; j<ua.length; j++ )
            {
                cbAdjacency[i].add( ua[j] );
            }

            for( int j=0; j<ua.length; j++ )
            {
                plugh += ( "\t" + ua[j].toString() );
            }

            plugh += "\n";

            try {
                xyzzy.write( plugh.getBytes() );
            } catch (Exception ex ) {
                Logger.getLogger(
                        DBInteractions.class.getName()).log(Level.SEVERE,
                        null, ex );
            }
        }

        plugh = "Pods\tIndex\tId\tCentroid\tCensus Block\tAvailable\n";

        try {
            xyzzy.write( plugh.getBytes() );
        } catch (Exception ex ) {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex );
        }

        for( int i=0; i<cbIdHash.size(); i++ )
        {
            plugh = i + "\t" + PODs.get( i ).toString() + "\t" +
                    podCensusBlock[i] +
                    "\t" + podsAvailable[i] + "\n";

            try {
                xyzzy.write( plugh.getBytes() );
            } catch (Exception ex ) {
                Logger.getLogger(
                        DBInteractions.class.getName()).log(Level.SEVERE,
                        null, ex );
            }
        }

        try {
            xyzzy.close();
        } catch (Exception ex ) {
            Logger.getLogger(
                    DBInteractions.class.getName()).log(Level.SEVERE,
                    null, ex );
        }
*/
    }

    public TreeSet extendSelectableCBs( int selectedCensusBlock,
            TreeSet<Integer> selectableCensusBlocks,
            TreeSet<CensusBlockDistance> cbAdjacency[],
            int path[], boolean cbAvailable[] )
    {
        CensusBlockDistance temp = null;

        Iterator it;

        TreeSet result = new TreeSet();

        result.addAll( selectableCensusBlocks );
        result.remove( selectedCensusBlock );

        it = cbAdjacency[selectedCensusBlock].iterator();
        while (it.hasNext()) {
            temp = (CensusBlockDistance) it.next();
            if( cbAvailable[temp.target] )
                result.add(temp.target);
        }

        return result;
    }

    // Returns the path from the closest POD to the Census Block with
    // the highest population.
    public int[] findPathToCensusBlockHoldingClosestPOD(
            CensusBlockDistance predecessor[],
            int blockId,
            int podCensusBlock[],
            boolean podsAvailable[] )
    {
        int result[] = null;
        int target   = -1;
        double dist  = Double.MAX_VALUE;

        // Find a POD which is in a census block with the smallest distance
        // from the census block with the greatest population.
        for( int i=0; i<podCensusBlock.length; i++ ) {
            if( podsAvailable[i] )
                if( predecessor[podCensusBlock[i]].distance < dist )
                {
                    target = podCensusBlock[i];
                    dist   = predecessor[target].distance;
                }
        }

        // Count the number of intermediate census blocks
        int n = 1;
        int p = target;
        while( p != blockId )
        {
            n++;
            p = predecessor[p].target;
        }

        // Allocate space and fill path
        result = new int[n];
        result[0] = target;
        for( int i=1; i<n; i++ )
        {
            result[i] = predecessor[result[i-1]].target;
        }

        return result;
    }

    // Find the list of PODs adjacent to the current allocation.
    public TreeSet<Integer> initialSelectableCBs(
            TreeSet<CensusBlockDistance> cbAdjacency[],
            int path[],
            boolean cbAvailable[] )
    {
        CensusBlockDistance temp = null;

        Iterator it;

        TreeSet result = new TreeSet();

        for( int i=0; i<path.length; i++ )
        {
            it = cbAdjacency[i].iterator();
            while (it.hasNext()) {
                temp = (CensusBlockDistance) it.next();
                if( cbAvailable[temp.target] )
                    result.add(temp.target);
            }
        }

        return result;
    }

    // Selects which Census Block has the highest population/distance ratio.
    public int selectCensusBlockByRatio( int popCBMap[],
            CensusBlockDistance predecessor[],
            TreeSet<Integer> selectableCensusBlocks )
    {
        double ratio = Double.MIN_VALUE;
        double temp = -1;

        int current = -1;
        int result = -1;

        Iterator it = selectableCensusBlocks.iterator();

        while( it.hasNext() )
        {
            current = ( (Integer) it.next() ).intValue();
            temp    = popCBMap[current] / predecessor[current].distance;
            if( temp > ratio )
            {
                result = current;
                ratio  = temp;
            }
        }

        return result;
    }

    private void removePODWithOptima() {
        double optimalValue = 0;
        if (m.get(Tables.CRITERION).equals(Tables.ADJUSTING_OPTIMA)) {
            optimalValue = (double) ((double) this.population / (double) (pods.
                    size() - 1));
        } else if (m.get(Tables.CRITERION).equals(Tables.FIXED)) {
            optimalValue = (double) ((double) this.population / (double) (new Integer(m.
                    get(Tables.NUM_PODS))));
        }

        System.out.println("optimal value = " + optimalValue);
        Iterator<POD> it = pods.iterator();
        double minMaxDeviation = Double.MAX_VALUE;
        POD toRemove = null;
        while (it.hasNext()) {
            double deviation = 0;
            // this is the POD that will be excluded
            POD pod = it.next();
            String values = "(";
            Iterator<POD> it2 = pods.iterator();
            boolean first = true;
            while (it2.hasNext()) {
                POD temp = it2.next();
                if (temp.getId() != pod.getId()) {
                    if (!first) {
                        values += ",";
                    }
                    first = false;
                    values += temp.getId();
                }
            }
            values += ")";

            Map<Integer, Integer> distro = db.populationDistribution(m, pop,
                    values);

            it2 = pods.iterator();
            while (it2.hasNext()) {
                POD temp = it2.next();
                if (temp.getId() != pod.getId()) {
                    double currPop = (double) distro.get(temp.getId());
                    double newDeviation = Math.abs(optimalValue - currPop);
                    System.out.println("id = " + temp.getId()
                            + " optimal value = " + optimalValue
                            + "\t currPop = " + currPop + "\t new deviation = "
                            + newDeviation);
                    if (newDeviation > deviation) {
                        deviation = newDeviation;
                    }
                }
            }
            if (deviation < minMaxDeviation) {
                this.finalDistro = distro;
                toRemove = pod;
                minMaxDeviation = deviation;
            }
            System.out.println("deviation " + deviation);
            System.out.println("=======");
        }
        System.out.println("Removing POD " + toRemove.getId()
                + " with deviation of " + minMaxDeviation);
        pods.remove(toRemove);
    }


    

     private void printPODNumbers(int[] selected){
        for(int i = 0; i < selected.length; i++){
            System.out.print(selected[i] + "\t");
        }
        System.out.println();
     }

    private void printDistro() {
        Iterator<POD> it = pods.iterator();
        System.out.println("========== Population distribution ===========");
        while (it.hasNext()) {
            POD temp = it.next();
            System.out.println(temp.getId() + "\t " + this.finalDistro.get(temp.
                    getId()));
        }
        System.out.println("=====================");
    }
}
