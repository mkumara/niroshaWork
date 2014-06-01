package edu.unt.cerl.replan.view.windows;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.NewScenarioEvent;
import edu.unt.cerl.replan.controller.action.NewScenarioListener;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/**
 * This class displays a frame for the creation of a new scenario. It contains 
 * name and description fields, as well as cancel and create buttons.
 * @author Tamara Schneider
 *
 */
public class NewScenarioFrame extends javax.swing.JFrame {

    private Component owner;
    private JButton cancelButton;
    private JButton createButton;
    private JTextArea descriptionArea;
    private JLabel descriptionLabel;
    private JPanel panel;
    private JScrollPane scrollPane;
    private JTextField nameField;
    private JLabel nameLabel;
    private NewScenarioListener newScenarioListener;
    private JList list;
    private JLabel timePerIndividualLabel; //time to serve individual at a booth
    private JTextField timePerIndividual;
    private JRadioButton minsRadio;
    private JRadioButton secsRadio;
    private JCheckBox setTimePerIndividual; 

    /** Creates a new NewScenarioFrame */
    public NewScenarioFrame(Component owner) {
        this.owner = owner;
        owner.setEnabled(false);
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(owner);
    }

    public ScenarioState getScenarioState() {
        return this.newScenarioListener.getScenarioState();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {

        // initialize listener to create new scenario
        this.newScenarioListener = new NewScenarioListener();

        // Create components
        nameLabel = new JLabel("Name of scenario");
        nameField = new JTextField();
        descriptionLabel = new JLabel("Description (optional)");
        descriptionArea = new JTextArea();
        timePerIndividualLabel = new JLabel("Time");
        timePerIndividual = new JTextField();
        cancelButton = new JButton("Cancel");
        createButton = new JButton("Create scenario");
        scrollPane = new JScrollPane();
        panel = new JPanel();
        minsRadio = new JRadioButton();
        secsRadio = new JRadioButton();
        
        // Checkbox to either enable or disable setting a time for user at booth
        setTimePerIndividual = new JCheckBox("Set Time Per Individual At Booth");

        minsRadio.setMnemonic(KeyEvent.VK_B);
        minsRadio.setActionCommand("Minutes");
        minsRadio.setText("Min");
        secsRadio.setMnemonic(KeyEvent.VK_B);
        secsRadio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        secsRadio.setText("Sec");
        secsRadio.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(secsRadio);
        group.add(minsRadio);

        timePerIndividualLabel.setToolTipText("Time allocated to each individual at a booth");        
        // set window properties
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Scenario");
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
        createButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);

            }
        });

        // set properties of description area and scroll pane
        descriptionArea.setColumns(20);
        descriptionArea.setRows(5);
        scrollPane.setViewportView(descriptionArea);

        // get the names of the datasets and add them to the list
        List<String> l = REPLAN.getDatasetNames();
        String[] listData = new String[l.size()];
        for (int i = 0; i < listData.length; i++) {
            listData[i] = l.get(i);
        }
        list = new JList(listData);
        JScrollPane datasetScroller = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        datasetScroller.setMinimumSize(new Dimension(200, 100));
        datasetScroller.setMaximumSize(new Dimension(300, 100));


        // Place the individual components onto the panel
        MigLayout layout = new MigLayout();
        panel.setLayout(layout);
        panel.add(this.nameLabel, "split2");
        panel.add(this.nameField, "gapleft 10, w 220!, wrap");
        panel.add(this.descriptionLabel, "wrap");
        panel.add(this.scrollPane, "w 350!, span");
        panel.add(datasetScroller, "wrap");
        panel.add(setTimePerIndividual,"wrap");  
        //panel.add(timePerIndividualLabel, "split2");
        panel.add(timePerIndividualLabel, "split 4");
        //panel.add(timePerIndividual, "gapleft 10, w 220!, wrap");
        panel.add(timePerIndividual, "gapleft 5, w 60!");
        panel.add(secsRadio, "gapleft 5, w 80!");
        panel.add(minsRadio, "gapleft 5, w 70!, wrap");
        // panel.add(list,"wrap");
        panel.add(this.createButton, "gapleft 100, split 2");
        panel.add(this.cancelButton);
        
        
        // Set defualts for "time per individual" input
        minsRadio.setEnabled(false);
        secsRadio.setEnabled(false);
        timePerIndividual.setText("0");
        timePerIndividual.setEnabled(false);
        
        // set action listener, if checkbox is clicked, enable fields
        setTimePerIndividual.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTimePerIndividualActionPerformed(evt);
            }
        });
        
        this.getContentPane().add(panel);

        pack();
    }
    
    // If user wants to set time per individual at booth, enable (or disable) feilds
    private void setTimePerIndividualActionPerformed(ActionEvent evt) {
        // If user doesn't want to set the value, disable
        if (timePerIndividual.isEnabled()) {
            timePerIndividualLabel.setEnabled(false);
            minsRadio.setEnabled(false);
            secsRadio.setEnabled(false);
            timePerIndividual.setText("0");
            timePerIndividual.setEnabled(false);
        }
        // If the user DOES want to set the value, enable 
        else {
            timePerIndividualLabel.setEnabled(true);
            minsRadio.setEnabled(true);
            secsRadio.setEnabled(true);
            timePerIndividual.setText("");
            timePerIndividual.setEnabled(true);
        }
    }
    

    /**
     * Whenever the create button is pressed, this method is called. It verifies
     * if the name is permissible and causes a new scenario to be created
     * @param evt
     */
    private void createButtonActionPerformed(ActionEvent evt) {
        try {
            if (this.inputValidator()) {
                Object[] geoObj = list.getSelectedValues();
                String[] geographies = new String[geoObj.length];
                for (int i = 0; i < geographies.length; i++) {
                    geographies[i] = (String) geoObj[i];
                    System.out.println(geographies[i]);
                }
                String timePerInd = this.timePerIndividual.getText();
                if (minsRadio.isSelected()) {
                    int timeInSecs = Integer.parseInt(timePerInd) * 60;
                    timePerInd = String.valueOf(timeInSecs);
                }

                newScenarioListener.newScenarioEventPerformed(new NewScenarioEvent(
                        this, this.nameField.getText().toLowerCase(), this.descriptionArea.getText(), geographies, timePerInd));
                owner.setEnabled(true);
                this.dispose();
            }

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
        } else if (!nameField.getText().matches(("[^0-9].[a-z0-9_]*"))) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "Names should start with a letter and may only contain lower case letters, numbers and underscores!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (timePerIndividual.getText().isEmpty()) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "You must enter a value for time per individual!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!timePerIndividual.getText().matches(("[0-9_]*"))) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "Time should be a numerical value!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (REPLAN.getQueries().entryExists("scenarios", "name", nameField.getText(), "author", UserState.userId, REPLAN.getController().
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
        } else if (list.getSelectedValues().length == 0) {
            permissible = false;
            JOptionPane.showMessageDialog(this,
                    "Please Select a County",
                    "Error",
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
