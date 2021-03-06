package edu.unt.cerl.replan.view;

/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JComponent;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.swing.MapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.*;

/**
 * A cursor tool to zoom in the map pane display.
 * <p>
 * For mouse clicks, the display will be zoomed-in such that the 
 * map centre is the position of the mouse click and the map
 * width and height are calculated as:
 * <pre>   {@code len = len.old / z} </pre>
 * where {@code z} is the linear zoom increment (>= 1.0)
 * <p>
 * The tool also responds to the user drawing a box on the map mapPane with
 * mouse click-and-drag to define the zoomed-in area.
 * 
 * @author Michael Bedward
 * @since 2.6
 * @source $URL$
 * @version $Id: CliffMapToolIrregular.java,v 1.2.2.2 2014-09-21 14:16:28 nirosha Exp $
 */
public class CliffMapToolIrregular extends AbstractZoomTool {
    
    /** Tool name */
    public static final String TOOL_NAME = "CliffMapIrregular";//LocaleUtils.getValue("CursorTool", "ZoomIn");
    
    /** Tool tip text */
    public static final String TOOL_TIP = "Click multiple points on the Map";//LocaleUtils.getValue("CursorTool", "ClippMapTooltip");
    
    /** Cursor */
    public static final String CURSOR_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";
    
    /** Cursor hotspot coordinates */
    public static final Point CURSOR_HOTSPOT = new Point(0, 0);
    
    /** Icon for the control */
    public static final String ICON_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";
    
    private Cursor cursor;
    
    private final Point posDevice;
    private final Point firstDevice;
    private final Point posDevicePrevious;
    private final Point2D posWorld;
    private CliffCoordinateIrregular coord;
    private final JComponent parentComponent;
    private Graphics2D graphics;
    private int pointCount=0;
  
    
     /**
     * Constructor
     */
    public CliffMapToolIrregular(JComponent component) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        ImageIcon imgIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
        cursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT, TOOL_NAME);
        
        this.parentComponent=component;
        posDevice = new Point();
        firstDevice = new Point();
        posDevicePrevious = new Point();
        posWorld = new DirectPosition2D();
        coord=new CliffCoordinateIrregular();
        this.pointCount=0;
    }
    
    @Override
    public void onMouseClicked(MapMouseEvent e) {
        if(e.getClickCount()==1){
        pointCount++;    
        posDevicePrevious.setLocation(posDevice);
        posDevice.setLocation(e.getPoint());
        posWorld.setLocation(e.getWorldPos());
        
        coord.addCoordinate(new Coordinate(posWorld.getX(), posWorld.getY()));
       
         if(this.pointCount==1){
         firstDevice.setLocation(posDevice);
         }
        
        ensureGraphics();
        
        if(posDevicePrevious.x !=0){
         graphics.drawLine(posDevicePrevious.x, posDevicePrevious.y, posDevice.x, posDevice.y);
          }
        }
                
        if(e.getClickCount()>1){
          
          graphics.drawLine(posDevice.x, posDevice.y, firstDevice.x, firstDevice.y);
           Envelope2D env;
           //Insert cliff coordinate to cliffed_geographies table
            REPLAN.getQueries().updateCliffedGeographiesIrregular(this.coord);
            this.cliffGeographyIrregular();
            REPLAN.getMainFrame().getTabs().getSelectedScenario().reRender();
            env=this.coord.getEnvelope();
            getMapPane().setDisplayArea(env);
           }
    }
    
    /**
     * Records the map position of the mouse event in case this
     * button press is the beginning of a mouse drag
     *
     * @param ev the mouse event
     */
    @Override
    public void onMousePressed(MapMouseEvent ev) {
        
    }

    /**
     * Records that the mouse is being dragged
     *
     * @param ev the mouse event
     */
    @Override
    public void onMouseDragged(MapMouseEvent ev) {
       
    }

    /**
     * If the mouse was dragged, determines the bounds of the
     * box that the user defined and passes this to the mapPane's
     * {@code setDisplayArea} method.
     *
     * @param ev the mouse event
     */
    @Override
    public void onMouseReleased(MapMouseEvent ev) {
       
    }

    private void cliffGeographyIrregular(){
        
         String id = UserState.userId;
         ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
         String name =state.getWorkingCopyName();
          String[] geographies = REPLAN.getQueries().getGeographies(id,
                name, REPLAN.getController().getConnection());
         Map<String, Map> map = REPLAN.getDatasets();
         Connection c = REPLAN.getController().getConnection();
         REPLAN.getQueries().cliffRoadTableIrregular(id, name, geographies, map, c);
         REPLAN.getQueries().cliffCentroidTableIrregular(id, name, geographies, map, c);
         REPLAN.getQueries().cliffCensusBlockTableIrregular(id, name, geographies, map, c);   
    }
    /**
     * Get the mouse cursor for this tool
     */
    @Override
    public Cursor getCursor() {
        return cursor;
    }
    
    /**
     * Returns true to indicate that this tool draws a box
     * on the map display when the mouse is being dragged to
     * show the zoom-in area
     */
    @Override
    public boolean drawDragBox() {
        return false;
    }
    
    public CliffCoordinateIrregular getCliffCoordinate(){
     return this.coord;
    }
    
    /**
     * Creates and initializes the graphics object if required.
     */
    private void ensureGraphics() {
        if (graphics == null) {
            graphics = (Graphics2D) parentComponent.getGraphics().create();
            graphics.setColor(Color.WHITE);
            graphics.setXORMode(Color.GREEN);
            graphics.setStroke(new BasicStroke(2));
        }
    }
}