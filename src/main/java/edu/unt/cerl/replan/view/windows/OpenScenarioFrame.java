package edu.unt.cerl.replan.view.windows;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.LoadScenarioEvent;
import edu.unt.cerl.replan.controller.action.LoadScenarioListener;
import edu.unt.cerl.replan.model.UserState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/**
 * A frame that lets the user select a scenario to load from any scenario
 * saved by any of the users
 */
public class OpenScenarioFrame extends JFrame {

    // GUI components
    private Component owner;
    private JButton load;
    private JButton cancel;
    private JButton delete;
    private JComboBox userListComboBox;
    private JLabel userLabel;
    private JList scenarioList;
    private JScrollPane scenarioScrollPane;
    private Map<String, String> descriptions;
    public LoadScenarioListener loadListener;
    public String publicUserID;
    private SwingWorker<Void, Void> statusBarWorker;
    Object[] loadScenarios;
    String[] loadScenariosNames;
    

    /**
     * Opens a frame that lets the user laod a scenario saved by any of the
     * users.
     */
    public OpenScenarioFrame(Component owner) {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.initComponents();
        this.owner = owner;
        this.owner.setEnabled(false);
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
        this.loadListener = new LoadScenarioListener();
    }

    /**
     * Creates a JList that will show the description of a scenario as
     * a tooltip if the Mouse hovers over it. The actual list model is set
     * dynamically based on which user is selected. The model then contains
     * an array of all scenario names for that particular user.
     *
     * @return JList with tooltip capabilities
     */
    private JList createScenarioList() {
        return scenarioList = new JList() {
            // This method is called as the cursor moves within the list.

            @Override
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());
                // Get item Object
                //System.out.println( "OpenScenarioFrame.getToolTipText calling getModel().getElementAt with index = " + index );
                String scenarioName = (String) getModel().getElementAt(index);
                // Return the tool tip text
                return "Description: " + descriptions.get(scenarioName);
            }
        };
    }

    /**
     * Initializes the GUI
     */
    private void initComponents() {

        // Initialize global GUI components
        load = new JButton("Load");
        cancel = new JButton("Cancel");
        delete = new JButton("Delete");
        scenarioScrollPane = new JScrollPane();
        userListComboBox = new JComboBox();
        userLabel = new JLabel("User");
        this.createScenarioList();
        
        /*
         *add action listeners to the load, delete and cancel buttons
         */
        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                loadActionPerformed(evt);
            }
        });

        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        delete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try{
                   deleteActionPerformed(evt);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("SQLException: Error in deleting scenario");
                }
            }
        });

        /*
         * Load all the user names from the database and extract the relevant
         * part of the user ids
         */
        String[] userStrings = REPLAN.getQueries().getUsersFromDB(REPLAN.getController().getConnection());
        int userIndex = 0;
        for (int i = 0; i < userStrings.length; i++) {
            Pattern p = Pattern.compile("\\(.*?\\)", Pattern.DOTALL);
            Matcher matcher = p.matcher(userStrings[i]);
            matcher.find();
            String userId = matcher.group();
            userId = userId.substring(1, userId.length() - 1);
            if (userId.equals(UserState.userId)) {
                System.out.println(userStrings[i] + "\t" + UserState.userId);
                userIndex = i;
                break;
            }
        }

        /*
         * Assign the combo box the user names as a list model. Add an
         * action listener that reacts to user name selection.
         */
        userListComboBox.setModel(new DefaultComboBoxModel(userStrings));
        userListComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                userListActionPerformed(evt);
            }
        });
        userListComboBox.setSelectedIndex(userIndex);

        /*
         * Show for the currently selected user (initialized to the first user)
         * all saved scenarios.
         */
        this.showScenariosForUser();

        scenarioScrollPane.setViewportView(scenarioList);

        JPanel borderPanel = new JPanel(new MigLayout());
        getContentPane().add(borderPanel);
        borderPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        this.scenarioScrollPane.setMinimumSize(new Dimension(350, 250));

        /*
         * Add the GUI components to the corresponding panel
         */
        borderPanel.add(this.userLabel);
        borderPanel.add(this.userListComboBox, "span 4 1, wrap");
        borderPanel.add(this.scenarioScrollPane, "span, wrap");
        borderPanel.add(this.load);
        borderPanel.add(this.delete);
        borderPanel.add(this.cancel);

        pack();
    }

    /**
     * This method is invoked once the load scenario button has been pressed.
     * It instantiates a LoadScenarioEvent and hands it to the
     * LoadScenarioListener, which handles the actual loading of the scenario.
     * @param evt LoadScenarioEvent containing name and author of the scenario
     */
    private void loadActionPerformed(java.awt.event.ActionEvent evt) {
        String user = (String) this.userListComboBox.getSelectedItem();
        Pattern p = Pattern.compile("\\(.*?\\)", Pattern.DOTALL);
        Matcher matcher = p.matcher(user);
        matcher.find();
        String userId = matcher.group();
        userId = userId.substring(1, userId.length() - 1);
        
        // Check & load multiple scenarios if there are more the one
        loadScenarios = scenarioList.getSelectedValues();
        // If there is more then one scenario to load
        if (loadScenarios.length > 0) {
            
            // Local variable to hold progress value for display
            final int step = (100/loadScenarios.length) / 2;
            
            // Create progress bar because multiple loads can take a little time
            final JProgressBar proBar = new JProgressBar(0,100);
            proBar.setPreferredSize(new Dimension(175,20));
            proBar.setStringPainted(true);
            proBar.setValue(0); 
            JLabel label = new JLabel("Progress: ");
            JPanel center_panel = new JPanel();
            center_panel.add(label);
            center_panel.add(proBar);
            
            // Create dialog to hold the loading bar
            final JDialog dialog = new JDialog((JFrame)null, "Loading Scenarios...");
            dialog.setUndecorated(true);
            dialog.getContentPane().add(center_panel, BorderLayout.CENTER);
            dialog.pack();
            dialog.setVisible(true);
            dialog.setLocationRelativeTo(this);
            dialog.toFront();
            
            // Place private variables into local public variables for new thread
            publicUserID = userId;
            loadScenariosNames = new String[loadScenarios.length];
            
            // Create new thread to not freeze UI while loading scenarios
            statusBarWorker = new SwingWorker<Void, Void>() {
                @Override
                // Do process in background, return null
                public Void doInBackground() throws InterruptedException {
                    for (int i=0; i<loadScenarios.length; i++) {
                        // Set the progress bar
                        proBar.setValue(proBar.getValue()+step);
                        // Get the name of the scenario to load from selection
                        loadScenariosNames[i] = loadScenarios[i].toString();
                        // Load the scenario on the screen
                        try {
                            loadListener.loadScenarioEventPerformed(new LoadScenarioEvent(this,publicUserID,loadScenariosNames[i]));
                        } catch (SQLException ex) {
                            Logger.getLogger(OpenScenarioFrame.class.getName()).
                            log(Level.SEVERE, null, ex);
                        }
                        // Update the progress bar to current status of completion
                        proBar.setValue((i+1)*(100/loadScenarios.length));
                        // If at the end of the loading loop, close the loading bar
                        if (i == loadScenarios.length-1) {
                            dialog.dispose();
                        }
                    }
                    return null;
                }  
            };
            // Execute worker thread
            runStatusBarWorkerThread();
        }
        // If there is NO scenario selected, print error
        else {
            JOptionPane.showMessageDialog(this,
                "Please select a scenario to open",
                "Empty selection error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        if (owner == null) {
            System.exit(0);
        } else {
            owner.setEnabled(true);
            this.dispose();
        }
    }
    
    public void runStatusBarWorkerThread() {
        statusBarWorker.execute();
    }
    
    /**
     * This method is called if a scenario is to be deleted
     * @param evt
     * @throws SQLException
     */
    private void deleteActionPerformed(ActionEvent evt) throws
            SQLException {
        String user = (String) this.userListComboBox.getSelectedItem();
        Pattern p = Pattern.compile("\\(.*?\\)", Pattern.DOTALL);
        Matcher matcher = p.matcher(user);
        matcher.find();
        String userId = matcher.group();
        userId = userId.substring(1, userId.length() - 1);
        
         // Check & display multiple deletion confirmations if more then one is selected
        Object[] deleteScenarios = scenarioList.getSelectedValues();
        // If there is more then one scenario to delete then create & show a confirmation form
        if (deleteScenarios.length > 1) {
            JCheckBox[] confirmDeleteScenarios = new JCheckBox[deleteScenarios.length];
            for(int i=0; i<deleteScenarios.length; i++) {
                confirmDeleteScenarios[i] = new JCheckBox();
                confirmDeleteScenarios[i].setText((String) deleteScenarios[i]);
                confirmDeleteScenarios[i].setSelected(true);
            }
           
            // Create the form to show
            Object[] form = {
              "Are you sure you want to delete these scenarios?\n"
            + "Uncheck any scenarios you do NOT want to delete.",
              confirmDeleteScenarios,  
            };
            
            // Get scenario confirmation selections
            int option = JOptionPane.showConfirmDialog(this, form, "Confirm Delete Scenarios", JOptionPane.PLAIN_MESSAGE);
            
            // If the user has confirmed the scenarios to delete, then delete the selections
            if (option == JOptionPane.OK_OPTION) {
                for (int i=0; i<confirmDeleteScenarios.length; i++) {
                    if (confirmDeleteScenarios[i].isSelected()) {
                        REPLAN.getQueries().deleteScenario(userId, confirmDeleteScenarios[i].getText(), REPLAN.getController().getConnection());
                    }
                }
                // when finished deleting, show the scenarios form for user
                this.showScenariosForUser();
            }
        }  
        // If there is only one scenario to delete, then keep default confirmation frame
        else {
            String scenario = (String) this.scenarioList.getSelectedValue();
            if (scenario == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a scenario to delete",
                        "Empty selection error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                String message =
                        "Are you sure, you would like to delete scenario:\n\t \"" + this.scenarioList.getSelectedValue().toString() + "\"";
                int r = JOptionPane.showConfirmDialog(this, message, "Delete?",
                        JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    REPLAN.getQueries().deleteScenario(userId, scenario, REPLAN.getController().getConnection());
                    this.showScenariosForUser();
                }
            }
        }
    }
        
    /**
     * Closes the window and returns to the RE-PLAN main window
     * @param evt
     */
    private void cancelActionPerformed(ActionEvent evt) {
        if (owner == null) {
            System.exit(0);
        } else {
            owner.setEnabled(true);
            this.dispose();
        }
    }

    /**
     * Implementation of action listener for JList
     * @param evt
     */
    private void userListActionPerformed(ActionEvent evt) {
        this.showScenariosForUser();
    }

    /**
     * Whenever a different user is selected, this method updates the list of
     * scenarios accordingly.
     */
    private void showScenariosForUser() {
        String user = (String) this.userListComboBox.getSelectedItem();
        Pattern p = Pattern.compile("\\(.*?\\)", Pattern.DOTALL);
        Matcher matcher = p.matcher(user);
        matcher.find();
        String userId = matcher.group();
        userId = userId.substring(1, userId.length() - 1);
        final String[] scenarios = REPLAN.getQueries().getScenariosForUser(
                userId, REPLAN.getController().getConnection());
        System.out.println("OpenScenarioFrame: scenario size = " + scenarios.length);
        descriptions = REPLAN.getQueries().getDescriptions(userId, REPLAN.getController().getConnection());
        scenarioList.setModel(new AbstractListModel() {

            String[] strings = scenarios;

            @Override
            public int getSize() {
                return strings.length;
            }

            @Override
            public Object getElementAt(int i) {
                //System.out.println( "OpenScenario.getElementAt with index = " + i );
                return strings[i];
            }
        });
    }
}
