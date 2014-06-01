package edu.unt.cerl.replan.view.windows;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.MenuActions;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/**
 * This class displays a frame for saving a new scenario.
 *
 * @author Tamara Jimenez
 *
 */
public class SaveAsFrame extends JFrame {

    private Component owner;
    private JButton cancelButton;
    private JButton saveButton;
    private JTextArea descriptionArea;
    private JLabel descriptionLabel;
    private JPanel panel;
    private JScrollPane scrollPane;
    private JTextField nameField;
    private JLabel nameLabel;
    private JCheckBox keepOldScenario;
    private boolean keepBothScenariosOpen;

    /** Creates a new NewScenarioFrame */
    public SaveAsFrame(Component owner) {
        this.owner = owner;
        owner.setEnabled(false);
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(owner);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {

        // Create components
        nameLabel = new JLabel("Name of scenario");
        nameField = new JTextField();
        descriptionLabel = new JLabel("Description (optional)");
        descriptionArea = new JTextArea();
        cancelButton = new JButton("Cancel");
        saveButton = new JButton("Save scenario");
        scrollPane = new JScrollPane();
        panel = new JPanel();
        keepOldScenario = new JCheckBox("Keep both scenarios open");
        keepBothScenariosOpen = false;

        // set window properties
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Save Scenario as...");
        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true);

        // set border around main panel
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 5));

        // action listener for the cancel button
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        // action listener for the create scenario button
        saveButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);

            }
        });

        // set properties of description area and scroll pane
        descriptionArea.setColumns(20);
        descriptionArea.setRows(5);
        scrollPane.setViewportView(descriptionArea);




        // Place the individual components onto the panel
        MigLayout layout = new MigLayout();
        panel.setLayout(layout);
        panel.add(this.nameLabel, "split2");
        panel.add(this.nameField, "gapleft 10, w 220!, wrap");
        panel.add(this.descriptionLabel, "wrap");
        panel.add(this.scrollPane, "w 350!, span");
        panel.add(this.keepOldScenario, "wrap");
        panel.add(this.saveButton, "gapleft 100, split 2");
        panel.add(this.cancelButton);
        

        this.getContentPane().add(panel);

        pack();
    }

    /**
     * Whenever the create button is pressed, this method is called. It verifies
     * if the name is permissible and causes a new scenario to be created
     * @param evt
     */
    private void saveButtonActionPerformed(ActionEvent evt) {
        try { // put the close tab option here instead
            if (this.inputValidator()) {
                if (keepOldScenario.isSelected()) {
                    keepBothScenariosOpen = true;
                }
                MenuActions.saveScenarioAs(nameField.getText(), keepBothScenariosOpen);
            }
            owner.setEnabled(true);
            this.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(NewScenarioFrame.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * If the user presses the cancel button, the frame is disposed of and
     * the main frame is enabled
     * @param evt action event
     */
    private void cancelButtonActionPerformed(ActionEvent evt) {
        owner.setEnabled(true);
        this.dispose();
    }

    /**
     * Checks if the input is valid for a scenario name
     * - must be alphanumeric
     * - must not contain white spaces
     * - name must not exist already
     * - must not start with "workingcpy"
     * - must not be empty
     * @return true or false
     * @throws SQLException
     */
    private boolean inputValidator() throws SQLException {

        boolean permissible = true;
        if (nameField.getText().isEmpty()) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "You must select a name!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!nameField.getText().matches(("[a-z0-9_]*"))) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "Names may only contain lower cas letters, numbers and underscores!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (REPLAN.getQueries().entryExists("scenarios", "name", nameField.
                getText(), "author", UserState.userId, REPLAN.getController().
                getConnection())) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "This scenario name already exists in database!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (nameField.getText().startsWith("workingcpy")) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "Name must not start with \"workingcpy\"",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            String[] tabNames = ((MainFrame) owner).getTabs().getTabNames();
            for (int i = 0; i < tabNames.length; i++) {
                if (tabNames[i].equals(nameField.getText())) {
                    JOptionPane.showMessageDialog(this,
                            "Tab already open with this name!",
                            "Naming error",
                            JOptionPane.ERROR_MESSAGE);
                    permissible = false;
                    break;
                }
            }
        }
        return permissible;
    }
}
