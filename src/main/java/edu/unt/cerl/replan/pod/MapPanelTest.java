package edu.unt.cerl.replan.pod;

import edu.unt.cerl.replan.controller.GISConversionTools;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.geotools.swing.JMapPane;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.FeatureSourceMapLayer;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.renderer.GTRenderer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.MapLayerTable;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;

/**
 *
 * @author tamara
 */
public class MapPanelTest {

    public static void main(String args[]) throws IOException, SQLException {
        GISConversionTools gisConvTools = new GISConversionTools();
        GISTools gisTools = new GISTools();
        StyleTools podTools = new StyleTools();

        DataStore pgDatastore = DataStoreFinder.getDataStore(gisConvTools.getPOSTGIS());


        // Create a map context and add our shapefile to it
        MapContext map = new DefaultMapContext();
        map.setTitle("Tarrant County");
        FeatureSource fs;
//        final JMapPane mapPane = new JMapPane(new StreamingRenderer(), map);
        final JMapPane mapPane = new JMapPane();
        mapPane.setMapContent(map);
        GTRenderer renderer = new StreamingRenderer();
        mapPane.setRenderer(renderer);

        MapLayerTable table = new MapLayerTable(mapPane);

         fs = pgDatastore.getFeatureSource("blocks");
         MapLayer blockLayer =  new FeatureSourceMapLayer(fs, podTools.createCatchmentStyle(), "Census Blocks");
         map.addLayer(blockLayer);

        fs = pgDatastore.getFeatureSource("catchment");
        MapLayer catchmentLayer =  new FeatureSourceMapLayer(fs, podTools.createCatchmentStyle(), "Census Blocks");
         map.addLayer(catchmentLayer);

        fs = pgDatastore.getFeatureSource("pods");
        // MapLayer podLayer =  new FeatureSourceMapLayer(fs, podTools.createPODStyle(), "Census Blocks");
     //    map.addLayer(podLayer);

         blockLayer.setVisible(false);

        // JMapFrame.showMap(map);

        JFrame frame = new JFrame("Text");
        frame.setVisible(true);
        Container contentpane = frame.getContentPane();
        contentpane.setLayout(new BorderLayout());
        contentpane.add(mapPane, BorderLayout.CENTER);
        contentpane.add(table,BorderLayout.EAST);

        JPanel buttons = new JPanel();
        JButton zoomInButton = new JButton(new ZoomInAction(mapPane));
        JButton zoomOutButton = new JButton(new ZoomOutAction(mapPane));
        JButton panButton = new JButton(new PanAction(mapPane));

        buttons.add(zoomInButton);

        buttons.add(zoomOutButton);
        buttons.add(panButton);

        frame.add(buttons, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}

