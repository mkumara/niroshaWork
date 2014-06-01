package edu.unt.cerl.replan.view.windows;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.UserState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;

/**
 * This class displays a frame with a list of available REPLAN users. It allows
 * for the creation of new users and the deletion of existing users
 *
 * @author Tamara Schneider
 */
public class UserSelectionFrame extends JFrame {

    //
    // Fields
    //
    private JButton deleteButton;
    private JLabel welcomeLabel;
    private JLabel selectLabel;
    private JPanel mainPanel;
    private JScrollPane userScrollPanel;
    private JButton newUserButton;
    private JButton selectButton;
    private JList userList;

    //
    // Constructors
    //
    public UserSelectionFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize
     * the form
     */
    private void initComponents() {
        // Initialize elements
        mainPanel = new JPanel();
        userScrollPanel = new JScrollPane();
        userList = new JList();
        newUserButton = new JButton();
        selectButton = new JButton();
        selectLabel = new JLabel();
        welcomeLabel = new JLabel();
        deleteButton = new JButton();

        // Set window properties
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        setUndecorated(true);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(
                0, 0, 0), 3));

        // Get users from DB and fill user list
        userList.setModel(new UserModelImplementation(REPLAN.getQueries().
                getUsersFromDB(REPLAN.getController().getConnection())));
        userList.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userScrollPanel.setViewportView(userList);

        // Set properties of new-user button
        newUserButton.setText("New...");
        newUserButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                newUserActionPerformed(evt);
            }
        });

        // Set properties of select button
        selectButton.setText("Select");
        selectButton.setMinimumSize(new Dimension(80, 27));
        selectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                selectActionPerformed(evt);
            }
        });

        // Set properties for labels
        selectLabel.setText("Select existing user:");
        welcomeLabel.setFont(new java.awt.Font("DejaVu Sans", 0, 24));
        welcomeLabel.setForeground(new java.awt.Color(132, 32, 16));
        welcomeLabel.setText("Welcome to RE-PLAN");

        // Set properties of delete button
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        // create panel with layout and border; add to content pane
        MigLayout layout = new MigLayout(
                "wrap 3, insets 20",
                "[][][]", // column constraints
                "[40]10[]20[120]10[]"); // row constraings
        mainPanel.setLayout(layout);

        this.getContentPane().add(mainPanel);

        // add components to panel
        mainPanel.add(welcomeLabel, "span");
        mainPanel.add(selectLabel, "wrap");
        mainPanel.add(userScrollPanel, "span3, h 120!, w 250!");
        mainPanel.add(selectButton, "split 2");
        mainPanel.add(deleteButton);
        mainPanel.add(newUserButton, "gap 20, w 80!");

        pack();
    }

    /**
     * This method updates the list of users displayed in the scroll pane
     */
    public void updateList() {
        userList.setModel(new UserModelImplementation(REPLAN.getQueries().
                getUsersFromDB(REPLAN.getController().getConnection())));
    }

    /**
     * This Method is called if the select button has been pressed. If a user
     * has been selected it sets the UserState accordingly.
     * @param evt action event
     */
    private void selectActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.userList.isSelectionEmpty()) {
            // No user has been selected; display error message
            JOptionPane.showMessageDialog(this,
                    "Please select a user",
                    "Empty selection error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // User has been selected. Extract ID and set UserState.
            String selection = (String) this.userList.getSelectedValue();
            Pattern p = Pattern.compile("\\(.*?\\)", Pattern.DOTALL);
            Matcher matcher = p.matcher(selection);
            matcher.find();
            String userId = matcher.group();
            userId = userId.substring(1, userId.length() - 1);
            UserState.userId = userId;
            String userName = selection.split("\\s\\(")[0];
            UserState.username = userName;
            REPLAN.userSelected();
        }
        this.dispose();
    }

    /**
     * This method is called if the new user button has been pressed. It opens
     * a new window to create new users.
     * @param evt action event
     */
    private void newUserActionPerformed(java.awt.event.ActionEvent evt) {
        NewUserFrame nuf = new NewUserFrame(this);
        nuf.setVisible(true);
    }

    /**
     * This method is called if the delete button has been pressed. If a user
     * is selected, it is deleted from the database.
     * @param evt action event
     */
    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.userList.isSelectionEmpty()) {
            // no user has been selected; display error message
            JOptionPane.showMessageDialog(this,
                    "Please select a user",
                    "Empty selection error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // Verify if the user really is to be delted
            String message = "Are you sure, you would like to delete this user \n "
                    + "and all corresponding scenarios?";
            int r = JOptionPane.showConfirmDialog(this, message, "Delete?",
                    JOptionPane.YES_NO_OPTION);
            //Delete if deletion has been verified
            if (r == JOptionPane.YES_OPTION) {
                // User is to be deleted: Extract key and delete from DB
                String selection = (String) this.userList.getSelectedValue();
                Pattern p = Pattern.compile("\\(.*?\\)", Pattern.DOTALL);
                Matcher matcher = p.matcher(selection);
                matcher.find();
                String userId = matcher.group();
                userId = userId.substring(1, userId.length() - 1);

                REPLAN.getQueries().deleteEntry("users", "id", userId, REPLAN.
                        getController().getConnection());
                REPLAN.getQueries().deleteSchema(userId, REPLAN.getController().
                        getConnection());
                this.updateList();

            }
        }
    }

    /**
     * This class implements the model that underlies the scrollpane to display
     * existing REPLAN users
     */
    private class UserModelImplementation extends AbstractListModel {

        private String[] strings;

        public UserModelImplementation(String[] strings) {
            this.strings = strings;
        }

        @Override
        public int getSize() {
            return strings.length;
        }

        @Override
        public Object getElementAt(int i) {
            //System.out.println( "UserSelectionFrame.getElementAt with index = " + i );
            return strings[i];
        }
    }
}
