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
import static edu.unt.cerl.replan.view.CliffMapTool.CURSOR_IMAGE;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.Popup;
import javax.swing.PopupFactory;

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
 * @version $Id: PodInfoTool.java,v 1.2.2.3 2014-09-24 16:15:58 nirosha Exp $
 */
public class PodInfoTool extends CursorTool {
  
    /** Tool name */
    public static final String TOOL_NAME = "podinfo";//LocaleUtils.getValue("CursorTool", "ZoomIn");
    
    /** Tool tip text */
    public static final String TOOL_TIP = "Move mouse over POD";//LocaleUtils.getValue("CursorTool", "ClippMapTooltip");
    
    /** Cursor */
    public static final String CURSOR_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";
    
    /** Cursor hotspot coordinates */
    public static final Point CURSOR_HOTSPOT = new Point(14, 9);
    
    /** Icon for the control */
    public static final String ICON_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";
    
    
    
    private Cursor cursor;
    private ArrayList<PodInfo> info;
    private PodInfo currentPod;
    private JComponent component;
    private Popup pop=null;
     
   // private final Point2D startPosWorld;
   // private final Point2D endPosWorld;
    /**
     * Constructor
     */
    public PodInfoTool(JComponent mp) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        ImageIcon imgIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
        cursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT, TOOL_NAME);
        this.component=mp;
        this.info=new ArrayList<PodInfo>();
        this.loadPODs();
       }
   
    
    @Override
    public void onMouseMoved(MapMouseEvent e) {
        //System.out.println(e.getWorldPos().getX());
        if(this.check(e)){
            JButton button = new JButton(this.currentPod.getInfo());
            PopupFactory factory = PopupFactory.getSharedInstance();
            Point frameLoc=new Point(component.getLocation());
            int x = frameLoc.x + e.getX();
            int y = frameLoc.y + e.getY();
            
            if(x > (frameLoc.x + 100) && this.pop==null)
            {
            this.pop = factory.getPopup(component, button, x, y);
            this.pop.show();
            }      
        }
        else{
            if(this.pop!=null){
               this.pop.hide();
               this.pop=null;
            }
        }
         
       }
    
    @Override
    public void onMouseClicked(MapMouseEvent e) {
        //System.out.println(this.info.get(3).getInfo() );
        //System.out.println(e.getX());
        System.out.println("X= "+e.getWorldPos().getX()+" Y= "+e.getWorldPos().getY());
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

     /**
     * Get the mouse cursor for this tool
     * @return 
     */
    @Override
    public Cursor getCursor() {
        return cursor;
    }
    
    /**
     * Returns true to indicate that this tool draws a box
     * on the map display when the mouse is being dragged to
     * show the zoom-in area
     * @return 
     */
    @Override
    public boolean drawDragBox() {
        return true;
    }

    private void loadPODs() {
        //for testing hard coded list
        for(int i=0;i<10;i++)
        {
            this.info.add(new PodInfo(Integer.toString(i),(i+1),(i+100),Integer.toString(i), i, i));
        }
        
        this.info.get(3).setPos(-96.92382257837453, 33.28823772068439);
        System.out.println(this.info.get(3).getInfo());
    }

    private boolean check(MapMouseEvent e){
       
        boolean found=false;
        for(int i=0;i<this.info.size();i++)
        {
            if(this.info.get(i).check(e.getWorldPos().getX(), e.getWorldPos().getY(), 0.005))
            {
                this.currentPod=this.info.get(i);
                found=true;
                break;
            }
         }
        return found;
    }
   
    
    private class PodInfo{
        
     String name;
     int booths;
     int lot_size;
     String road_rating;
     double x;
     double y;
     
     public PodInfo(String name, int booths, int lot_size, String road_rating, double x, double y) {
            this.name = name;
            this.booths = booths;
            this.lot_size = lot_size;
            this.road_rating = road_rating;
            this.x=x;
            this.y=y;
        }
     
     public String getInfo()
     {
         return "Name :"+this.name+"\n, Number of booths: "+this.booths+"\n, Parking lot size: "+this.lot_size+
                 "\n, Road rating: "+this.road_rating+"\n";
     }
     
     public boolean check(double x, double y, double margin)
     {
         double distanceX=Math.abs(this.x - x);
         double distanceY=Math.abs(this.y - y);
         
         if(distanceX < margin && distanceY < margin)
             return true;
         else
             return false;
         
     }
     
     public void setPos(double x, double y)
     {
      this.x=x;
      this.y=y;
     }
    }
    
      
 }