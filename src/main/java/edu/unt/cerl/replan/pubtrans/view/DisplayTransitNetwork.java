/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pubtrans.view;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import java.awt.Color;
import org.geotools.filter.text.cql2.CQLException;

/**
 *
 * @author martyo
 */
public class DisplayTransitNetwork {

    public static void DisplayTransitNetwork() throws CQLException {
        REPLAN.getMainFrame().getTabs().getSelectedScenario().createLayer("tarrant_gtfs_shape",
                DefaultStyles.createSingleSymbolLineStyle(
                1.0,
                new Color(0x000000), //new Color(0x0868AC),
                4), "Transit Network");

        REPLAN.getMainFrame().getTabs().getSelectedScenario().createLayer("tarrant_gtfs_stops",
                DefaultStyles.createSingleSymbolPointStyle(
                1.0,
                Color.black,
                1.0,
                Color.yellow,
                1.0f,
                6.0f), "Transit Stops");

    }
}
