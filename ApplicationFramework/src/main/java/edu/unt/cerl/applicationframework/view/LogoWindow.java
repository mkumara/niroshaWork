package edu.unt.cerl.applicationframework.view;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This class displays a logo in an undecorated frame for 2 seconds. Then it
 * disappears and enables the component designated as its owner
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class LogoWindow extends JFrame {

    Component owner; // component that owns the logo window

    /**
     * Creates a new instance of the logo window. It sets the owner and causes
     * a 2 second waiting period
     * @param owner component that owns the logo window
     * @param img path to image
     * @throws InterruptedException
     */
    public LogoWindow(Component owner, String img) throws InterruptedException {
        this.owner = owner;
        this.init(img);
        Thread.sleep(2000);
        owner.setVisible(true);
        this.dispose();
    }

    /**
     * Initializes the logo window. Removes decorations and sets size and image
     * @param img path to image that is to be used
     */
    private void init(String img) {
        this.setUndecorated(true);
        ImageIcon icon = new ImageIcon(img);
        this.setSize(icon.getIconWidth(), icon.getIconHeight());
        this.getContentPane().add(new JLabel(icon));
        this.setVisible(true);
        setAlwaysOnTop(true);
        this.setLocationRelativeTo(owner);
    }
}
