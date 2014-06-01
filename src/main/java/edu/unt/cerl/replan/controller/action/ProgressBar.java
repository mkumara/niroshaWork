/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller.action;

import edu.unt.cerl.replan.controller.SwingWorkerTask;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 *
 * @author sarat
 */
public class ProgressBar extends JPanel implements PropertyChangeListener {

    private JProgressBar progressBar;
    private SwingWorker task;
    private JTextArea taskOutput;
    private JFrame frame;

    public ProgressBar() {


        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        panel.add(progressBar);
        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        frame = new JFrame("ProgressBar");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JComponent newContentPane = this;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            String str1 = String.format(".. %d%% done.\n", task.getProgress());

            SwingWorkerTask t = (SwingWorkerTask)task;
            t.getProgressString();
            
            taskOutput.append(t.getProgressString()+str1);
            if(progress == 100) {
                frame.setVisible(false);
            }
        }
    }

    public void setTask(SwingWorker t) {
        this.task = t;
    }

}
