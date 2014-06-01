package edu.unt.cerl.replan.view.windows;

import edu.unt.cerl.replan.REPLAN;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/**
 * This class displays a window that allows for the creation of a new
 * RE-PLAN user
 *
 * @author Tamara Schneider
 *
 */
public class NewUserFrame extends javax.swing.JFrame {

    private UserSelectionFrame owner;
    private JButton addButton;
    private JButton cancelButton;
    private JTextField idField;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel domainLabel;
    private JTextField nameField;

    /** Creates new form NewUserFrame */
    public NewUserFrame(UserSelectionFrame owner) {
        this.owner = owner;
        initComponents();
        this.setLocationRelativeTo(owner);
        owner.setEnabled(false);
        owner.setVisible(false);
        this.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {


        // initialize labels and buttons
        nameLabel = new JLabel("Name");
        emailLabel = new JLabel("Email");
        nameField = new JTextField();
        idField = new JTextField();
        domainLabel = new JLabel("@ tarrantcounty.com");
        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");

        // initialize frame
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true);

        // action listener for add button
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        // action listener for cancel button
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        // create panel with layout and border; add to content pane
        MigLayout layout = new MigLayout(
                "wrap 3, insets 10",
                "[][][]",
                "[][]20[]");
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createLineBorder(new Color(
                0, 0, 0), 3));
        this.getContentPane().add(panel);

        // add components to panel
        panel.add(nameLabel);
        panel.add(nameField, "span 2, w 200!");
        panel.add(emailLabel);
        panel.add(idField, "w 150!");
        panel.add(domainLabel);
        panel.add(addButton, "skip, split 2");
        panel.add(cancelButton, "gap 5");

        pack();
    }

    /**
     * Adds new user to the database. User is only added if user does not
     * exist yet and supported characters have been used. Otherwise, an
     * error message is displayed.
     * @param evt ActionEvent
     */
    private void addActionPerformed(java.awt.event.ActionEvent evt) {

        String userName = nameField.getText();
        String userId = idField.getText();

        if (userId.length() == 0 || userName.length() == 0) {
            // empty field found
            JOptionPane.showMessageDialog(this,
                    "Please fill out all fields",
                    "Empty field error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (REPLAN.getQueries().entryExists("users", "id", userId, REPLAN.
                getController().getConnection())) {
            // user already exists
            JOptionPane.showMessageDialog(this,
                    "User with this e-mail address already exists",
                    "Duplicate user error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (Character.isDigit(userId.charAt(0))) {
            // disallow e-mail addresses starting with digits
            // due to database compatibility
            JOptionPane.showMessageDialog(this,
                    "First character of e-mail address cannot be a digit",
                    "Illegal entry error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!userName.matches(("[\\w\\s\\-]*"))) {
            // disallowed character found in name field
            JOptionPane.showMessageDialog(this,
                    "Names may only contain letters, numbers, hyphens, and whitespaces!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!userId.matches(("[\\w]*"))) {
            // disallowed character found in e-mail field
            JOptionPane.showMessageDialog(this,
                    "Email addresses may only contain letters, numbers, and underscores!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // no problem found; create new user and schema in database
            REPLAN.getQueries().addEntry("users", userId, userName, REPLAN.
                    getController().getConnection());
            REPLAN.getQueries().addSchema(userId, REPLAN.getController().
                    getConnection());
            this.owner.updateList();
            this.owner.setEnabled(true);
            this.owner.setVisible(true);
            this.dispose();
        }
    }

    /**
     * This method is called if the cancel button is pressed. It closes
     * the frame
     * @param evt action event
     */
    private void cancelActionPerformed(ActionEvent evt) {
        this.owner.setEnabled(true);
        this.owner.setVisible(true);
        this.dispose();
    }
}
