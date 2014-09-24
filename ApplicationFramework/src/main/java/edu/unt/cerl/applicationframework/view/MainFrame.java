
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package edu.unt.cerl.applicationframework.view;

import edu.unt.cerl.applicationframework.controller.action.MainFrameWindowListener;
import java.awt.BorderLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 * This class creates a JFrame that will exit the program upon clicking on
 * the x-button. It provides a desktop pane, that allows for adding and
 * removing sub-frames. The desktop pane can be obtained by the getDesktop()
 * method. By default it is the only component of the main frame's content pane
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class MainFrame extends JFrame {

    private JDesktopPane desktop;

    /**
     * Initializes the main frame
     * @param title title displayed by the frame
     * @param w width of the frame
     * @param h height of the frame
     */
    public MainFrame(String title, int w, int h) {
        this.init(title, w, h);
    }

    /**
     * Initializes the main frame. Sets title, size, location and window listener
     * @param title title of the frame
     * @param w width of the frame
     * @param h height of the frame
     */
    private void init(String title, int w, int h) {
        this.setSize(w, h);
        this.setTitle(title);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BorderLayout());
        desktop = new JDesktopPane();
        this.getContentPane().add(desktop,BorderLayout.CENTER);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new MainFrameWindowListener());
    }

    /**
     * Accessor method for desktop
     * @return desktop pane
     */
    public JDesktopPane getDesktop() {
        return this.desktop;
    }

    /**
     * Adds a window to the desktop pane of the main frame.
     * @param frame window to be added
     */
    public void addWindow(JInternalFrame frame) {
        desktop.add(frame);
    }

    /**
     * Removes a window from the desktop pane of the main frame
     * @param frame window to be removed
     */
    public void removeWindow(JInternalFrame frame) {
        desktop.remove(frame);
    }

    public void disableActionsDuringReplan() {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
