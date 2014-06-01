package edu.unt.cerl.replan.pod;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.controller.GISConversionTools;
import edu.unt.cerl.replan.model.ScenarioState;
import java.sql.SQLException;
import java.util.Map;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import java.awt.Color;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.FilterFactory2;

/**
 * Provides GIS related methods
 *
 * @author tamara
 */
public class GISTools {

    GISConversionTools convTools = new GISConversionTools();

    /**
     * Calculates the centroids of a default geometry of a table
     *
     * @param tableName name of the table
     *
     * @return collection of centroids (type Point)
     *
     * @throws Exception
     */
    public Point[] calculateCentroids(String tableName) throws Exception {
        DataStore pgDatastore = DataStoreFinder.getDataStore(convTools.
                getPOSTGIS());
        FeatureSource source = pgDatastore.getFeatureSource(tableName);
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.
                getFeatures();
        FeatureIterator<SimpleFeature> iterator = features.features();
        Point[] centroids = new Point[features.size()];

        try {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                Point centroid = geom.getCentroid();
                centroids[(Integer) feature.getAttribute("id") - 1] = centroid;

            }
        } finally {
            iterator.close(); // IMPORTANT
        }
        return centroids;
    }

    /**
     * This method writes an array of points to a PostGIS database
     * @param points
     * @param post_params
     * @param tableName
     * @param pointName
     * @throws IOException
     */
    public void writePointsToPostGIS(Point[] points, Map post_params,
            String tableName, String pointName) throws IOException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(
                null);

        DataStore ds = (DataStore) DataStoreFinder.getDataStore(post_params);
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(tableName);
        builder.srs("EPSG:4326");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
        builder.add("id", Integer.class);
        builder.add(pointName, Point.class);
        final SimpleFeatureType TYPE = builder.buildFeatureType();
        ds.createSchema(TYPE);
        FeatureWriter fw = ds.getFeatureWriter(tableName,
                Transaction.AUTO_COMMIT);
        for (int i = 0; i < points.length; i++) {
            fw.hasNext();
            SimpleFeature f = (SimpleFeature) fw.next();
            f.setAttribute("id", i + 1);
            f.setAttribute(pointName, points[i]);
            fw.write();
        }

        fw.close();
    }

    /**
     * Generic method to generate a point style for GeoTools to display
     * on a map
     * @param c
     * @return
     */
    public Style createPointStyle(Color c) {
        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(
                null);
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(c), // color
                filterFactory.literal(2), // line width
                filterFactory.literal(1));           // opacity

        Fill fill = styleFactory.createFill(
                filterFactory.literal(c), // color
                filterFactory.literal(1));     // opacity

        Graphic gr = styleFactory.createDefaultGraphic();
        gr.graphicalSymbols().clear();
        Mark mark = styleFactory.getCircleMark();
        mark.setFill(fill);
        mark.setStroke(stroke);
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(3.0f));
        Symbolizer sym = styleFactory.createPointSymbolizer(gr, null);
        return SLD.wrapSymbolizers(sym);
    }

    /**
     * Calls the corresponding methods to calculate the catchment areas
     * @param prefix
     * @throws SQLException
     * @throws IOException
     */
    public void calculateCatchmentAreas(String prefix, ScenarioState s) throws
            SQLException,
            IOException {

        GISConversionTools gisConvTools = new GISConversionTools();
        Connection c = DriverManager.getConnection(gisConvTools.
                getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Statement stmt = c.createStatement();
        // create table to map block id to pod id
        this.createBlock2PodMapping(stmt, prefix, s);
        // calculate boundaries of catchment areascollection extract
        this.createCatchmentAreaPolygons(stmt, prefix, s);
        c.close();

    }

    /**
     * Callas the corresponding methods to calculate rings of proximity
     * @param prefix
     * @param number
     * @throws SQLException
     */
    public void calculateTrafficRings(String prefix, int number,
            double ringDistance, ScenarioState s) throws
            SQLException {
        GISConversionTools gisConvTools = new GISConversionTools();
        Connection c = DriverManager.getConnection(gisConvTools.
                getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        Statement stmt = c.createStatement();
        this.createRings(stmt, prefix, number, ringDistance, s);
        stmt.close();
        c.close();

    }

    /**
     * Calculates the polygons defining the catchment areas based on the assignment
     * of individual census blocks to PODs
     * @param stmt
     * @param prefix
     * @throws SQLException
     */
    public void createCatchmentAreaPolygons(Statement stmt, String prefix,
            ScenarioState s)
            throws SQLException {
        String blockTable = (String) s.getSettings().get("block_table");
        String query =
                "SELECT b2p.pod AS id, ST_UNION(b.the_geom) AS the_geom INTO " +
                ScenarioState.getAuthor() + "." + prefix + "_catchment FROM " +
                ScenarioState.getAuthor() + "." + prefix
                + DefaultConstants.B2P_SUFFIX + " AS b2p, " + blockTable
                + " AS b WHERE b2p.block=b.id GROUP BY b2p.pod;";
        System.out.println(query);
        stmt.executeUpdate(query);
    }

    /**
     * Create rings of proximity. The distance of the rings is determined
     * by an entry into a settings file.
     * @param stmt
     * @param prefix
     * @param numRings
     * @throws SQLException
     */
    public void createRings(Statement stmt, String prefix, int numRings,
            double ringDistance, ScenarioState s) throws SQLException {
        String query = "ALTER TABLE " + ScenarioState.getAuthor() + "." + prefix
                + "_block_to_pod ADD ring integer;";
        String centroidTable = (String) s.getSettings().get("centroid_table");
        System.out.println(query);
        stmt.executeUpdate(query);
        for (int i = 0; i < numRings - 1; i++) {
            double factor1 = (double) i * ringDistance;
            double factor2 = (double) (i + 1) * ringDistance;
            query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
                    + "_block_to_pod "
                    + "SET ring = " + (i + 1) + " "
                    + "FROM (SELECT b2p.block as block2 "
                    + "FROM " + ScenarioState.getAuthor() + "." + prefix
                    + "_pods p, " + ScenarioState.getAuthor() + "." + prefix
                    + "_block_to_pod b2p, " + centroidTable + " c "
                    //+ "(SELECT b2p.pod AS pod, MAX(distance(p.location,c.centroid)) AS dist "
                    //  + "FROM " + ScenarioState.getAuthor() + "." + prefix
                    //  + "_pods AS p, " + ScenarioState.getAuthor() + "." + prefix
                    //  + DefaultConstants.B2P_SUFFIX + " AS b2p, block_centroids AS c "
                    //  + "WHERE p.id=b2p.pod AND c.id=b2p.block "
                    //  + "GROUP BY b2p.pod) AS d "
                    + "WHERE p.id=b2p.pod AND c.id=b2p.block AND (p.type ='public' OR p.type = 'Public')"
                    + " AND onoff = 'true' "
                    //  + " AND p.id=d.pod "
                    + "AND distance(p.location,c.centroid)>" + factor1
                    + " "
                    + "AND distance(p.location,c.centroid)<=" + factor2
                    + ") ring_blocks "
                    + "WHERE block = ring_blocks.block2;";
            System.out.println(query);
            stmt.executeUpdate(query);
        }
        // now address the outer ring, which is everything outside the last ring
        double factor1 = (double) (numRings - 1) * ringDistance;
        query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
                + "_block_to_pod "
                + "SET ring = " + numRings + " "
                + "FROM (SELECT b2p.block as block2 "
                + "FROM " + ScenarioState.getAuthor() + "." + prefix
                + "_pods p, " + ScenarioState.getAuthor() + "." + prefix
                + "_block_to_pod b2p, " + centroidTable + " c "
                //+ "(SELECT b2p.pod AS pod, MAX(distance(p.location,c.centroid)) AS dist "
                //  + "FROM " + ScenarioState.getAuthor() + "." + prefix
                //  + "_pods AS p, " + ScenarioState.getAuthor() + "." + prefix
                //  + DefaultConstants.B2P_SUFFIX + " AS b2p, block_centroids AS c "
                //  + "WHERE p.id=b2p.pod AND c.id=b2p.block "
                //  + "GROUP BY b2p.pod) AS d "
                + "WHERE p.id=b2p.pod AND c.id=b2p.block AND (p.type ='public' OR p.type = 'Public')"
                + " AND onoff = 'true' "
                //  + " AND p.id=d.pod "
                + "AND distance(p.location,c.centroid)> " + factor1
                //+ " AND distance(p.location,c.centroid)<=dist*" + factor2
                + ") "
                + " ring_blocks "
                + "WHERE block = ring_blocks.block2;";
        System.out.println(query);
        stmt.executeUpdate(query);
        this.calculateRingPolygons(stmt, prefix, s);
    }

    /**
     * "old" method of creating rings of proximity. The maximum distance of
     * a census block to the POD divided by the number of rings is used to
     * determine the distance of the individual rings for each of the
     * catchment areas
     * @param stmt
     * @param prefix
     * @param numRings
     * @throws SQLException
     */
    public void createRingsOldMode(Statement stmt, String prefix, int numRings,
            ScenarioState s)
            throws SQLException {
        String query = "ALTER TABLE " + ScenarioState.getAuthor() + "." + prefix
                + "_block_to_pod ADD ring integer;";
        System.out.println(query);
        String centroidTable = (String) s.getSettings().get(
                "centroid_table");
        stmt.executeUpdate(query);
        for (int i = 0; i < numRings; i++) {
            double factor1 = (double) i / (double) numRings;
            double factor2 = (double) (i + 1) / (double) numRings;
            query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
                    + "_block_to_pod "
                    + "SET ring = " + (i + 1) + " "
                    + "FROM (SELECT b2p.block as block2 "
                    + "FROM " + ScenarioState.getAuthor() + "." + prefix
                    + "_pods p, " + ScenarioState.getAuthor() + "." + prefix
                    + "_block_to_pod b2p, " + centroidTable + " c, "
                    + "(SELECT b2p.pod AS pod, MAX(distance(p.location,c.centroid)) AS dist "
                    + "FROM " + ScenarioState.getAuthor() + "." + prefix
                    + "_pods AS p, " + ScenarioState.getAuthor() + "." + prefix
                    + DefaultConstants.B2P_SUFFIX + " AS b2p, " + centroidTable + " AS c "
                    + "WHERE p.id=b2p.pod AND c.id=b2p.block "
                    + "GROUP BY b2p.pod) AS d "
                    + "WHERE p.id=b2p.pod AND c.id=b2p.block AND p.id=d.pod "
                    + "AND distance(p.location,c.centroid)>dist*" + factor1
                    + " "
                    + "AND distance(p.location,c.centroid)<=dist*" + factor2
                    + ") ring_blocks "
                    + "WHERE block = ring_blocks.block2;";
            System.out.println(query);
            stmt.executeUpdate(query);

        }
        this.calculateRingPolygons(stmt, prefix, s);
    }

    /**
     * Given the census blocks belonging to each of the rings, the corresponding
     * polygons are created
     * @param stmt
     * @param prefix
     * @throws SQLException
     */
    public void calculateRingPolygons(Statement stmt, String prefix, ScenarioState s)
            throws
            SQLException {
        String blockTable = (String) s.getSettings().get(
                "block_table");
        String sequence = prefix + "_rings_seq";
        stmt.executeUpdate("CREATE SEQUENCE " + ScenarioState.getAuthor() + "."
                + sequence + ";");
        // String query = "CREATE TABLE " + prefix + "_rings ( pod INTEGER, ring INTEGER);";
        // System.out.println(query);
        //  stmt.executeUpdate(query);
        // query = "SELECT AddGeometryColumn('" + prefix + "_rings', 'the_geom',4326,'MULTIPOLYGON',2);";
        // System.out.println(query);
        // stmt.executeQuery(query);
        String query = "SELECT nextval('" + ScenarioState.getAuthor() + "." + prefix
                + "_rings_seq') AS id, b2p.pod AS pod, b2p.ring as ring, ST_UNION(b.the_geom) AS the_geom "
                + "INTO " + ScenarioState.getAuthor() + "." + prefix + "_rings "
                + "FROM " + ScenarioState.getAuthor() + "." + prefix
                + DefaultConstants.B2P_SUFFIX + " AS b2p, " + blockTable + " AS b "
                + "WHERE b2p.block=b.id "
                + "GROUP BY b2p.pod, b2p.ring;";
        System.out.println("======================");
        // query = "INSERT INTO " + prefix + "_rings (pod, ring, the_geom) " +
        //         "SELECT b2p.pod AS pod, b2p.ring as ring, ST_MultipolygonFromText(ST_AsText(ST_UNION(b.the_geom)),4326) AS the_geom " +
        //        "FROM " + prefix + DefaultConstants.B2P_SUFFIX + " AS b2p, blocks AS b " +
        //       "WHERE b2p.block=b.id " +
        //       "GROUP BY b2p.pod, b2p.ring;";
        System.out.println(query);
        stmt.executeUpdate(query);
        // query = "ALTER SEQUENCE " + prefix + "_rings_seq OWNED BY " + prefix+"_rings.fid;";
        // System.out.println(query);
        // stmt.executeUpdate(query);
        //  query = "ALTER TABLE " + prefix + "_rings ADD PRIMARY KEY (pod, ring);";
        // System.out.println("---> " + query);
        //  stmt.executeUpdate(query);
        query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
                + "_rings SET the_geom = SetSRID(the_geom,4326);";
        stmt.executeUpdate(query);
        query = "UPDATE geometry_columns SET srid=4326 WHERE f_table_name='"
                + prefix + "_rings';";
        stmt.executeUpdate(query);
        query = "ALTER TABLE " + ScenarioState.getAuthor() + "." + prefix
                + "_rings ADD PRIMARY KEY(id);";
        stmt.executeUpdate(query);
        stmt.executeUpdate("DROP SEQUENCE " + ScenarioState.getAuthor() + "."
                + prefix + "_rings_seq");

    }

    /**
     * Calculate all crossing points (intersections of the road network and ring
     * polygons)
     * @param stmt
     * @param prefix
     * @throws SQLException
     */
    public void createCrossingPoints(Statement stmt, String prefix, ScenarioState s)
            throws
            SQLException {

        String sequence_name = prefix + "_crossingpoints_seq";
        String query = "CREATE SEQUENCE " + ScenarioState.getAuthor() + "."
                + sequence_name + ";";
        stmt.executeUpdate(query);
        String roadTable = (String) s.getSettings().get(
                "road_table");

        query =
                "SELECT ST_CollectionExtract(temp.intersection,1) as crossing_point, temp.pod as pod, temp.ring as ring, nextval('"
                + ScenarioState.getAuthor() + "." + prefix
                + "_crossingpoints_seq') as id, temp.road_id as road_id "
                + "INTO " + ScenarioState.getAuthor() + "." + prefix
                + "_crossingpoints "
                + "FROM (SELECT DISTINCT ST_Intersection(ST_Boundary(ri.the_geom), ro.the_geom) AS intersection, ri.pod as pod, ri.ring as ring, ro.id as road_id "
                + "FROM " + roadTable + " ro, "
                + ScenarioState.getAuthor() + "." + prefix
                + "_rings ri WHERE ri.ring < 3 AND ST_Boundary(ri.the_geom) && ro.the_geom) AS temp "
                + "WHERE GeometryType(ST_CollectionExtract(temp.intersection,1))='POINT' OR GeometryType(ST_CollectionExtract(temp.intersection,1))='MULTIPOINT';";
        System.out.println("====\n" + query);
        stmt.executeUpdate(query);
       // query = "ALTER TABLE " + prefix + "_crossingpoints ADD PRIMARY KEY (id);";
     //   stmt.executeUpdate(query);
        //this.removeCertainCrossingPoints(stmt,  ScenarioState.getAuthor() + "." + prefix);
        //  this.createSectorIds(stmt,  ScenarioState.getAuthor() + "." + prefix);
        stmt.executeUpdate("DROP SEQUENCE " + ScenarioState.getAuthor() + "."
                + prefix + "_crossingpoints_seq");
    }

//    private void removeCertainCrossingPoints(Statement stmt, String prefix) throws SQLException {
//        String query = "DELETE FROM " + ScenarioState.getAuthor() + "." + prefix + "_crossingpoints c1 "
//                + "WHERE EXISTS (SELECT c1.crossing_point FROM " + prefix + "_crossingpoints c2 "
//                + "WHERE (c1.crossing_point = c2.crossing_point) AND (c1.pod = c2.pod) AND (c1.ring != c2.ring));";
//        System.out.println(query);
//        stmt.executeUpdate(query);
//        query = "DELETE FROM " + ScenarioState.getAuthor() + "."+ prefix + "_crossingpoints c1 "
//                + "WHERE EXISTS (SELECT c1.crossing_point FROM " + prefix + "_crossingpoints c2 "
//                + "WHERE (c1.crossing_point = c2.crossing_point) AND (c1.pod != c2.pod));";
//        System.out.println(query);
//        stmt.executeUpdate(query);
//        query = "DELETE FROM " + ScenarioState.getAuthor() + "."+ prefix + "_crossingpoints c "
//                + "WHERE EXISTS (SELECT c.crossing_point FROM tarrant t "
//                + "WHERE ST_Within(c.crossing_point,t.the_geom)=false);";
//        System.out.println(query);
//        stmt.executeUpdate(query);
//    }
    /**
     * Assign each census block to a pod
     * @param stmt
     * @param prefix
     * @throws SQLException
     */
    public void createBlock2PodMapping(Statement stmt, String prefix, ScenarioState s)
            throws
            SQLException {
        String centroidTable = (String) s.getSettings().get(
                "centroid_table");
        String query = "SELECT b2.id as block, p2.id as pod " + "INTO " +
                ScenarioState.getAuthor() + "." + prefix + "_block_to_pod FROM "
                + "(SELECT DISTINCT b.id as id,MIN(distance(b.centroid,p.location)) AS dist "
                + "FROM " + centroidTable + " AS b, " + ScenarioState.getAuthor() + "."
                + prefix + "_pods AS p "
                + " WHERE (p.type ='public' OR p.type = 'Public') AND onoff = 'true' GROUP BY b.id ) AS mins, "
                + ScenarioState.getAuthor() + "." + prefix
                + "_pods as p2, " + centroidTable + " as b2 "
                + "WHERE b2.id = mins.id "
                + " AND mins.dist = distance(b2.centroid,p2.location);";
        System.out.println(query);
        stmt.executeUpdate(query);
    }

    private void createSectorIds(Statement stmt, String prefix) throws
            SQLException {
        String query = "ALTER TABLE " + prefix
                + "_block_to_pod ADD sector integer;";
        System.out.println(query);
        stmt.executeUpdate(query);

        query = "ALTER TABLE " + prefix + "_crossingpoints ADD sector integer;";
        System.out.println(query);
        stmt.executeUpdate(query);

        query = "ALTER TABLE " + prefix
                + "_crossingpoints ADD feeds_into_sector integer;";
        System.out.println(query);
        stmt.executeUpdate(query);
    }
}
