/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller;

import javax.swing.SwingWorker;

/**
 *
 * @author sarat
 */
public class SwingWorkerTask extends SwingWorker {

    String progressString;

    @Override
    protected Object doInBackground() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getProgressString() {
        return progressString;
    }
}
