/*
 *      REPLANMapLayerTableCellRenderer of GEOTools REPLANMapLayerTableCellRenderer that does everything the same but 
 *      leaves out certain icons along with their actions that are not useful at this point
 *      in REPLAN. This code is copied over from the GEOTools source file and then changed 
 *      or commented out.
 *  
 * 
 * 
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package edu.unt.cerl.replan.view.mainframe;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.geotools.map.FeatureLayer;

import org.geotools.map.MapLayer;
import org.geotools.map.StyleLayer;
import org.geotools.swing.MapLayerTableCellRenderer;

/**
 *
 *  (Changes made to the original source file for custom look/icons)
 */

/**
 * A custom list cell renderer for items in the JList used by {@linkplain MapLayerTable}
 * to show map layer names and states.
 *
 * @author Michael Bedward
 * @since 2.6
 * @source $URL: http://svn.osgeo.org/geotools/tags/2.7-M3/modules/unsupported/swing/src/main/java/org/geotools/swing/MapLayerTableCellRenderer.java $
 * @version $Id: REPLANMapLayerTableCellRenderer.java,v 1.5 2013-10-24 17:55:47 josh Exp $
 */

public class REPLANMapLayerTableCellRenderer extends JPanel implements ListCellRenderer {
private static final long serialVersionUID = 7907189175227502588L;

    //private static final ResourceBundle stringRes = ResourceBundle.getBundle("org/geotools/swing/Text");

    /**
     * Items used to display layer states and controls. Each item has
     * one or two icons associated with it: one for simple controls,
     * two for toggle controls.
     */
    public static enum LayerControlItem {
        /**
         * Layer visibility - whether the layer will be shown or hidden
         * when the map display is drawn
         */
        VISIBLE(
            new ImageIcon("eye_open.png"),
            new ImageIcon("eye_closed.png")
                /*
            new ImageIcon(MapLayerTableCellRenderer.class.getResource(
                "/org/geotools/swing/icons/eye_open.png")),
            new ImageIcon(MapLayerTableCellRenderer.class.getResource(
                "/org/geotools/swing/icons/eye_closed.png"))
               */ 
        ),

        /**
         * Layer selection - the selected status of layers can be used
         * to include or exclude them in map queries etc.
         */
        SELECTED(
            new ImageIcon("tick.png"),
            new ImageIcon("cross.png")
                /*
            new ImageIcon(MapLayerTableCellRenderer.class.getResource(
                "/org/geotools/swing/icons/tick.png")),
            new ImageIcon(MapLayerTableCellRenderer.class.getResource(
                "/org/geotools/swing/icons/cross.png"))
                */
        ),

        /**
         * Layer style - to open a style dialog for the layer
         */
        STYLE(
            new ImageIcon("style_layer.png"),null    
           /*
            new ImageIcon(MapLayerTableCellRenderer.class.getResource(
                "/org/geotools/swing/icons/style_layer.png")),
            null // no off state for this label
            */
        ),
        
        REMOVE(
            new ImageIcon("remove_layer.png"),null     
                /*
            new ImageIcon(MapLayerTableCellRenderer.class.getResource(
                "/org/geotools/swing/icons/remove_layer.png")),
            null // no off state for this label
            */
        ),
        
        
        
        //Create Custom Icons
            // Blank Space For Formatting
        BLANKSPACE(
                new ImageIcon("blankSpace.png"),null
        ),
            // Roads
        ROADS(
                new ImageIcon("roads_Icon.png"),null
        ),
            // PODs
        PODS(
                new ImageIcon("PODs_Icon.png"),null
        ),
            // County Outline
        COUNTYOUTLINE(
                new ImageIcon("countyOutline_Icon.png"),null
        ),
            // Catchment Areas
        CATCHMENTAREAS(
                new ImageIcon("catchment_Icon.png"),null
        ),
            // Crossing Points
        CROSSINGPOINTS(
                new ImageIcon("crossingPoints_Icon.png"),null
        ),
        // Rings
        RINGS(
                new ImageIcon("rings_Icon.png"),null
        ),
            // Cencus Blocks
        CENCUSBLOCKS(
                new ImageIcon("cencusBlocks_Icon.png"),null
        );
        
        
        
        

        private ImageIcon onIcon;
        private ImageIcon offIcon;

        /**
         * Private constructor
         * @param onIcon icon for the 'on' state
         * @param offIcon icon for the 'off' state
         */
        private LayerControlItem(ImageIcon onIcon, ImageIcon offIcon) {
            this.onIcon = onIcon;
            this.offIcon = offIcon;
        }

        /**
         * Get the icon used to signify the 'on' state for toggle controls
         * or the single icon for non-toggle controls
         *
         * @return the icon
         */
        public Icon getIcon() {
            return onIcon;
        }

        /**
         * Get the icon used to signify the 'off' state. If called for a non-toggle
         * control this returns the single icon.
         *
         * @return the icon
         */
        public Icon getOffIcon() {
            if (offIcon != null) {
                return offIcon;
            } else {
                return onIcon;
            }
        }
    }
    
    private final static int CELL_PADDING = 5;
    private final static int CELL_HEIGHT;
    private final static Rectangle SELECT_LABEL_BOUNDS;
    private final static Rectangle VISIBLE_LABEL_BOUNDS;
    private final static Rectangle STYLE_LABEL_BOUNDS;
    private final static Rectangle REMOVE_LABEL_BOUNDS;
    private final static Rectangle NAME_LABEL_BOUNDS;

    static {
        int maxIconHeight = 0;
        for (LayerControlItem state : LayerControlItem.values()) {
            maxIconHeight = Math.max(maxIconHeight, state.getIcon().getIconHeight());
        }
        CELL_HEIGHT = maxIconHeight + 2*CELL_PADDING;

        int x = CELL_PADDING;
        int h = LayerControlItem.VISIBLE.getIcon().getIconHeight();
        int w = LayerControlItem.VISIBLE.getIcon().getIconWidth();
        VISIBLE_LABEL_BOUNDS = new Rectangle(x, CELL_PADDING, w, h);
        x += w + CELL_PADDING;

        h = LayerControlItem.SELECTED.getIcon().getIconHeight();
        w = LayerControlItem.SELECTED.getIcon().getIconWidth();
        SELECT_LABEL_BOUNDS = new Rectangle(x, CELL_PADDING, w, h);
        x += w + CELL_PADDING;

        h = LayerControlItem.STYLE.getIcon().getIconHeight();
        w = LayerControlItem.STYLE.getIcon().getIconWidth();
        STYLE_LABEL_BOUNDS = new Rectangle(x, CELL_PADDING, w, h);
        x += w + CELL_PADDING;

        h = LayerControlItem.REMOVE.getIcon().getIconHeight();
        w = LayerControlItem.REMOVE.getIcon().getIconWidth();
        REMOVE_LABEL_BOUNDS = new Rectangle(x, CELL_PADDING, w, h);
        x += w + CELL_PADDING;

        NAME_LABEL_BOUNDS = new Rectangle(x, CELL_PADDING, 1000, CELL_HEIGHT - 2*CELL_PADDING);
    }

    private JLabel visibleLabel;
    private JLabel selectedLabel;
    private JLabel styleLabel;
    private JLabel removeLayerLabel;
    private JLabel nameLabel;
    
    // Custom JLabels for icons
    private JLabel blankSpaceLabel;
    private JLabel cencusBlocksLabel;
    private JLabel roadsLabel;
    private JLabel podsLabel;
    private JLabel countyOutlineLabel;
    private JLabel catchmentAreaLabel;
    private JLabel ringsLabel;
    private JLabel crossingPointsLabel;


    /**
     * Get the constant height that will be used for list cells
     * @return cell height in pixels
     */
    public static int getCellHeight() {
        return CELL_HEIGHT;
    }

    /**
     * Check if a point representing a mouse click location lies within
     * the bounds of the layer visibility label
     * @param p coords of the mouse click; relative to this cell's origin
     * @return true if the point is within the label bounds; false otherwise
     */
    public static boolean hitVisibilityLabel(Point p) {
        return VISIBLE_LABEL_BOUNDS.contains(p);
    }

    /**
     * Check if a point representing a mouse click location lies within
     * the bounds of the layer selection label
     * @param p coords of the mouse click; relative to this cell's origin
     * @return true if the point is within the label bounds; false otherwise
     */
    public static boolean hitSelectionLabel(Point p) {
        return SELECT_LABEL_BOUNDS.contains(p);
    }

    /**
     * Check if a point representing a mouse click location lies within
     * the bounds of the layer style label
     * @param p coords of the mouse click; relative to this cell's origin
     * @return true if the point is within the label bounds; false otherwise
     */
    public static boolean hitStyleLabel(Point p) {
        return STYLE_LABEL_BOUNDS.contains(p);
    }

    /**
     * Check if a point representing a mouse click location lies within
     * the bounds of the remove layer label
     * @param p coords of the mouse click; relative to this cell's origin
     * @return true if the point is within the label bounds; false otherwise
     */
    public static boolean hitRemoveLabel(Point p) {
        return REMOVE_LABEL_BOUNDS.contains(p);
    }

    public static boolean hitNameLabel(Point p) {
        return NAME_LABEL_BOUNDS.contains(p);
    }


    /**
     * Constructor
     */
    public REPLANMapLayerTableCellRenderer() {
        super(new FlowLayout(FlowLayout.LEFT, CELL_PADDING, CELL_PADDING));
        
        // First Alwasy add the visible icon
        visibleLabel = new JLabel();
        add(visibleLabel);
        
        // Add custom icons for legend
            // Blank space for formatting
        blankSpaceLabel = new JLabel(LayerControlItem.BLANKSPACE.getIcon());
        add(blankSpaceLabel);
            // Cencus Blocks
        cencusBlocksLabel = new JLabel(LayerControlItem.CENCUSBLOCKS.getIcon());
        add(cencusBlocksLabel);
        cencusBlocksLabel.setVisible(false);
            // Roads
        roadsLabel = new JLabel (LayerControlItem.ROADS.getIcon());
        add(roadsLabel);
        roadsLabel.setVisible(false);
            // PODS
        podsLabel = new JLabel (LayerControlItem.PODS.getIcon());
        add(podsLabel);
        podsLabel.setVisible(false);
            // County Outline
        countyOutlineLabel = new JLabel (LayerControlItem.COUNTYOUTLINE.getIcon());
        add(countyOutlineLabel);
        countyOutlineLabel.setVisible(false);
            // Catchment Area
        catchmentAreaLabel = new JLabel (LayerControlItem.CATCHMENTAREAS.getIcon());
        add(catchmentAreaLabel);
        catchmentAreaLabel.setVisible(false);
            // Rings
        ringsLabel = new JLabel (LayerControlItem.RINGS.getIcon());
        add(ringsLabel);
        ringsLabel.setVisible(false);
            // Crossing Points
        crossingPointsLabel = new JLabel (LayerControlItem.CROSSINGPOINTS.getIcon());
        add(crossingPointsLabel);
        crossingPointsLabel.setVisible(false);  
        
       
        // Dont add these label because they are not useful in this version and are the reason for
        // the creation of this custom class of the GIS MapLayerTableCellRenderer
        // (Note: They are still created and kept (just not added) to keep most of the default code in place)
        
        selectedLabel = new JLabel();
        //add(selectedLabel);
        styleLabel = new JLabel(LayerControlItem.STYLE.getIcon());
        //add(styleLabel);
        removeLayerLabel = new JLabel(LayerControlItem.REMOVE.getIcon());
        //add(removeLayerLabel);
        
        
        
        // Add name label last
        nameLabel = new JLabel();
        add(nameLabel);
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value, // value to display
            int index, // cell index
            boolean isSelected, // is the cell selected
            boolean cellHasFocus) // the list and the cell have the focus
    {
        FeatureLayer layer = (FeatureLayer) value;
        //MapLayer layer = (MapLayer)value;
        String name = layer.getTitle();
        if (name == null || name.trim().length() == 0) {
            name = layer.getFeatureSource().getName().getLocalPart();
        }
        nameLabel.setText(name);

        visibleLabel.setIcon(
                layer.isVisible() ? 
                    LayerControlItem.VISIBLE.getIcon() : LayerControlItem.VISIBLE.getOffIcon());

        selectedLabel.setIcon(
                layer.isSelected() ?
                    LayerControlItem.SELECTED.getIcon() : LayerControlItem.SELECTED.getOffIcon());
        
        
       /*
        * Names That can be given for different layers:
        * 
        * PODs
          Census Blocks
          Roads
          County outline
          Catchment Areas
          Rings
          Crossing Points
        */
        
        // Check the current label and set its specific icon
            // Roads
        if (name.equals("Roads")){
            roadsLabel.setVisible(true);
        }
        else {
            roadsLabel.setVisible(false);
        }
            // Cencus Blocks
        if (name.equals("Census Blocks")){
            cencusBlocksLabel.setVisible(true);
        }
        else {
            cencusBlocksLabel.setVisible(false);
        }
            // PODs
        if (name.equals("PODs")){
            podsLabel.setVisible(true);
        }
        else {
            podsLabel.setVisible(false);
        }
            // County Outline
        if (name.equals("County outline")){
            countyOutlineLabel.setVisible(true);
        }
        else {
            countyOutlineLabel.setVisible(false);
        }
            // Catchment Areas
        if (name.equals("Catchment Areas")){
            catchmentAreaLabel.setVisible(true);
        }
        else {
            catchmentAreaLabel.setVisible(false);
        }
        // Crossing Points
        if (name.equals("Crossing Points")){
            crossingPointsLabel.setVisible(true);
        }
        else {
            crossingPointsLabel.setVisible(false);
        }
        // Rings
        if (name.equals("Rings")){
            ringsLabel.setVisible(true);
        }
        else {
            ringsLabel.setVisible(false);
        }
        
        
         
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}