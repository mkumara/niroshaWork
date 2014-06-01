/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.view;

import edu.unt.cerl.replan.REPLAN;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author josh
 */

// Progress bar with no status updates
public class IndeterminateProgressBar {
    
    final static JProgressBar proBar = new JProgressBar();
    static JDialog dialog;
    Component frame;
    
    public IndeterminateProgressBar(Component frameToSitIn){
       dialog = new JDialog((JFrame)null, "");
       frame = frameToSitIn;
    }
    
   
    public void showProgressBar(){
        proBar.setPreferredSize(new Dimension(190,20));
        proBar.setIndeterminate(true);
       
        JPanel center_panel = new JPanel();
        center_panel.add(proBar);
        dialog.setUndecorated(true);        
        dialog.getContentPane().add(center_panel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(frame);
        dialog.toFront();
    }
    
    public void removeProgressBar(){
        dialog.dispose();
    }
    
    
    
}
