package edu.unt.cerl.replan.controller;

import com.vividsolutions.jts.geom.Point;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.db.GeneralDBQueries;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

/**
 * Class RingSegmentCreator
 */
public class RingSegmentCreator implements PopulationAtCrossingPointEstimator {

    //
    // Fields
    //
    private static int sectorId;
    private Statement stmt;
    private String prefix;
    private ScenarioState scenario;
    private Connection c;

    //
    // Constructors
    //
    public RingSegmentCreator(ScenarioState s, Connection c) {
        this.scenario = s;
        this.c = c;
    }

    ;

    private void prepareTables(String prefix, Connection c, Statement stmt)
            throws SQLException {
        String b2pTable = UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX;
        String cpTable = UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX;
        //String b2pTable = ScenarioState.getAuthor() + "." + this.scenario.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX;  //UserState.getSchemaPrefix() + prefix + "_block_to_pod";
        //String cpTable = ScenarioState.getAuthor() + "." + this.scenario.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX;  //UserState.getSchemaPrefix() + prefix + "_crossingpoints";

        String query;

        if (GeneralDBQueries.columnExists(ScenarioState.getAuthor(), prefix + DefaultConstants.B2P_SUFFIX, "sector", c)) {
            System.out.println("Dropping column sector from b2ptable\n");
            query = "ALTER table " + b2pTable + " DROP COLUMN sector;";
            stmt.executeUpdate(query);
        }
        query = "ALTER table " + b2pTable + " ADD COLUMN sector integer;";
        System.out.println(query);
        stmt.executeUpdate(query);

        if (GeneralDBQueries.columnExists(ScenarioState.getAuthor(), prefix + DefaultConstants.CROSSINGPT_SUFFIX, "sector", c)) {
            query = "ALTER table " + cpTable + " DROP COLUMN sector;";
            stmt.executeUpdate(query);
        }
        query = "ALTER table " + cpTable + " ADD COLUMN sector integer;";
        System.out.println(query);
        stmt.executeUpdate(query);


        if (GeneralDBQueries.columnExists(ScenarioState.getAuthor(), prefix + DefaultConstants.CROSSINGPT_SUFFIX, "feeds_into_sector", c)) {
            query = "ALTER table " + cpTable + " DROP COLUMN feeds_into_sector;";
            stmt.executeUpdate(query);
        }
        query = "ALTER table " + cpTable
                + " ADD COLUMN feeds_into_sector integer;";
        System.out.println(query);
        stmt.executeUpdate(query);

    }

    /**
     * Creates a linked list for each ring and stores it in the matrix position
     * according to pod number and ring number. Each linked list contains
     * Features of census block centroids (id and location)
     *
     * @param prefix the prefix used for the corresponding workingcpy
     * @param numPods number of PODs of the scenario
     * @param numRings number of rings
     * @return a matrix of linked lists, which is indexed by POD number and ring
     * number
     * @throws IOException
     */
    private LinkedList<Feature>[][] readCensusBlocks(String prefix, int numPods,
            int numRings) throws IOException {
        //GISConversionTools ct = new GISConversionTools();
        String centroidTable = UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX;
        //String centroidTable = ScenarioState.getAuthor() + "." + scenario.getWorkingCopyName() +"_"+ DefaultConstants.CENTROID_TABLE; //(String) scenario.getScenarioState().getSettings().get("centroid_table");

        /**
         * FOR TESTING ONLY TAKE OUT
         */
        //  ct.setDBParams();
        //  ct.initTestUserSchemaProperties();
        /**
         * END FOR TESTING ONLY TAKE OUT
         */
        LinkedList<Feature>[][] mappings = new LinkedList[numPods][numRings];

        //initialize lists
        for (int i = 0; i < numPods; i++) {
            for (int j = 0; j < numRings; j++) {
                mappings[i][j] = new LinkedList<Feature>();
            }
        }
        //DataStore pgDatastore = DataStoreFinder.getDataStore(ct.
        //        getPOSTGIS_USERSCHEMA());
        //DataStore pgDatastore = DataStoreFinder.getDataStore(ct.getPOSTGIS_USERSCHEMA());
        DataStore pgDatastore = DataStoreFinder.getDataStore(REPLAN.getController().getPostGIS());
        //String tableName = prefix + "_block_to_pod";
        String tableName = prefix + DefaultConstants.B2P_SUFFIX;
        //  System.out.println("table = " + tableName);
        FeatureSource fs =
                pgDatastore.getFeatureSource(tableName);
        FeatureCollection b2p = fs.getFeatures();
        /**
         * Store the Feature for each census block at the array position
         * corresponding to its id
         */
        //pgDatastore = DataStoreFinder.getDataStore(ct.getPOSTGIS());
        //pgDatastore = DataStoreFinder.getDataStore(REPLAN.getController().getPostGIS());
        //fs = pgDatastore.getFeatureSource(centroidTable);
        fs = pgDatastore.getFeatureSource(scenario.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX);
        FeatureCollection blocks = fs.getFeatures();
        //Feature[] censusblocks = new Feature[blocks.size()];
        HashMap censusblocks = new HashMap();
        FeatureIterator it = blocks.features();
        int counter = 0;
        while (it.hasNext()) {
            Feature f = it.next();
//            int id = ((Integer) f.getProperty("id").getValue());
//            censusblocks[id - 1] = f;
            int blockNum = ((Integer) f.getProperty("logrecno").getValue());
            censusblocks.put(blockNum, f);
            counter++;
        }
        it = b2p.features();
        /**
         * Store the Feature for each census block in a Linked list. The linked
         * list is stored at the matrix position corresponding to the pod and
         * ring the census block belongs to.
         */
        while (it.hasNext()) {
            Feature f = it.next();
            int block = ((Integer) f.getProperty("block").getValue());
            int pod = ((Integer) f.getProperty("pod").getValue());
            int ring = ((Integer) f.getProperty("ring").getValue());
            //Feature feat = censusblocks[block - 1];
            Feature feat = (Feature) censusblocks.get(block);
            System.out.println("readCensusBlocks 167 pod = " + pod + " out of "
                    + numPods + " ring = " + ring + " out of " + numRings);
            mappings[pod - 1][ring - 1].add(feat);
        }
        it.close();
        pgDatastore.dispose();
        return mappings;
    }

    /**
     * Creates a linked list for each ring and stores it in the matrix position
     * according to pod number and ring number. Each linked list contains
     * Features of crossing points
     *
     * @param prefix the prefix used for the corresponding workingcpy
     * @param numPods number of PODs of the scenario
     * @param numRings number of rings
     * @return a matrix of linked lists, which is indexed by POD number and ring
     * number
     * @throws IOException
     */
    private LinkedList<Feature>[][] readCrossingPoints(String prefix,
            int numPods, int numRings) throws IOException {
        //GISConversionTools ct = new GISConversionTools();
        LinkedList<Feature>[][] mappings = new LinkedList[numPods][numRings];

        //initialize lists
        for (int i = 0; i < numPods; i++) {
            for (int j = 0; j < numRings; j++) {
                mappings[i][j] = new LinkedList<Feature>();
            }
        }


        /**
         * FOR TESTING ONLY TAKE OUT
         */
        //   ct.setDBParams();
        //   ct.initTestUserSchemaProperties();
        /**
         * END FOR TESTING ONLY TAKE OUT
         */
//        DataStore pgDatastore = DataStoreFinder.getDataStore(ct.
//                getPOSTGIS_USERSCHEMA());
        DataStore pgDatastore = DataStoreFinder.getDataStore(REPLAN.getController().getPostGIS());
//        FeatureSource fs = pgDatastore.getFeatureSource(prefix
//                + "_crossingpoints");
        FeatureSource fs = pgDatastore.getFeatureSource(prefix
                + DefaultConstants.CROSSINGPT_SUFFIX);
        FeatureCollection crossingpts = fs.getFeatures();
        FeatureIterator it = crossingpts.features();

        /**
         * Store the Feature for each crossing point in a Linked list. The
         * linked list is stored at the matrix position corresponding to the pod
         * and ring the crossing point belongs to.
         */
        while (it.hasNext()) {
            Feature f = it.next();
            int pod = ((Integer) f.getProperty("pod").getValue());
            int ring = ((Integer) f.getProperty("ring").getValue());
            mappings[pod - 1][ring - 1].add(f);
        }

        for (int i = 0; i < numPods; i++) {
            for (int j = 0; j < numRings; j++) {
                System.out.print(mappings[i][j].size() + "\t");
            }
            System.out.println();
        }
        it.close();
        pgDatastore.dispose();

        return mappings;
    }

    private Feature[] getPODs(String prefix) throws IOException {
//        GISConversionTools ct = new GISConversionTools();
        /**
         * FOR TESTING ONLY TAKE OUT
         */
        //   ct.setDBParams();
        //    ct.initTestUserSchemaProperties();
        /**
         * END FOR TESTING ONLY TAKE OUT
         */
        DataStore pgDatastore = DataStoreFinder.getDataStore(REPLAN.getController().getPostGIS());
//        DataStore pgDatastore = DataStoreFinder.getDataStore(ct.
//                getPOSTGIS_USERSCHEMA());
        FeatureSource fs = pgDatastore.getFeatureSource(prefix + DefaultConstants.POD_SUFFIX);
        FeatureCollection pods = fs.getFeatures();
        Feature[] podArray = new Feature[pods.size()];
        FeatureIterator it = pods.features();
        int index = 0;
        while (it.hasNext()) {
            Feature f = it.next();
            podArray[index] = f;
            index++;
        }
        pgDatastore.dispose();
        it.close();
        return podArray;

    }

    /**
     * Partition the geographic space along the X-axis
     *
     * @param o origin (location of POD)
     * @param l list of crossing points
     * @param b list of census blocks
     * @param s list of super-centroids
     * @throws SQLException
     */
    private void partitionX(Point o, LinkedList<Feature> l,
            LinkedList<Feature> b, LinkedList<Feature> s) throws SQLException {
        //System.out.println("partitionX");
        //   System.out.println("pod position = " +o.getX()+" " + o.getY());

        // 2 linked lists for the crossing points
        LinkedList<Feature> left = new LinkedList<Feature>();
        LinkedList<Feature> right = new LinkedList<Feature>();

        // 2 linked lists for the census blocks
        LinkedList<Feature> left_b = new LinkedList<Feature>();
        LinkedList<Feature> right_b = new LinkedList<Feature>();

        // 2 linked lists for the super centroids
        LinkedList<Feature> left_s = new LinkedList<Feature>();
        LinkedList<Feature> right_s = new LinkedList<Feature>();

        // separate crossing points at x=0
        Iterator<Feature> it = l.iterator();
        //   System.out.println("\n first split: 190 \n");
        //   System.out.println("size = " + l.size());
        while (it.hasNext()) {
            Feature f = it.next();
            //    System.out.println("feature around line 192: " + f);
            Point p = (Point) f.getDefaultGeometryProperty().getValue();
            //int pod = ((Integer) f.getProperty("pod").getValue());
            // System.out.println("pod = " + pod);
            //  System.out.println("point position = " + p.getX() + " " + p.getY());
            if (p.getX() - o.getX() >= 0) {
                right.add(f);
            } else {
                left.add(f);
            }
        }
        //         System.out.println("size left = " + left.size());
        //  System.out.println("size  right = " + right.size());
        // separate blocks at x=0
        it = b.iterator();
        while (it.hasNext()) {
            Feature f = it.next();
            Point p = (Point) f.getDefaultGeometryProperty().getValue();
            if (p.getX() - o.getX() >= 0) {
                right_b.add(f);
            } else {
                left_b.add(f);
            }
        }

        // separate super centroids at x=0
        if (s != null) {
            it = s.iterator();
            while (it.hasNext()) {
                Feature f = it.next();
                Point p = (Point) f.getDefaultGeometryProperty().getValue();
                if (p.getX() - o.getX() >= 0) {

                    right_s.add(f);
                } else {
                    left_s.add(f);
                }
            }
        } else {
            right_s = null;
            left_s = null;
        }
//        System.out.println("size right = " + right.size());
//        System.out.println("size left = " + left.size());
//        System.out.println("size right_b = " + right_b.size());
//        System.out.println("size left_b = " + left_b.size());
        if (!right.isEmpty() && !left.isEmpty() && !right_b.isEmpty() && !left_b.isEmpty()) {
            //       System.out.println("Dividing the left side");
            this.divide(o, left, left_b, left_s, 0, 0.5);
            //        System.out.println("\n=================== \n ================== \n");
            //       System.out.println("Dividing the right side");
            this.divide(o, right, right_b, right_s, 0, 0.5);
        } else {
            //    System.out.println("Do not divide");
            sectorId++;
            this.writeSectorIdToDB(b, sectorId, DefaultConstants.B2P_SUFFIX, "block");
            //     System.out.println("still ok");
            this.writeSectorIdToDB(l, sectorId, DefaultConstants.CROSSINGPT_SUFFIX, "id");
            if (s != null) {
                this.writeCrossingpointFeedsIntoToDB(s, sectorId);
            }
        }
    }

    // Divide sectors recursively into small sectors
    private void divide(Point o, LinkedList<Feature> l, LinkedList<Feature> b,
            LinkedList<Feature> s, double nom, double denom) throws SQLException {
        double alpha = nom / denom * 45;
        // 2 linked lists for the crossing points
        LinkedList<Feature> l1 = new LinkedList<Feature>();
        LinkedList<Feature> l2 = new LinkedList<Feature>();
        // 2 linked lists for the census blocks
        LinkedList<Feature> b1 = new LinkedList<Feature>();
        LinkedList<Feature> b2 = new LinkedList<Feature>();
        // 2 linked lists for the super centroids
        LinkedList<Feature> s1 = new LinkedList<Feature>();
        LinkedList<Feature> s2 = new LinkedList<Feature>();

//        System.out.println("tan(" + alpha + ")=" + Math.tan(Math.toRadians(alpha)));

        // split crossing points
        //      System.out.println("Number of crossingpoints to split = " + l.size() );
        Iterator<Feature> it = l.iterator();
        while (it.hasNext()) {

            Feature f = it.next();


            Property p2 = f.getProperty("id");
            //System.out.println("property = " + p);
//            Number id = ((Number) p2.getValue());
            //      System.out.println(p2);

            Point p = (Point) f.getDefaultGeometryProperty().getValue();
            double x = p.getX() - o.getX();
            double y = p.getY() - o.getY();
            double sign = Math.signum(x * y);
            if (sign == 0) {
                sign = Math.signum(x) + Math.signum(y);
            }

            if (y >= (sign * Math.tan(Math.toRadians(alpha)) * x)) {
                l1.add(f);
            } else {
                l2.add(f);
            }
        }

        // split census blocks
        it = b.iterator();
        while (it.hasNext()) {
            Feature f = it.next();
            Point p = (Point) f.getDefaultGeometryProperty().getValue();
            double x = p.getX() - o.getX();
            double y = p.getY() - o.getY();
            double sign = Math.signum(x * y);
            if (sign == 0) {
                sign = Math.signum(x) + Math.signum(y);
            }

            if (y >= (sign * Math.tan(Math.toRadians(alpha)) * x)) {
                b1.add(f);
            } else {
                b2.add(f);
            }
        }

        // split super centroids
        if (s != null) {
            it = s.iterator();
            while (it.hasNext()) {
                Feature f = it.next();
                Point p = (Point) f.getDefaultGeometryProperty().getValue();
                double x = p.getX() - o.getX();
                double y = p.getY() - o.getY();
                double sign = Math.signum(x * y);
                if (sign == 0) {
                    sign = Math.signum(x) + Math.signum(y);
                }

                if (y >= (sign * Math.tan(Math.toRadians(alpha)) * x)) {
                    s1.add(f);
                } else {
                    s2.add(f);
                }
            }
        } else {
            s1 = null;
            s2 = null;
        }

        if (!l1.isEmpty() && !l2.isEmpty() && !b1.isEmpty() && !b2.isEmpty()) {
            double y_sign = Math.signum(((Point) l1.getFirst().
                    getDefaultGeometryProperty().getValue()).getY() - o.getY());
            if (y_sign == 0) {
                y_sign = 1;
            }
            this.divide(o, l1, b1, s1, Math.abs(2 * nom + y_sign), 2 * denom);
            this.divide(o, l2, b2, s2, Math.abs(2 * nom - y_sign), 2 * denom);
        } else {
            sectorId++;
//            System.out.println("END");
//            System.out.println("nom=" + nom);
//            System.out.println("denom=" + denom);
//            System.out.println("size=" + l.size());
//            System.out.println("block size=" + b.size());
//            System.out.println("sector id =  " + this.sectorId);
//            System.out.println("=================");

            this.writeSectorIdToDB(b, sectorId, DefaultConstants.B2P_SUFFIX, "block");
            this.writeSectorIdToDB(l, sectorId, DefaultConstants.CROSSINGPT_SUFFIX, "id");
            if (s != null) {
                this.writeCrossingpointFeedsIntoToDB(s, sectorId);
            }
        }
    }

    public void writeCrossingpointFeedsIntoToDB(List<Feature> s, int sector)
            throws SQLException {

        //String schema = ScenarioState.getAuthor() + ".";  //UserState.getSchemaPrefix();
        //schema = "tamara.";
        //String schema = UserState.userId;
        Iterator<Feature> it = s.iterator();
        while (it.hasNext()) {
            Feature f = it.next();
            Number id = ((Number) f.getProperty("id").getValue());
//            String query = "UPDATE " + schema + prefix + "_crossingpoints "
//                    + " SET feeds_into_sector = " + sector
//                    + " WHERE " + "id = " + id + ";";
            String cptable = UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX;
            String query = "UPDATE " + cptable
                    + " SET feeds_into_sector = " + sector
                    + " WHERE " + "id = " + id + ";";
            stmt.executeUpdate(query);
        }
    }

    public void writeSectorIdToDB(List<Feature> l, int sector, String table,
            String idName) throws SQLException {
        //write sector ids to b2p and to crossingpoints
        //  System.out.println("\n ------ \n table = " + table + " \n ------ \n" );
        //  System.out.println("idName = " + idName);
        Iterator<Feature> it = l.iterator();
        Property p;
        //  System.out.println("list size = " + l.size());
        while (it.hasNext()) {
            Feature f = it.next();
            //    if(!table.equals("block_to_pod")){System.out.println("====>" + f);}
            if (idName.contentEquals("block")) {
                p = f.getProperty("logrecno");
            } else {
                p = f.getProperty("id");
            }

            //      System.out.println("property = " + p);
            //   System.out.println(p.getValue());
            Number id = ((Number) p.getValue());
            //       System.out.println("id =" + id);

            // It looks like the following string should have the . at the end???
            //String schema = ScenarioState.getAuthor() + ".";  //UserState.getSchemaPrefix();
            //           schema = "tamara.";
            String schema = UserState.userId;
//            String query = "UPDATE " + schema + prefix + "_" + table
//                    + " SET sector = " + sector
//                    + " WHERE " + idName + " = " + id + ";";
            String query = "UPDATE " + schema + "." + scenario.getWorkingCopyName() + table
                    + " SET sector = " + sector
                    + " WHERE " + idName + " = " + id + ";";
            stmt.executeUpdate(query);
        }
    }

    public void assignInnerRingSector(int pod, int numRings, String prefix,
            int value) throws SQLException {
//        String query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
//                + "_block_to_pod "
//                + " SET sector = " + value
//                + " WHERE ring = 1 AND pod = " + pod + ";";
        String query = "UPDATE " + UserState.userId + "." + scenario.getWorkingCopyName()
                + DefaultConstants.B2P_SUFFIX
                + " SET sector = " + value
                + " WHERE ring = 1 AND pod = " + pod + ";";
        System.out.println(query);
        stmt.executeUpdate(query);
    }

    public void population(Connection c) throws SQLException {
//        String schema = ScenarioState.getAuthor() + ".";  //UserState.getSchemaPrefix();
        //schema = UserState.getSchemaPrefix();
        String query;
        if (GeneralDBQueries.columnExists(UserState.userId, prefix + DefaultConstants.CROSSINGPT_SUFFIX,
                "population", c)) {
//            query = "ALTER table " + schema + prefix + "_crossingpoints"
//                    + " DROP COLUMN population;";
            query = "ALTER table " + UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX
                    + " DROP COLUMN population;";
            stmt.executeUpdate(query);
        }
//        query = "ALTER TABLE " + schema + prefix
//                + "_crossingpoints ADD COLUMN population INTEGER;";
        query = "ALTER TABLE " + UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX
                + " ADD COLUMN population INTEGER;";
        System.out.println(query);
        stmt.executeUpdate(query);
    }

    private void calculatePopulationAtCrossingpoints(int numRings) throws
            SQLException {
        // sum up the population within each sector
//        String tableName = ScenarioState.getAuthor()+"." + prefix
//                + "_crossingpoints";
        String tableName = UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX;
//        String populationTable =   ScenarioState.getAuthor() + "." + scenario.getWorkingCopyName() + DefaultConstants.POPULATION_TABLE;  //(String) scenario.getScenarioState().getSettings().get("population_table");
        String populationTable = UserState.userId + "." + scenario.getWorkingCopyName() + DefaultConstants.POPULATION_SUFFIX;
//               String query = "UPDATE " + tableName
//                + " SET population = tmp2.population "
//                + "FROM (SELECT sector, SUM(population) as population "
//                + "FROM (SELECT distinct b2p.block, cpts.sector, p.population "
//                + " FROM " + ScenarioState.getAuthor() + "." + prefix
//                + "_block_to_pod b2p, "
//                + tableName + " cpts, " + populationTable + " p "
//                + " WHERE cpts.sector = b2p.sector AND b2p.block = p.block_id) "
//                + " AS tmp GROUP BY sector) AS tmp2"
//                + " WHERE " + ScenarioState.getAuthor() + "." + prefix
//                + "_crossingpoints.sector = tmp2.sector;";

//                       String query = "UPDATE " + tableName
//                + " SET population = tmp2.population "
//                + "FROM (SELECT sector, SUM(population) as population "
//                + "FROM (SELECT distinct b2p.block, cpts.sector, p.population "
//                + " FROM " + ScenarioState.getAuthor() + "." + prefix
//                + DefaultConstants.B2P_SUFFIX +" b2p, "
//                + tableName + " cpts, " + populationTable + " p "
//                + " WHERE cpts.sector = b2p.sector AND b2p.block = p.block_id) "
//                + " AS tmp GROUP BY sector) AS tmp2"
//                + " WHERE " + ScenarioState.getAuthor() + "." + scenario.getWorkingCopyName()
//                + DefaultConstants.CROSSINGPT_SUFFIX+".sector = tmp2.sector;";

        String query = "UPDATE " + tableName
                + " SET population = tmp2.population "
                + "FROM (SELECT sector, SUM(population) as population "
                + "FROM (SELECT distinct b2p.block, cpts.sector, p.p0010001 AS population"
                + " FROM " + ScenarioState.getAuthor() + "." + prefix
                + DefaultConstants.B2P_SUFFIX + " b2p, "
                + tableName + " cpts, " + populationTable + " p "
                + " WHERE cpts.sector = b2p.sector AND b2p.block = p.logrecno) "
                + " AS tmp GROUP BY sector) AS tmp2"
                + " WHERE " + ScenarioState.getAuthor() + "." + scenario.getWorkingCopyName()
                + DefaultConstants.CROSSINGPT_SUFFIX + ".sector = tmp2.sector;";


        System.out.println(query);
        stmt.executeUpdate(query);
    }

    public void hybridMethod(int numPods, int numRings)
            throws IOException, SQLException {

        //GISConversionTools gisConvTools = new GISConversionTools();
        //Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        stmt = REPLAN.getController().getConnection().createStatement();
        prefix = scenario.getWorkingCopyName();
        System.out.println("scenario.getWorkingCopyName() is " + prefix);
        this.prepareTables(prefix, c, stmt);

        sectorId = 0;

        /**
         * Read all census blocks and crossingpoints into matrices indicating
         * which pod and ring they are assigned to Read all pods into an array
         */
        LinkedList<Feature>[][] blocks = this.readCensusBlocks(prefix, numPods,
                numRings);
        LinkedList<Feature>[][] cpts = this.readCrossingPoints(prefix, numPods,
                numRings);
        Feature[] pods = this.getPODs(prefix);


        /**
         * POD i is indexed as (i-1) Census blocks of ring k lead into crossing
         * points of ring (k-1)
         */
        /**
         * Divide the geographic space using x=0, whereas setting the pod
         * location to the origin = (0,0)
         */
        LinkedList<Feature> l = new LinkedList<Feature>(); // crossing points
        LinkedList<Feature> b = new LinkedList<Feature>(); // blocks
        LinkedList<Feature> s = new LinkedList<Feature>(); // super centroids

        // iterate through all pods
        for (int i = 1; i <= numPods; i++) {
            // for each pod iterate through all rings except inner ring
            for (int j = numRings; j > 1; j--) {
                /*
                 * For current ring and pod assign blocks and crossingpoints
                 * they are feeding into to l and b
                 */
                l = cpts[i - 1][j - 2];
                b = blocks[i - 1][j - 1];
                System.out.println("====== pod = " + i + " and ring = " + j);
                System.out.println("size of l = " + l.size() + " and size of b = " + b.size());

                /*
                 * After the first iteration (outmost ring completed)
                 * super-centroids have to be considered
                 */
                if (j < numRings) {
                    System.out.println("Assign super centroids");
                    // super centroids = crossing points from prior ring
                    s = cpts[i - 1][j - 1];
                } else {
                    s = null;
                }
                /*
                 * Get the location of the current POD as the origin and start
                 * partitioning crossing points, blocks and super-centroids
                 */
                Point o = (Point) pods[i - 1].getDefaultGeometryProperty().
                        getValue();
                this.partitionX(o, l, b, s);

            }
            /*
             * all crossing points into the inner circle feed into the same
             * sector (circle itself)
             */
            sectorId++;
            l = cpts[i - 1][0];
            if (s != null) {
                this.writeCrossingpointFeedsIntoToDB(l, sectorId);
            }
            this.assignInnerRingSector(i, numRings, prefix, sectorId);

        }
        //add the population column
        this.population(c);
        //sum up the population for each sector
        this.calculatePopulationAtCrossingpoints(numRings);
        stmt.close();
        //c.close();

    }

    //
    // Methods
    //
    //
    // Accessor methods
    //
    //
    // Other methods
    //
    /**
     */
    public void generatePopulationEstimates() {
    }
}
