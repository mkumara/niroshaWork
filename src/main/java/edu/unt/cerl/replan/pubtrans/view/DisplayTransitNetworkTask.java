/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pubtrans.view;

import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.view.IndeterminateProgressBar;
import java.awt.Color;
import org.geotools.filter.text.cql2.CQLException;

/**
 *
 * @author martyo
 */
public class DisplayTransitNetworkTask {

    private javax.swing.SwingWorker<Void, Void> statusBarWorker;

    public void DisplayTransitNetwork() throws CQLException {

        // Create the progress bar
        final IndeterminateProgressBar progressBar = new IndeterminateProgressBar(REPLAN.getMainFrame().getTabs().getSelectedScenario().getMapPane());
        // can copy the above line directly. The parameter passed simply specifies where to draw the progress bar.

        // Create the worker thread, it holds the work
        statusBarWorker = new javax.swing.SwingWorker<Void, Void>() { // create new thread

            @Override
            public Void doInBackground() {

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
                return null;
            }
        };

          // execute thread after its created
        this.statusBarWorker.execute();
        System.out.println("After execute");
    }
}
