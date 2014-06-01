package edu.unt.cerl.replan.controller;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import edu.unt.cerl.applicationframework.model.DBInfo;
//import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.styling.Style;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.JOptionPane;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Provides methods for the conversion between different GIS storage options
 *
 * @author tamara
 */
public class GISConversionTools {

    private HashMap postgis_params = new HashMap();
    private HashMap postgis_userschema_params = new HashMap();
    private String connection;
    private Properties jdbc_properties = new Properties();
    private Properties jdbc_userschema_properties = new Properties();
    private ScenarioState scenarioState;

    public GISConversionTools() {
        //this.setDBParams();
        //postgis_userschema_params();
    }

    public void setDBParams() {

        //

        //Map<String, String> m = DBInfo.getParams();
        Map<String, String> m = REPLAN.getController().getPostGIS(); //DBInfo.getParams();
        System.out.println(m.get("host") + " " + m.get("port") + " " + m.get("database"));
        this.connection = "jdbc:postgresql://" + m.get("host") + ":" + m.get(
                "port") + "/" + m.get("database");
        postgis_params.put("dbtype", "postgis");        //must be postgis
        postgis_params.put("host", m.get("host"));   //the name or ip address of the machine running PostGIS
        if (m.get("port") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL PORT ERROR: Make sure DatabaseSettings.txt is in same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        postgis_params.put("port", new Integer(m.get("port")));  //the port that PostGIS is running on (generally 5432)
        postgis_params.put("database", m.get("database"));      //the name of the database to connect to.
        if (m.get("user") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL USER ERROR: Make sure DatabaseSettings.txt is in same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        postgis_params.put("user", m.get("user"));         //the user to connect with
        if (m.get("passwd") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL PASSWORD ERROR: Make sure DatabaseSettings.txt is in same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        postgis_params.put("passwd", m.get("passwd"));   //the password of the user.
        if (m.get("role") == null) {
            JOptionPane.showMessageDialog(null,
                    "NULL ROLE ERROR: Make sure DatabaseSettings.txt is in same directory as executable jar file!",
                    "RE-PLAN Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        postgis_params.put(("role"), m.get("role"));
        jdbc_properties.put("user", m.get("user"));
        jdbc_properties.put("password", m.get("passwd"));
        jdbc_properties.put("role", m.get("role"));
    }

    /**
     * Initialize the PostGIS and JDBC parameters
     */
    private void postgis_userschema_params() {

        if (ScenarioState.getAuthor() != null) {
            this.initUserSchemaProperties();
        }
    }

    public void initUserSchemaProperties() {
        this.jdbc_userschema_properties = (Properties) this.jdbc_properties.
                clone();
        if (ScenarioState.getAuthor() != null) {
            this.jdbc_userschema_properties.put("schema", ScenarioState.getAuthor());
        }
        this.postgis_userschema_params = (HashMap) this.postgis_params.clone();
        this.postgis_userschema_params.put("schema", ScenarioState.getAuthor());
        // this.postgis_userschema_params.put(, this)
    }

    public void initTestUserSchemaProperties() {
        this.jdbc_userschema_properties = (Properties) this.jdbc_properties.
                clone();
        this.jdbc_userschema_properties.put("schema", "tamara");
        this.postgis_userschema_params = (HashMap) this.postgis_params.clone();
        this.postgis_userschema_params.put("schema", "tamara");
        // this.postgis_userschema_params.put(, this)
    }

    /**
     * Displays a single shapefile in a JMapFrame
     *
     * @throws IOException
     * @throws SchemaException
     */
    public void displayShapefile() throws IOException, SchemaException {

        //  File file = JFileDataStoreChooser.showOpenFile("shp", null);
        File file = new File("test");
        if (file == null) {
            System.out.println("------> null");
            return;
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        // SimpleFeatureType type = store.getSchema();
        FeatureSource featureSource = store.getFeatureSource();

        // Create a map context and add our shapefile to it
        MapContext map = new DefaultMapContext();
        map.setTitle(featureSource.getName().toString());
        map.addLayer(featureSource, null);

        // Now display the map
        JMapFrame.showMap(map);

    }

    /**
     * Displays a single layer from the PostGIS DB in a JMapFrame
     *
     * @param tableName name of the table to be displayed

     * @throws IOException
     */
    public void displayMapFromPostgis(String tableName, Map post_params,
            Style style) throws IOException {

        DataStore pgDatastore = DataStoreFinder.getDataStore(post_params);
        FeatureSource fs = pgDatastore.getFeatureSource(tableName);
        // Create a map context and add our shapefile to it
        MapContext map = new DefaultMapContext();

        map.setTitle(fs.getName().toString());
        map.addLayer(fs, style);

        // Now display the map
        JMapFrame.showMap(map);

    }

    /**
     * Creates a shapefile from a PostGIS table
     *
     * @param tableName name of the table to be converted
     * @param file shapefile to write to
     *
     * @throws MalformedURLException
     * @throws IOException
     * @throws SchemaException
     */
    public void createShpFromPostgis(String tableName, File file,
            Map post_params) throws MalformedURLException, IOException,
            SchemaException {

        // read table and create feature type based on its schema

        //DataStore pgDatastore = DataStoreFinder.getDataStore(post_params);
        DataStore pgDatastore = DataStoreFinder.getDataStore(REPLAN.getController().getPostGIS());
        FeatureSource fs = pgDatastore.getFeatureSource(tableName);
        FeatureType ft = fs.getSchema();
        String geom_descriptor = ft.getGeometryDescriptor().getName().toString();
        SimpleFeatureType TYPE = createFeatureTypeFromDB(ft);

        // iterate throught he features of the table and add them to a feature
        // starting with the geometry feature type
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection =
                FeatureCollections.newCollection();
        FeatureIterator f_it = fs.getFeatures().features();
        while (f_it.hasNext()) {
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
            Feature feat = f_it.next();
            featureBuilder.add(feat.getDefaultGeometryProperty().getValue());
            Iterator<Property> prop_it = feat.getProperties().iterator();
            while (prop_it.hasNext()) {
                Property p = prop_it.next();
                if (geom_descriptor.equals(p.getType().getName().toString())) {
                    continue;
                }
                if (p.getValue() != null) {
                    featureBuilder.add(p.getValue());
                } else {
                    featureBuilder.add(null);
                }

            }
            SimpleFeature feature = featureBuilder.buildFeature(null);
            collection.add(feature);
        }

        // create shapefile based on type and store the featues in it
        DataStoreFactorySpi dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params_file =
                new HashMap<String, Serializable>();
        params_file.put("url", file.toURI().toURL());
        params_file.put("create spatial index", Boolean.TRUE);
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.
                createNewDataStore(params_file);
        newDataStore.createSchema(TYPE);
        newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
        Transaction transaction = new DefaultTransaction("create");
        String typeName = newDataStore.getTypeNames()[0];
        FeatureStore<SimpleFeatureType, SimpleFeature> featureStore =
                (FeatureStore<SimpleFeatureType, SimpleFeature>) newDataStore.
                getFeatureSource(typeName);
        featureStore.setTransaction(transaction);
        try {
            featureStore.addFeatures(collection);
            transaction.commit();
        } catch (Exception problem) {
            problem.printStackTrace();
            transaction.rollback();

        } finally {
            transaction.close();
        }

        System.out.println(file.getAbsolutePath());
    }

    /**
     * Read in a POD CSV and convert it into a shapefile
     *
     * @param source the CSV file
     * @param target the resutling SHP file
     *
     * @throws MalformedURLException
     * @throws IOException
     * @throws SchemaException
     */
    public void createShpFromPODs(File source, File target) throws
            MalformedURLException, IOException, SchemaException {

        // create a POD feature type and build it
        final SimpleFeatureType TYPE = createPODFeatureType(source.getName());
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection =
                FeatureCollections.newCollection();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(
                null);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

        // read in CSV line by line and add it to a feature collection
        Scanner s = new Scanner(new BufferedReader(new FileReader(source)));
        s.nextLine();
        while (s.hasNext()) {
            String line = s.nextLine();
            Scanner tokens = new Scanner(line);
            tokens.useDelimiter("\\|");

            Integer id = tokens.nextInt();
            String name = tokens.next();
            String addy = tokens.next();
            String city = tokens.next();
            Integer zip = tokens.nextInt();
            double longitude = tokens.nextDouble();
            double latitude = tokens.nextDouble();
            Point location = geometryFactory.createPoint(new Coordinate(
                    longitude, latitude));
            String add_data = tokens.next();
            String comments = tokens.next();

            featureBuilder.add(location);
            featureBuilder.add(id);
            featureBuilder.add(name);
            featureBuilder.add(addy);
            featureBuilder.add(city);
            featureBuilder.add(zip);
            featureBuilder.add(add_data);
            featureBuilder.add(comments);

            SimpleFeature feature = featureBuilder.buildFeature(null);
            collection.add(feature);
        }

        // write the collection to a shape file
        DataStoreFactorySpi dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", target.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.
                createNewDataStore(params);
        newDataStore.createSchema(TYPE);
        newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
        Transaction transaction = new DefaultTransaction("create");
        String typeName = newDataStore.getTypeNames()[0];
        FeatureStore<SimpleFeatureType, SimpleFeature> featureStore =
                (FeatureStore<SimpleFeatureType, SimpleFeature>) newDataStore.
                getFeatureSource(typeName);
        featureStore.setTransaction(transaction);
        try {
            featureStore.addFeatures(collection);
            transaction.commit();

        } catch (Exception problem) {
            problem.printStackTrace();
            transaction.rollback();

        } finally {
            transaction.close();
        }

        System.exit(0);
    }

    /**
     * Create the feature type POD
     *
     * @return the POD SimpleFeatureType
     */
    public SimpleFeatureType createPODFeatureType(String name) {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(name);
        builder.srs("EPSG:4326");

        builder.crs(DefaultGeographicCRS.WGS84);
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

        builder.add("location", Point.class);
        // add attributes in order
        builder.add("id", Integer.class);
        //  builder.add("booths", Integer.class);
        // builder.add("type", Boolean.class);
        builder.add("name", String.class);
        builder.add("address", String.class);
        builder.add("city", String.class);
        builder.add("zip", Integer.class);

        builder.add("additional", String.class);
        builder.add("comments", String.class);
        //builder.add("on", Boolean.class);


        //System.out.println("namespace uri = " + builder.getNamespaceURI());
        //builder.length(15).add("Name", String.class); // <- 15 chars width for name field

        // build the type
        final SimpleFeatureType POD_DATA = builder.buildFeatureType();
        return POD_DATA;
    }

    /**
     * Creates a feature type for a shapefile based on the feature type
     * obtained from a PostGIS DB
     *
     * @param ft feature type obtained from a DB
     *
     * @return feature type
     */
    public SimpleFeatureType createFeatureTypeFromDB(FeatureType ft) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(ft.getName().toString());
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
        String geom_descriptor = ft.getGeometryDescriptor().getName().toString();
        builder.add(geom_descriptor, ft.getGeometryDescriptor().getType().
                getBinding());
        Collection<PropertyDescriptor> c = ft.getDescriptors();
        Iterator<PropertyDescriptor> it = c.iterator();
        while (it.hasNext()) {
            PropertyDescriptor pd = it.next();
            String feature_name = pd.getName().toString();
            if (!feature_name.equals(geom_descriptor)) {
                builder.add(feature_name, pd.getType().getBinding());
            }
        }
        final SimpleFeatureType TYPE = builder.buildFeatureType();
        System.out.println(TYPE.getGeometryDescriptor().
                getCoordinateReferenceSystem());
        return TYPE;
    }

    /**
     * Creates a feature type based on the feature type obtained from a shp file
     *
     * @param ft feature type obtained from a shp file
     *
     * @return feature type
     */
    public SimpleFeatureType createFeatureTypeFromShp(FeatureType ft,
            String target, boolean addID) {
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        //tb.setNamespaceURI( "http://acme.com");
        tb.setName(target);
        tb.srs("EPSG:4326");
        tb.crs(DefaultGeographicCRS.WGS84);

        if (addID) {
            tb.add("id", Integer.class, 4326);
        }


        Collection<PropertyDescriptor> c = ft.getDescriptors();
        Iterator<PropertyDescriptor> it = c.iterator();

        while (it.hasNext()) {
            PropertyDescriptor pd = it.next();
            String feature_name = pd.getName().toString();
            //       if (!feature_name.equals(geom_descriptor)) {
            tb.add(feature_name.toLowerCase(), pd.getType().getBinding());
            //       }
        }
        final SimpleFeatureType TYPE = tb.buildFeatureType();
        return TYPE;
    }

    /**
     * Creates a Postgis table based on a shapefile
     *
     * @param source the shape file
     * @param target the name of the database table
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void createPostgisFromShp(File source, String target, Map post_params,
            boolean addID) throws IOException, ClassNotFoundException,
            SQLException {

        // get the features from the shape file and crate a feature type
        FileDataStore fDatastore = FileDataStoreFinder.getDataStore(source);
        SimpleFeatureType type = fDatastore.getSchema();
        FeatureSource featureSource = fDatastore.getFeatureSource();
        FeatureCollection collection = featureSource.getFeatures();
        DataStore ds = (DataStore) DataStoreFinder.getDataStore(post_params);
        ds.createSchema(createFeatureTypeFromShp(type, target, addID));
        FeatureWriter fw = ds.getFeatureWriter(target, Transaction.AUTO_COMMIT);


        // create all the features of the postgis feature type and write to DB
        Iterator<Feature> it = collection.iterator();

        int counter = 1;

        while (it.hasNext()) {
            fw.hasNext();
            SimpleFeature f = (SimpleFeature) fw.next();

            if (addID) {
                f.setAttribute("id", counter);
            }

            Iterator<Property> prop_it = it.next().getProperties().iterator();
            while (prop_it.hasNext()) {
                Property p = prop_it.next();
                f.setAttribute(p.getName().toString().toLowerCase(),
                        p.getValue());
            }
            fw.write();
            counter++;
        }
        fw.close();

        // FeatureSource fs = ds.getFeatureSource(target);
        // System.out.println("Number of features = " + fs.getCount(Query.ALL));

        //name of shp file
        //String typeName = fDatastore.getTypeNames()[0];

    }

    /**
     * Read in PODs into a DB table from a pipe-delimited CSV file
     *
     * @param source CSV file
     * @param target name of database table
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void createPostgisFromPODs(File source, String target,
            Map post_params, LinkedList<String[]> pods) throws IOException,
            ClassNotFoundException, SQLException {

        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(
                null);
        JDBCDataStore ds = (JDBCDataStore) DataStoreFinder.getDataStore(
                post_params);

        String tempTarget = ScenarioState.getAuthor() + "_" + target;
        SimpleFeatureType sft = this.createPODFeatureType(tempTarget);
        ds.createSchema(sft);
        FeatureWriter fw = ds.getFeatureWriter(tempTarget,
                Transaction.AUTO_COMMIT);

        // Read in POD file line by line and create DB table entries
        Scanner s = new Scanner(new BufferedReader(new FileReader(source)));
        s.nextLine();
        while (s.hasNext()) {
            fw.hasNext();
            SimpleFeature f = (SimpleFeature) fw.next();
            String line = s.nextLine();
            Scanner tokens = new Scanner(line);
            tokens.useDelimiter("\\|");
            Integer id = tokens.nextInt();
            System.out.println(id);
            String name = tokens.next();
            String addy = tokens.next();
            String city = tokens.next();
            Integer zip = tokens.nextInt();
            double longitude = tokens.nextDouble();
            double latitude = tokens.nextDouble();
            Point location = geometryFactory.createPoint(new Coordinate(
                    longitude, latitude));
            String add_data = tokens.next();
            String comments = tokens.next();

            List l = new LinkedList();
            l.add(location);
            l.add(id);
            l.add(name);
            l.add(addy);
            l.add(city);
            l.add(zip);
            l.add(add_data);
            l.add(comments);
            f.setAttributes(l);
            fw.write();

            String[] podEntry = new String[8];
            podEntry[0] = name;
            podEntry[1] = addy;
            podEntry[2] = city;
            podEntry[3] = zip.toString();
            podEntry[4] = Double.toString(longitude);
            podEntry[5] = Double.toString(latitude);
            podEntry[6] = add_data;
            podEntry[7] = comments;
            pods.add(podEntry);

        }
        fw.close();

        this.fixSchemaAndName(tempTarget, target);
        // FeatureSource fs = ds.getFeatureSource("pod_test");
        //  System.out.println("Number of features = " + fs.getCount(Query.ALL));

    }
/*
    public void createPostgisFromPODs(String source, String target,
            Map post_params, LinkedList<Map<String, String>> pods) throws
            IOException, ClassNotFoundException, SQLException {

        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(
                null);
        JDBCDataStore ds = (JDBCDataStore) DataStoreFinder.getDataStore(
                post_params);

        String tempTarget = UserState.userId + "_" + target;
        SimpleFeatureType sft = this.createPODFeatureType(tempTarget);

//        SimpleFeatureType sft = this.createPODFeatureType(target);

        ds.createSchema(sft);


        FeatureWriter fw = ds.getFeatureWriter(tempTarget,
                Transaction.AUTO_COMMIT);

        // Read in POD file line by line and create DB table entries


        Read_POD_CSV reader = new Read_POD_CSV(source, '|', Charset.forName(
                "UTF-8"));

        for (int i = 0; i < reader.returnNumRecords(); i++) {
            fw.hasNext();
            SimpleFeature f = (SimpleFeature) fw.next();
            Integer id = reader.returnId(i);
            String name = reader.returnName(i);
            String addy = reader.returnAddress(i);
            String city = reader.returnZip(i);
            Integer zip = new Integer(reader.returnZip(i));
            Double longitude = reader.returnLongitude(i);
            Double latitude = reader.returnLatitude(i);
            Point location = geometryFactory.createPoint(new Coordinate(
                    longitude, latitude));
            String add_data = reader.returnAdditional(i);
            String comments = reader.returnComments(i);
            Integer booths = reader.returnNumBooths(i);


            // We may want to leave this as String since we convert it to String a few lines below!
            Boolean public_pod;
            if (reader.returnType(i).equals("public")) {
                public_pod = true;
            } else {
                public_pod = false;
            }

            // We may want to leave this as String since we convert it to String a few lines below!
            Boolean on; // = true;
            if (reader.returnOn(i).equals("true")) {
                on = true;
            } else {
                on = false;
            }


            Map<String, String> podEntry = new HashMap<String, String>();

            podEntry.put("id", id.toString());
            podEntry.put("name", name);
            podEntry.put("addy", addy);
            podEntry.put("city", city);
            podEntry.put("zip", zip.toString());
            podEntry.put("lon", longitude.toString());
            podEntry.put("lat", latitude.toString());
            podEntry.put("additional", add_data);
            podEntry.put("comments", comments);
            podEntry.put("booths", booths.toString());
            podEntry.put("on", on.toString());
            podEntry.put("fid", id.toString());
            podEntry.put("is_public", public_pod.toString());

            pods.add(podEntry);

            List l = new LinkedList();
            l.add(location);
            l.add(id);
            l.add(booths);
            l.add(public_pod);
            l.add(name);
            l.add(addy);
            l.add(city);
            l.add(zip);
            l.add(add_data);
            l.add(comments);
            l.add(on);
            f.setAttributes(l);
            fw.write();

        }

        fw.close();
        this.fixSchemaAndName(tempTarget, target);

    }
*/
    private void fixSchemaAndName(String tempTarget, String target) throws
            ClassNotFoundException {
        System.out.println("tempTarget = " + tempTarget);
        System.out.println("target = " + target);
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println(this.getJDBC_CONNECTION_STRING());
            Connection c = DriverManager.getConnection(this.
                    getJDBC_CONNECTION_STRING(), jdbc_properties);
            //Connection c = DriverManager.getConnection(this.getJDBC_CONNECTION_STRING());
            Statement stmt = c.createStatement();

            String query = "ALTER TABLE " + tempTarget + " SET SCHEMA " +
                    ScenarioState.getAuthor() + ";";
            System.out.println(query);
            stmt.executeUpdate(query);
            query = "ALTER TABLE " + ScenarioState.getAuthor() + "." + tempTarget +
                    " RENAME TO " + target + ";";
            System.out.println(query);
            stmt.executeUpdate(query);

            query = "DELETE FROM geometry_columns WHERE f_table_name = '" +
                    tempTarget + "';";
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(GISConversionTools.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens a file chooser for files of type shp
     *
     * @return file handle
     */
    public File openSHP() {
        return JFileDataStoreChooser.showOpenFile("shp", null);
    }

    /**
     * Opens a file chooser for files of type csv
     *
     * @return file handle
     */
    public File openCSV() {
        return JFileDataStoreChooser.showOpenFile("csv", null);
    }

    /**
     * Get parameters for PostGIS connection
     * @return
     */
    public Map getPOSTGIS() {
        return postgis_params;
    }

    public Map getPOSTGIS_USERSCHEMA() {
        return this.postgis_userschema_params;
    }

    public Map getPOSTGIS_USERSCHEMA_TAMARA() {
        this.initUserSchemaProperties();
        this.postgis_userschema_params.remove("schema");
        this.postgis_userschema_params.put("schema", "tamara");

        return this.postgis_userschema_params;
    }

    /**
     * Get properties for JDBC connection
     * @return
     */
    public Properties getJDBC() {
        return jdbc_properties;
    }

    /**
     * Get string needed for JDBC connection
     * @return
     */
    public String getJDBC_CONNECTION_STRING() {
        return connection;
    }

    public Connection getJDBC_Connection() throws SQLException {
        return DriverManager.getConnection(getJDBC_CONNECTION_STRING(),
                getJDBC());
    }

    /**
     * Get string for JDBC test connection
     * @return
     */
    public static String getJDBC_TEST_CONNECTION_STRING() {
        return "jdbc:postgresql://cerl04.cse.unt.edu:5432/gis_testing";
    }

    /**
     * JDBC connection properties for test db
     * @return
     */
    public static Properties getJDBC_TEST_PARAMS() {

        final Properties JDBC_TEST_PROPERTIES = new Properties();
        JDBC_TEST_PROPERTIES.put("user", "gis_tester");
        JDBC_TEST_PROPERTIES.put("password", "x;le9082jpd");
        return JDBC_TEST_PROPERTIES;
    }

    /**
     * PostGIS connection parameters for the test DB
     * @return
     */
    public static Map getPOSTGIS_TEST_PARAMS() {

        final Map POSTGIS_TEST_PARAMS = new HashMap();
        POSTGIS_TEST_PARAMS.put("dbtype", "postgis");        //must be postgis
        POSTGIS_TEST_PARAMS.put("host", "cerl04.cse.unt.edu");   //the name or ip address of the machine running PostGIS
        POSTGIS_TEST_PARAMS.put("port", new Integer(5432));  //the port that PostGIS is running on (generally 5432)
        POSTGIS_TEST_PARAMS.put("database", "gis_testing");      //the name of the database to connect to.
        POSTGIS_TEST_PARAMS.put("user", "gis_tester");         //the user to connect with
        POSTGIS_TEST_PARAMS.put("passwd", "x;le9082jpd");   //the password of the user.
        return POSTGIS_TEST_PARAMS;
    }
}

