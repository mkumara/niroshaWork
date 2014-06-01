package edu.unt.cerl.applicationframework.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.JInternalFrame;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class DefaultGeoInternalFrame extends JInternalFrame {

    private int width;
    private int height;
    private String name;
    private int id;
    private MapContent map;

    public DefaultGeoInternalFrame(int width, int height, int id) throws
            IOException {
        // resizable, closable, maximizable, iconifiable
        super("unnamed", true, true, true, true);
        this.initFrame(width, height, id);

    }

    public DefaultGeoInternalFrame(int width, int height) throws IOException {
                // resizable, closable, maximizable, iconifiable
        super("unnamed", true, false, true, true);
        this.initFrame(width, height, -1);
    }

        public DefaultGeoInternalFrame(String frameName, int width, int height, int id) throws
            IOException {
        // resizable, closable, maximizable, iconifiable
        super(frameName, true, false, true, true);
        this.initFrame(width, height, id);

    }

    public DefaultGeoInternalFrame(String frameName,int width, int height) throws IOException {
                // resizable, closable, maximizable, iconifiable
        super(frameName, true, false, true, true);
        this.initFrame(width, height, -1);
    }

    private void initFrame(int width, int height, int id){
                this.width = width;
        this.height = height;
        this.id = id;
        this.setSize(width, height);
        this.setMinimumSize(new Dimension(width, height));
        this.setVisible(rootPaneCheckingEnabled);
    }

    private void init(FeatureSource fs, Style style) throws IOException {

        this.setBackground(Color.yellow);
        map = new MapContent();
          Layer l = new FeatureLayer(fs,style);
        map.addLayer(l);
        JMapPane mapPane = new JMapPane();
        mapPane.setRenderer(new StreamingRenderer());
        mapPane.setMapContent(map);
        mapPane.setSize(width, height);
        mapPane.setBackground(Color.WHITE);
        this.setSize(width, height);
        this.setLayout(new BorderLayout());
        this.add(mapPane, BorderLayout.CENTER);

    }

    public void addLayerFromFile(File f, Style style) throws IOException {
        FileDataStore store =
                FileDataStoreFinder.getDataStore(f);
        FeatureSource fs = store.getFeatureSource();
        if (map == null) {
            this.init(fs, style);
        } else {
                   Layer l = new FeatureLayer(fs,style);
        map.addLayer(l);
        }
    }

    public void addLayerFromDB(String tableName, Style style, Map postgisParams)
            throws IOException {
        if (!postgisParams.containsKey("schema")) {
            postgisParams.put("schema", "public");
        }
        DataStore pgDatastore = DataStoreFinder.getDataStore(postgisParams);
        FeatureSource fs = pgDatastore.getFeatureSource(tableName);


        if (map == null) {
            this.init(fs, style);
        } else {
                 Layer l = new FeatureLayer(fs,style);
        map.addLayer(l);
        }
    }
}
