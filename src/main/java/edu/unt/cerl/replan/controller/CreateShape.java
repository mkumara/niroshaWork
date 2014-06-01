/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller;

import edu.unt.cerl.replan.REPLAN;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
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
 * @author sarat
 */
public class CreateShape {

    public static void createShpFromPostgis(String tableName, File file) throws MalformedURLException, IOException,
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
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params_file);
        newDataStore.createSchema(TYPE);
        newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
        Transaction transaction = new DefaultTransaction("create");
        String typeName = newDataStore.getTypeNames()[0];
        FeatureStore<SimpleFeatureType, SimpleFeature> featureStore =
                (FeatureStore<SimpleFeatureType, SimpleFeature>) newDataStore.getFeatureSource(typeName);
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
        pgDatastore.dispose();

        System.out.println(file.getAbsolutePath());
    }

    public static SimpleFeatureType createFeatureTypeFromDB(FeatureType ft) {
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
}
