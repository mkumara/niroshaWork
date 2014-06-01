package edu.unt.cerl.replan.view.mainframe;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import java.awt.Color;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Class TodoPanel
 */
public class TodoPanel extends JPanel implements Observer {

    private final String REEXECUTE_MSG =
            "The current scenario may not be up \n to date! Refresh Scenario \n";
    private JTextArea warningArea;

    public TodoPanel() {
        this.init();
    }

    private void init() {
        this.setMaximumSize(new java.awt.Dimension(280, 50));
        this.setMinimumSize(new java.awt.Dimension(280, 50));
        this.setPreferredSize(new java.awt.Dimension(280, 50));
        this.warningArea = new JTextArea();
        this.warningArea.setEditable(false);
        this.warningArea.setBackground(Color.RED);
        this.warningArea.setForeground(Color.WHITE);
        this.add(this.warningArea);
    }

    @Override
    public void update(Observable o, Object o1) {
        if (((ScenarioState) o).didPodsChange() && ((ScenarioState) o).isTrafficAnalysisPerformed()) {
            this.warningArea.setText(REEXECUTE_MSG);
            this.warningArea.setVisible(true);
            //this.setVisible(false);
            //this.setVisible(true);
            //this.repaint();
            REPLAN.getMainFrame().getTabs().getSelectedScenario().setVisible(false);
            REPLAN.getMainFrame().getTabs().getSelectedScenario().setVisible(true);
        } else {
            this.warningArea.removeAll();
            this.warningArea.setVisible(false);
        }
    }
}
