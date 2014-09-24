/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.view;

/*
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



import java.awt.event.ActionEvent;
import javax.swing.JComponent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.*;
import org.geotools.geometry.Envelope2D;

/**
 * An action for connect a control (probably a JButton) to
 * the ZoomInTool for zooming the map with mouse clicks
 * or drags.
 * 
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 * @version $Id: PodInfoAction.java,v 1.1.2.2 2014-09-24 16:15:59 nirosha Exp $
 */
public class PodInfoAction extends MapAction {

    /**
     * Constructor. The associated control will be labelled with an icon.
     * 
     * @param mapPane the map pane being serviced by this action
     */
    public PodInfoAction(MapPane mapPane) {
        this(mapPane, false);
    }

    /**
     * Constructor. The associated control will be labelled with an icon and,
     * optionally, the tool name.
     * 
     * @param mapPane the map pane being serviced by this action
     * @param showToolName set to true for the control to display the tool name
     */
    public PodInfoAction(MapPane mapPane, boolean showToolName) {
        String toolName = showToolName ? PodInfoTool.TOOL_NAME : null;
        super.init(mapPane, toolName, PodInfoTool.TOOL_TIP, PodInfoTool.ICON_IMAGE);
    }
    
    /**
     * Called when the associated control is activated. Leads to the
     * map pane's cursor tool being set to a new CliffMapTool object
     *
     * @param ev the event (not used)
     */
    public void actionPerformed(ActionEvent ev) {
        getMapPane().setCursorTool(new PodInfoTool((JComponent)getMapPane()));
         System.out.println("Cursor Tool = "+getMapPane().getCursorTool().getClass());
        
          
        }

}
