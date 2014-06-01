package edu.unt.cerl.replan.controller.action;

import edu.unt.cerl.replan.view.mainframe.MainFrame;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import javax.swing.JOptionPane;

/**
 * This class implements the methods required by the interface WindowListener.
 * The only method necessary for RE-PLAN is the windowClosing method, which 
 * verifies if the user wants to close RE-PLAN. If so, RE-PLAN is terminated 
 * after performing cleaning tasks for each scenario.
 * @author Tamara Schneider
 */
public class MainFrameWindowListener implements WindowListener {

    private Component owner;

    public MainFrameWindowListener(Component owner) {
        this.owner = owner;
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    /**
     * Called when the x-button of the main frame has been pressed. 
     */
    public void windowClosing(WindowEvent we) {
        //TODO Replace with pop-up to verify intention to close

        MenuActions.quitAction();
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }
}
