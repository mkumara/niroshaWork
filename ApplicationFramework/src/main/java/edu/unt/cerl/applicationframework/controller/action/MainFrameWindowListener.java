
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.applicationframework.controller.action;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class MainFrameWindowListener implements WindowListener {

    @Override
    public void windowOpened(WindowEvent we) {
      //  throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowClosing(WindowEvent we) {
//        File dir = new File("tmp");
//        File[] files = dir.listFiles();
//        for(File file: files){
//            file.delete();
//        }

        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent we) {
      //  throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowIconified(WindowEvent we) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowActivated(WindowEvent we) {
    //    throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
      //  throw new UnsupportedOperationException("Not supported yet.");
    }

}
