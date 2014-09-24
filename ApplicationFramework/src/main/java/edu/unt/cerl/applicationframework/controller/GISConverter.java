
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.applicationframework.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class GISConverter {

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
    public static void createPostgisFromShp(File source, String target, Map post_params,
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

    }

    /**
     * Creates a feature type based on the feature type obtained from a shp file
     *
     * @param ft feature type obtained from a shp file
     *
     * @return feature type
     */
    public static SimpleFeatureType createFeatureTypeFromShp(FeatureType ft,
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
}
