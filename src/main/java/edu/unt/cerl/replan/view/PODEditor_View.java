package edu.unt.cerl.replan.view;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.LoadPodsFromCSV;
import edu.unt.cerl.replan.controller.db.PODs2CSV;
import edu.unt.cerl.replan.model.POD;
import edu.unt.cerl.replan.model.PODList;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.TestList;
import java.awt.BorderLayout;

import java.awt.image.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Component;
import java.awt.Color;
import javax.swing.DefaultListModel;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.JMapPane;
import org.geotools.swing.locale.LocaleUtils;

public class PODEditor_View extends javax.swing.JFrame implements Observer {
    
    /** Creates new form NewJFrame */
    private javax.swing.JFileChooser fc;
    private PODList podList;
    private ScenarioState state;
    private BufferedImage img = null;
    private HashMap<Object, Icon> icons = null;
    private int selectListenerFlag = 0;
    private TestList myList;
    private ListData podListModel;
    private JMapPane mapPane;
    LocationSelector locSelector;
    private boolean locSelectorActive = false;
    public static final String TOOL_NAME = LocaleUtils.getValue("CursorTool", "Info");
    /** Tool tip text */
    public static final String TOOL_TIP = LocaleUtils.getValue("CursorTool", "InfoTooltip");
    /** Cursor */
    public static final String CURSOR_IMAGE = "/org/geotools/swing/icons/pointer.png";
    /** Cursor hotspot coordinates */
    public static final Point CURSOR_HOTSPOT = new Point(0, 0);
    /** Icon for the control */
    public static final String ICON_IMAGE = "/org/geotools/swing/icons/pointer.png";
    
    // Keep track of the pod that the index is on for when it changes,
    // for saving any changes to the database
    PODTracker podTracker = new PODTracker();
    private javax.swing.SwingWorker<Void, Void> statusBarWorker;
    
    //******** SET TO ENABLE OR DISABLE THE GEOCODING FEATURE ********
    private boolean useGeocoding;
    public final void setGeocoding(){
        if (REPLAN.GeocodingSessionAvailable){
            this.useGeocoding = Boolean.parseBoolean(REPLAN.getTables().get("tamu_geocoding_on"));
            System.out.println("useGeocoding = " + this.useGeocoding);
        }
    }
    
    
// ****************************************************************************
    // Inner Class ListData for maintaining POD data within POD Editor
    // Methods in this class also call updates to PODList and call appropriate
    // DB Update methods
    private class ListData extends DefaultListModel {

        public int getSize() {
            return podList.get_number_of_pods();
        }

        public Object getElementAt(int index) {
            //System.err.println("PODEditor.getElementAt index = " + index);
            //System.err.println("PODEditor.getElementAt podList = " + podList.toString());
            if (podList == null) {
                System.err.println("PODEditor.getElementAt podList == null");
            }
            //System.err.println("PODEditor.getElementAt podList = " + podList.toString());
            int fid = podList.get_pod_fid(index + 1);
            //System.err.println("PODEditor.getElementAt fid = " + fid);
            POD pod = podList.access_POD(fid);
            //System.err.println("PODEditor.getElementAt pod = " + pod.toString());
            String name = pod.get_name();
            //System.err.println("PODEditor.getElementAt ponamedList = " + name);
            return name;
        }

        public void addElementToEnd()  throws SQLException {
            POD temp = podList.add_pod(state, "<New POD Name>", "", "", "00000", 0.0, 0.0, true, true, "", 1);
            this.addElement(temp);
            //PODQueries.addNewPOD("new POD name", "", "", "00000", 0.0, 0.0, true, true, "", 1);
            //this.addElement(podList.add_pod("new POD name", "", "", "00000", 0.0, 0.0, true, true, "", 1)); // add new POD to data structure
            System.out.println("Added element to end. Total number, this.getSize = " + this.getSize());
            jList2.setSelectedIndex(this.getSize() - 1); // select last element, which will be new element
            jToggleButton1.setSelected(true);
            this.PODStatusIsOn(jToggleButton1.isSelected());
            
            // ***UPDATED FOR NEW UI:***
            // when adding a new pod, create a temp one for the podTracker
            jTextField1.setText("<New POD Name>");
            jComboBox1.setSelectedItem("Public");
            jSpinner1.setValue(1);
            jToggleButton1.setSelected(true);
            jTextArea1.setText("");
            streetLabel.setText("(Current address not given)");
            cityLabel.setText("");
            zipLabel.setText("00000");
            lonLabel.setText("0.0");
            latLabel.setText("0.0");
            
            
            if (!podTracker.hasOne){
                System.out.println("PODTracker*** doesn't have anything to track yet and is now tracking a newly added one!");
                System.out.println("PODTracker*** is about to be tracking the pod at index => " + (jList2.getSelectedIndex()+1));
                
                podTracker.hasOne = true;
                String podtype = String.valueOf(jComboBox1.getSelectedItem());
                boolean type;
                if (podtype.equals("Public") || podtype.equals("TRUE")) {
                    type = true;
                } else {
                    type = false;
                }
               
                
                 // ***UPDATED FOR NEW UI:***
                // set the pod tracker to follow the current on on the screen
                podTracker.setNew(
                       jList2.getSelectedIndex()+1,
                       jTextField1.getText(),
                       streetLabel.getText(),
                       cityLabel.getText(),
                       zipLabel.getText(),
                       jTextArea1.getText(),
                       Double.valueOf(lonLabel.getText()),
                       Double.valueOf(latLabel.getText()),
                       Integer.parseInt((jSpinner1.getValue()).toString()),
                        podList.access_POD(0).get_id(),
                        podList.access_POD(0).get_fid(),
                        jToggleButton1.isSelected(),
                        type
                );
                podTracker.isNew = true;
                System.out.println("finished Setting new one!");
                
                jTextField1.setText("<New POD Name>");
                jComboBox1.setSelectedItem("Public");
                jSpinner1.setValue(1);
                jToggleButton1.setSelected(true);
                jTextArea1.setText("");
                streetLabel.setText("(Current address not given)");
                cityLabel.setText("");
                zipLabel.setText("00000");
                lonLabel.setText("0.0");
                latLabel.setText("0.0");
            }
            
            
            
        }
        
        // change the labels & colors to represent the pods on/off state
        public void PODStatusIsOn(boolean statusIsOn) {
            if (statusIsOn){
                jToggleButton1.setText("Turn POD OFF");
                statusLabel.setText("ON");
                statusLabel.setForeground(new Color(107,142,35));
            }
            else {
                jToggleButton1.setText("Turn POD ON");
                statusLabel.setText("OFF");
                statusLabel.setForeground(Color.red);
            }
        }

        public void updateElement(int id, String name, String address, String city, String zip, double lon, double lat, boolean type, boolean status, String comments, int numBooths) throws SQLException {
            // podListModel.updateElement(index + 1, name, address, city, zip, lon, lat, type, status, comments, booths);
            System.out.println("updated with id of => " + id);
            podList.update_pod(id, name, address, city, zip, lon, lat, type, status, comments, numBooths);
            state.setChangedAndNotify();
            state.setPodsChanged();
            //  try {
            //      PODQueries.update_POD(podList.get_pod_fid(id), name, address, city, zip, lon, lat, type, status, comments, numBooths);
            //  } catch (SQLException ex) {
            //      Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
            //  }
            selectListenerFlag = 1;
            selectListenerFlag = 0;

            //Select the new item and make it visible.
        }

        public void deleteElementAt(int index) throws SQLException {
            if (index == -1) {
                System.out.println("No POD index selected. Nothing to delete!");
                return;
            }
            System.out.println("deleting POD index: " + index + "  total number pods: " + getSize());
            //PODQueries.delete_POD(podList.get_pod_fid(index + 1));// must delete from DB first, otherwise, local data structure changes before DB and correct fid will be unavailable when converting id to fid
            podList.delete_pod(podList.get_pod_fid(index + 1));
            
            
            System.out.println("POD deleted, total number of pods remaining: " + getSize());
            jList2.repaint(); // is it really this easy???
            // Note to self... Look at this example: http://www.cs.cf.ac.uk/Dave/HCI/HCI_Handout_CALLER/node144.html
            // next item is selected, so need to display next item in fields.
            if (index > 0) {
                // if the POD deleted isn't the first POD (at index==0), select the POD above it
                jList2.setSelectedIndex(index - 1);
            } else {
                jList2.setSelectedIndex(index + 1);
                jList2.setSelectedIndex(index);
            }
                        
            // If no PODs are left in the list, set all fields to blank
            if (getSize() == 0) {

                 // ***UPDATED FOR NEW UI:***
                 jTextField1.setText("");
                 streetLabel.setText("");
                 cityLabel.setText("");
                 zipLabel.setText("");
                 lonLabel.setText("");
                 latLabel.setText("");
                 jTextArea1.setText("");
                 
                 
                // special case for when all pods are deleted, there is a problem here with the tables before the podTracker implementation
                System.out.println("Deleted the last element and is now not tracking anything!");
                podTracker.isNew = false;
                podTracker.hasOne = false;
            }
        }
    }
// ****************************************************************************

    public PODEditor_View(String workingCopyName, ScenarioState state) {
        this(workingCopyName, new PODList(), state);
        podTracker.hasOne = false;
        this.editLocationButton.setEnabled(false);
        this.jButton6.setEnabled(false);
        this.setGeocoding();
        
        this.setTitle("POD Editor - Scenario: " + state.getName());
    }

    public PODEditor_View(String workingCopyName, final PODList pods, ScenarioState state) {
      this.setTitle("POD Editor - Scenario: " + state.getName());
      this.setGeocoding();
        this.state = state;
        podList = pods;
        podList.addObserver(this);
        System.out.println("In Constructor, number of PODS: " + podList.get_number_of_pods());

        podListModel = new ListData();

        initComponents();

        jList2.setCellRenderer(new IconListRenderer());
        jList2.addListSelectionListener(new selectListener());
        
        // set image for refresh button
        ImageIcon temp = new ImageIcon("Refresh.png");
        Image rImage = temp.getImage();
        Image newRImage = rImage.getScaledInstance(20, 15, java.awt.Image.SCALE_SMOOTH);
        ImageIcon refreshImage = new ImageIcon(newRImage);
        jButton8.setText(null);
        jButton8.setIcon(refreshImage);
        //System.out.println("BUTTON SIZE*****************");
        //System.out.println(jButton8.getPreferredSize());
        //System.out.println("BUTTON SIZE*****************");
        
        this.editLocationButton.setEnabled(false);
        this.jButton6.setEnabled(false);
        
        if (!podList.isEmpty()) {
            
            this.editLocationButton.setEnabled(true);
            this.jButton6.setEnabled(true);

            int id = 1;

            //try {
            while (id <= pods.get_number_of_pods()) {

                podListModel.addElement(podList.access_POD(podList.get_pod_fid(id)));
                id++;
            }
            //podListModel.addElementToEnd();
            //} catch (SQLException ex) {
            // Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
            //}
  
            jList2.setSelectedIndex(pods.get_number_of_pods() - 1);
            jList2.ensureIndexIsVisible(pods.get_number_of_pods() - 1);
            jList2.repaint();
        }
        // need to load data from saved scenario here


        jList2.setSelectedIndex(1); // hack: for some reason, won't load fields with data from first POD unless we select a different POD first
        jList2.setSelectedIndex(0); // when PODList window is first created, selected the first POD (index ==0) in the list
        System.out.println("$$$$$$$$$$selected index: " + jList2.getSelectedIndex());
        
        // follow the first pod for the first time after the creation of the podlist
        if (podList.access_POD(0) != null){
            this.editLocationButton.setEnabled(true);
            this.jButton6.setEnabled(true);
            podTracker.hasOne = true;
            String podtype = String.valueOf(jComboBox1.getSelectedItem());
            boolean type;
            if (podtype.equals("Public") || podtype.equals("TRUE")) {
                type = true;
            } else {
                type = false;
            }
            
             
            // ***UPDATED FOR NEW UI:***
            podTracker.setNew(
                   jList2.getSelectedIndex()+1,
                   jTextField1.getText(),
                   streetLabel.getText(),
                   cityLabel.getText(),
                   zipLabel.getText(),
                   jTextArea1.getText(),
                   Double.valueOf(lonLabel.getText()),
                   Double.valueOf(latLabel.getText()),
                   Integer.parseInt((jSpinner1.getValue()).toString()),
                    podList.access_POD(0).get_id(),
                    podList.access_POD(0).get_fid(),
                    jToggleButton1.isSelected(),
                    type
            );
            podTracker.isNew = false;
            
        }
        System.out.println("set PODTracker initially to -> " + podTracker.name);
    }
   
    
    
    
//    public PODEditor_View(int dummy) {
    //  }
    //public PODEditor_View(String workingCopyName, int deleteMe) {
    //  System.out.println("In the POD Editor");
    // }
    public void setPodList(PODList podList) {
        System.out.println("setPodList");
        this.podList = podList;
    }

    public void displayPodList() {
        System.out.println("displayPodList");
        try {
            System.out.println("displayPodList 52: podListModel " + podListModel);
            if (podListModel == null) {
                podListModel = new ListData();
            }
            System.out.println("displayPodList 54: podListModel " + podListModel);
            int size = podList.get_number_of_pods();
            System.out.println("displayPodList 56: size = " + size);
            for (int i = 0; i < size; i++) {
                System.out.println("displayPodList 59: i = " + i);
//                podListModel.setElementAt( podList.access_POD_by_id(i+1), i );
//                POD temp = podList.access_POD( podList.get_pod_fid(i+1) );
//                System.out.println("displayPodList 61");
//                podListModel.addElement(temp);
                POD temp = podList.access_POD(podList.get_pod_fid(i + 1));
                System.out.println("displayPodList 65: POD = " + temp.toString());
                podListModel.addElement(temp);
                System.out.println("displayPodList 67");
            }
            jList2.setSelectedIndex(size - 1);
            System.out.println("displayPodList 69");
        } catch (Exception ex) {
            Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PODList getPodList() {
        System.out.println("getPodList");
        return podList;
    }

    public String getPodType(int index) {
        if (podList.isEmpty()) {
            return null;
        } else {
            if (podList.access_POD_by_id(index + 1).get_type()) {
                return "public";
            }
            return "private";
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        streetLabel = new javax.swing.JLabel();
        cityLabel = new javax.swing.JLabel();
        zipLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lonLabel = new javax.swing.JLabel();
        latLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton("Select Location from Map", new ImageIcon(LocationSelector.ICON_IMAGE));
        hidePODEditorButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        editLocationButton = new javax.swing.JButton();

        setResizable(false);

        jList2.setModel(this.podListModel);
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.setMaximumSize(new java.awt.Dimension(200, 200));
        jList2.setMinimumSize(new java.awt.Dimension(50, 50));
        jList2.setSelectedIndex(0);
        jScrollPane3.setViewportView(jList2);

        jLabel1.setText("Name:");

        jLabel8.setText("POD Type:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Public", "Private" }));

        jLabel7.setText("Booths:");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        jLabel9.setText("POD Status:");

        statusLabel.setText("OFF");

        jToggleButton1.setText("Turn POD: ON");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Address:");

        streetLabel.setText("(Current address not given)");

        zipLabel.setText("00000");

        jLabel6.setText("Latitude:");

        jLabel14.setText("Longitude:");

        lonLabel.setText("0.0");

        latLabel.setText("0.0");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel10.setText("Comments:");

        jButton1.setText("Add POD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Delete POD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton8.setText("↺");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel11.setText("Number Of PODs To Add:");

        jButton5.setText("OK");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton3.setText("Load from FIle");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton7.setText("Export to CSV");
        jButton7.setToolTipText("Export PODs to a CSV file");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton6.setText("Select Location On Map");
        jButton6.setToolTipText("Select POD Location on Map");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        hidePODEditorButton.setText("Hide POD Editor");
        hidePODEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hidePODEditorButtonActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        editLocationButton.setText("Edit Location");
        editLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLocationButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField9, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(46, 46, 46)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(editLocationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField1)
                                    .addComponent(jSpinner1)
                                    .addComponent(latLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lonLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(cityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(zipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(streetLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(hidePODEditorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jToggleButton1)
                            .addComponent(statusLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(streetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(zipLabel))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(lonLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(latLabel))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6)
                            .addComponent(editLocationButton))
                        .addGap(29, 29, 29)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1)
                                    .addComponent(jButton8)
                                    .addComponent(jButton2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton5))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hidePODEditorButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void toggleLoadButton() {
        if (jButton3.isEnabled()) {
            jButton3.setEnabled(false);
        } else {
            jButton3.setEnabled(true);
        }
        jButton3.setVisible(false);
        jButton3.setVisible(true);
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        
        
        final PODEditor_View tempEditor = this;
        
        new LoadPodsFromCSV(this);
        
        
        statusBarWorker = new javax.swing.SwingWorker<Void, Void>() {
            @Override
            // Do process in background, return null
            public Void doInBackground() throws InterruptedException {
                // PROGRES BAR 
                // Create progress bar because multiple loads can take a little time
                final JProgressBar proBar = new JProgressBar(0,100);
                proBar.setPreferredSize(new Dimension(175,20));
                proBar.setStringPainted(true);
                proBar.setValue(0); 
                JLabel label = new JLabel("Generating Coordinates:");
                JPanel center_panel = new JPanel();
                center_panel.add(label);
                center_panel.add(proBar);
                
                
                // Create dialog to hold the loading bar
                //final JDialog dialog = new JDialog((JFrame)null, "Generating coordinates...");
                final JDialog dialog = new JDialog(tempEditor, "Generating Coordinates...");
                dialog.setUndecorated(true);
                dialog.getContentPane().add(center_panel, BorderLayout.CENTER);
                dialog.pack();
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(tempEditor.jList2);
                dialog.toFront();
                
                tempEditor.setEnabled(false);
                
                
                //new LoadPodsFromCSV(tempEditor);
                
                final int step = (100/podList.get_number_of_pods());
                
                podListModel.setSize(podList.get_number_of_pods());    
                for (int index = 0; index < podList.get_number_of_pods(); index++) {
                    
                    // Set the progress bar
                    proBar.setValue(proBar.getValue()+step);
                    proBar.repaint();
                    
                    // if the pod needs geocoding & has all the proper parameters
                    if (tempEditor.useGeocoding) {
                        if (podList.access_POD_by_id(index + 1).get_latitude() == 0.0 || podList.access_POD_by_id(index + 1).get_longitude() == 0.0){
                            if (!podList.access_POD_by_id(index + 1).get_address().isEmpty() && !podList.access_POD_by_id(index + 1).get_city().isEmpty() && !podList.access_POD_by_id(index + 1).get_zip().isEmpty()){
                                // TAMU's Geocoding:
                                String TAMU_address = "streetAddress=" + podList.access_POD_by_id(index + 1).get_address().replaceAll(" ", "%20") + 
                                                      "&city=" + podList.access_POD_by_id(index + 1).get_city().replaceAll(" ", "%20") + "&state=tx" +
                                                      "&zip=" + podList.access_POD_by_id(index + 1).get_zip().trim();

                                TAMU_Geocoding geo = new TAMU_Geocoding();
                                String[] latLonInfo = new String[2];



                                latLonInfo = geo.getData_TAMU_Geocoder(TAMU_address);
                                
                                // make sure tamu's site is responding correctly
                                if (latLonInfo == null){
                                    REPLAN.GeocodingSessionAvailable = false;
                                    useGeocoding = false;
                                    break;
                                }

                                if (!latLonInfo[0].isEmpty() && !latLonInfo[1].isEmpty()) {

                                    podList.access_POD_by_id(index + 1).set_latitude(Double.parseDouble(latLonInfo[0]));
                                    podList.access_POD_by_id(index + 1).set_longitude(Double.parseDouble(latLonInfo[1]));


                                    try {
                                        podList.update_pod_dontNotify_2(
                                                index+1, 
                                                podList.access_POD_by_id(index + 1).get_name(), 
                                                podList.access_POD_by_id(index + 1).get_address(), 
                                                podList.access_POD_by_id(index + 1).get_city(),
                                                podList.access_POD_by_id(index + 1).get_zip(), 
                                                podList.access_POD_by_id(index + 1).get_longitude(), 
                                                podList.access_POD_by_id(index + 1).get_latitude(),
                                                podList.access_POD_by_id(index + 1).get_type(), 
                                                podList.access_POD_by_id(index + 1).get_status(), 
                                                podList.access_POD_by_id(index + 1).get_comments(), 
                                                podList.access_POD_by_id(index + 1).get_numBooths()
                                        );
                                        
                                        
                                    } catch (SQLException ex) {
                                        REPLAN.print(REPLAN.PrintType.ENVIRONMENT, "Couldn't update POD with geocoding to database");
                                    }

                                }


                            }
                        }
                    }
                }
                
                podList.setListChanged();
                REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setPodsChanged();
                REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setChangedAndNotify();
                
                
                for (int index = 0; index < podList.get_number_of_pods(); index++) {
                    podListModel.setElementAt(podList.access_POD_by_id(index + 1), index);
                }
                

                // if the PODTracker and podList are out of sync due to adding first
                // pods by loading them from a file
                if (!podTracker.hasOne && podList.access_POD(0) != null){

                    REPLAN.print(REPLAN.PrintType.DEVELOPMENT, "Has PODs but PODTracker does not think it does");


                    
                    // enable the features because the editor now has PODs
                    editLocationButton.setEnabled(true);
                    jButton6.setEnabled(true);

                    // set the PODTracker to the currently selected POD in the podList
                    podTracker.hasOne = true;
                        String podtype = String.valueOf(jComboBox1.getSelectedItem());
                        boolean type;
                        if (podtype.equals("Public") || podtype.equals("TRUE")) {
                            type = true;
                        } else {
                            type = false;
                        }
                        
                        
                        // set the pod tracker to follow the current on on the screen
                        podTracker.setNew(
                               jList2.getSelectedIndex()+1,
                               jTextField1.getText(),
                               streetLabel.getText(),
                               cityLabel.getText(),
                               zipLabel.getText(),
                               jTextArea1.getText(),
                               Double.valueOf(lonLabel.getText()),
                               Double.valueOf(latLabel.getText()),
                               Integer.parseInt((jSpinner1.getValue()).toString()),
                                podList.access_POD(0).get_id(),
                                podList.access_POD(0).get_fid(),
                                jToggleButton1.isSelected(),
                                type
                        );
                        podTracker.isNew = false;

                }
                
                dialog.dispose();
                tempEditor.setEnabled(true);
                //update podListModel with pods from updated podList
                jList2.setSelectedIndex(podListModel.getSize() - 1);
                jList2.repaint();
                
                return null;
            }
        };
        
        this.statusBarWorker.execute();
       
    }//GEN-LAST:event_jButton3ActionPerformed

    public void updatePODTracker(){
        // if the PODTracker and podList are out of sync due to adding first
        // pods by loading them from a file
        if (!podTracker.hasOne && podList.access_POD(0) != null){

            REPLAN.print(REPLAN.PrintType.DEVELOPMENT, "Has PODs but PODTracker does not think it does");



            // enable the features because the editor now has PODs
            editLocationButton.setEnabled(true);
            jButton6.setEnabled(true);

            // set the PODTracker to the currently selected POD in the podList
            podTracker.hasOne = true;
                String podtype = String.valueOf(jComboBox1.getSelectedItem());
                boolean type;
                if (podtype.equals("Public") || podtype.equals("TRUE")) {
                    type = true;
                } else {
                    type = false;
                }
                
                // set the pod tracker to follow the current on on the screen
                podTracker.setNew(
                       jList2.getSelectedIndex()+1,
                       jTextField1.getText(),
                       streetLabel.getText(),
                       cityLabel.getText(),
                       zipLabel.getText(),
                       jTextArea1.getText(),
                       Double.valueOf(lonLabel.getText()),
                       Double.valueOf(latLabel.getText()),
                       Integer.parseInt((jSpinner1.getValue()).toString()),
                        podList.access_POD(0).get_id(),
                        podList.access_POD(0).get_fid(),
                        jToggleButton1.isSelected(),
                        type
                );
                podTracker.isNew = false;
        }
    }

    private void hidePODEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hidePODEditorButtonActionPerformed
        //Hide button        
        if (podListModel.getSize() == 0) {
            this.setVisible(false);
        }
        else {
            this.updateSave();
            this.setVisible(false);
        }
    }//GEN-LAST:event_hidePODEditorButtonActionPerformed
    
    
    private void updateSave(){
        if (podListModel.getSize() == 0) {
            this.setVisible(false);
        }
        // change the save button to be a hide button instead but with similar implementation, just hide the editor after the save
        else {
            String podtype = String.valueOf(jComboBox1.getSelectedItem());
            boolean type;
            if (podtype.equals("Public") || podtype.equals("TRUE")) {
                type = true;
            } else {
                type = false;
            }

            
            // ***UPDATED FOR NEW UI:***
            podTracker.checkForChanges(
                    podTracker.index,
                    jTextField1.getText(),
                    streetLabel.getText(),
                    cityLabel.getText(),
                    zipLabel.getText(),
                    jTextArea1.getText(),
                    lonLabel.getText(),
                    latLabel.getText(),
                    Integer.parseInt((jSpinner1.getValue()).toString()),
                    podList.access_POD(0).get_id(),
                    podList.access_POD(0).get_fid(),
                    jToggleButton1.isSelected(),
                    type
            );
            
            if (podTracker.hasChanges) {

                String name = jTextField1.getText();                
                
                 // ***UPDATED FOR NEW UI:***
                String address = streetLabel.getText();
                String city = cityLabel.getText();
                String zip = zipLabel.getText();
                String lonstr = lonLabel.getText();
                String latstr = latLabel.getText();
                
                int booths = Integer.parseInt((jSpinner1.getValue()).toString());
                String comments = jTextArea1.getText();
                boolean status = jToggleButton1.isSelected();

                if (zip.isEmpty() || lonstr.isEmpty() || latstr.isEmpty()) {
                    //pop dialog

                    JOptionPane.showMessageDialog(null, "Please provide a value for fields with an *", "Message", JOptionPane.WARNING_MESSAGE);
                    return;

                }
                
                Float lon = Float.parseFloat(lonstr);
                Float lat = Float.parseFloat(latstr);

                int index = jList2.getSelectedIndex();
                try {
                    podListModel.updateElement(index + 1, name, address, city, zip, lon, lat, type, status, comments, booths);
                } catch (SQLException ex) {
                    Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
                }    
                /*
                podList.update_pod(index + 1, name, address, city, zip, lon, lat, type, status, comments, booths);
                try {
                PODQueries.update_POD(podList.get_pod_fid(index + 1), name, address, city, zip, lon, lat, type, status, comments, booths);
                } catch (SQLException ex) {
                Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
                }
                selectListenerFlag = 1;
                selectListenerFlag = 0;
                 */
                //Select the new item and make it visible.
                jList2.setSelectedIndex(index);
                jList2.ensureIndexIsVisible(index);

                jList2.repaint();
                if (locSelectorActive) {
                    updateCursor();
                }
            }
        }
    }
    
    
    private void updateCursor() {


        //Toolkit tk = Toolkit.getDefaultToolkit();
        //ImageIcon cursorIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
        //Cursor cursor;
        //cursor = tk.createCustomCursor(cursorIcon.getImage(), CURSOR_HOTSPOT, TOOL_TIP);
        locSelector.setCursor(Cursor.getDefaultCursor());
        locSelector.getSelCoords().deleteObservers();
        locSelectorActive = false;

    }

    public void toggleAddButton() {
        if (jButton1.isEnabled()) {
            jButton1.setEnabled(false);
        } else {
            jButton1.setEnabled(true);
        }
        jButton1.setVisible(false);
        jButton1.setVisible(true);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            System.out.println("jButton1ActionPerformed: Add POD invoked");
            podListModel.addElementToEnd();
            this.editLocationButton.setEnabled(true);
            this.jButton6.setEnabled(true);
        } catch (SQLException ex) {
            Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
        }
        jList2.repaint();
//        podList.add_pod("new POD", null, null, null, -999, -999, true, true, null, 0);
//        jList2.setListData(podListModel);
    }//GEN-LAST:event_jButton1ActionPerformed

    public void toggleDeleteButton() {
        if (jButton2.isEnabled()) {
            jButton2.setEnabled(false);
        } else {
            jButton2.setEnabled(true);
        }
        jButton2.setVisible(false);
        jButton2.setVisible(true);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            //DELETE pod button
            podListModel.deleteElementAt(jList2.getSelectedIndex());
            /*
            if (jList2.getSelectedIndex() == -1) {
            System.out.println("You hit delete, but you didn't have anything selected!");
            return; //nothing selected
            }
            int id = jList2.getSelectedIndex() + 1; //list index is indexed from 0, id is indexed from 12
            //podListModel.removeElementAt(id);
            podListModel.removeAllElements();
            selectListenerFlag = 1;
            System.out.println("*********************************************Deleting POD id " + id);
            podList.delete_pod(podList.get_pod_fid(id));
            System.out.println("POD Deleted. " + podList.get_number_of_pods() + " PODs Remain");
            selectListenerFlag = 0;
             *
             */
        } catch (SQLException ex) {
            Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    public void toggleOkButton() {
        if (jButton5.isEnabled()) {
            jButton5.setEnabled(false);
        } else {
            jButton5.setEnabled(true);
        }
        jButton5.setVisible(false);
        jButton5.setVisible(true);
    }

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // OK button
        int numBooths = Integer.parseInt(jTextField9.getText());
        String name = "<unnamed>";
        String address = null;
        String city = null;
        String zip = null;
        float lat = 0;
        float lon = 0;
        String podtype = "Public";  //default: public
        String comments = null;
        boolean status = false;   //default: off
        int booths = 0;
        
        for (int i = 0; i < numBooths; i++) {
//            PodEntry pe = new PodEntry(name, address, city, lon, lat, zip, podtype, booths, status, comments);
//            boolean type;
//            if (podtype.equals("public")) {
//                type = true;
//            } else {
//                type = false;
//            }

            //podList.add_pod(name, address, city, zip, lon, lat, type, status, comments, booths);
            try {
                podListModel.addElementToEnd();
            } catch (SQLException ex) {
                Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
            }
            jList2.repaint();


            int index = jList2.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
                index++;
            }
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // Location Selector
        //PodInfoTool podInfoTool = new PodInfoTool();
        // MapAction: super.init(mapPane, toolName, "Select location on map", PodInfoTool.ICON_IMAGE);
        if (podListModel.getSize() == 0) {
            JOptionPane.showMessageDialog(null, "Please add a POD before you try to save it", "Message", JOptionPane.WARNING_MESSAGE);
            return;
        }
        locSelectorActive = true;
        locSelector = new LocationSelector();
        mapPane.setCursorTool(locSelector);
        locSelector.getSelCoords().addObserver(this);
        REPLAN.getMainFrame().toFront();  
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        //Export PODs to CSV

        JFileChooser fc1 = new JFileChooser();

        int fc1_state_on_popdown = fc1.showSaveDialog(this);
        File file1 = fc1.getSelectedFile();

        try {
            if (fc1_state_on_popdown == JFileChooser.APPROVE_OPTION) {
                System.out.println("new file: " + file1.getPath());
                PODs2CSV pods2csv = new PODs2CSV();
                pods2csv.exportPOD_fromDB_CSV(file1.getPath());
            } else {
                System.out.println("File save cancelled or error! (if you want to know which, write more code, lazy!)");
            }
            //ExportCSV ex
            //  try {
            //  try {
            //ExportCSV.exportPOD_fromDB_CSV(scenario, file1.getPath());
            //  } catch (IOException ex) {
            //      Logger.getLogger(Jpod2.class.getName()).log(Level.SEVERE, null, ex);
            //  } catch (SQLException ex) {
            //      Logger.getLogger(Jpod2.class.getName()).log(Level.SEVERE, null, ex);
            //  }
        } catch (IOException ex) {
            Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PODEditor_View.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // Refresh Button: Refresh POD list, refresh nuber of booths
        podList.setList(REPLAN.getQueries().readWrkCpyPODsFromDB(state, REPLAN.getController().getConnection()));
        
        selectListenerFlag = 0;
//        int num = podList.get_number_of_pods();
//        for (int id = 1; id <= num; id++) {
//            int fid = podList.get_pod_fid(id);
//            jSpinner1.setValue((Object) podList.access_POD(fid).get_numBooths());
//        }


    }//GEN-LAST:event_jButton8ActionPerformed

    // if the toggle button (Turn POD ON/OFF) is pressed, change change text to represent the new state
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
          podListModel.PODStatusIsOn(jToggleButton1.isSelected());
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void editLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLocationButtonActionPerformed
        // create a form for editing & converting location
        editLocationForm editForm = null;
        if (editForm == null) {
            editForm = new editLocationForm(this,true, useGeocoding);
            
            editForm.populateFields(streetLabel.getText(), cityLabel.getText(), zipLabel.getText(), 
                                    latLabel.getText(), lonLabel.getText());
            
        }
        if (!editForm.isVisible()){
            editForm.setVisible(true);
        }
        
        // update status of using geocoding for the session
        if (!editForm.isUsingGeocoding){
            REPLAN.GeocodingSessionAvailable = false;
            useGeocoding = false;
        }
        
        if (editForm.formIsValid) {
            
            if (editForm.foundAddress && editForm.foundCoordinates){
                streetLabel.setText(editForm.street);
                lonLabel.setText(editForm.lon.toString());
                latLabel.setText(editForm.lat.toString());
                cityLabel.setText(editForm.city);
                zipLabel.setText(editForm.zip);
                // update changes to map also
                this.updateSave();
            }
            else if (editForm.foundAddress && !editForm.foundCoordinates){
                streetLabel.setText(editForm.street);
                lonLabel.setText("0.0");
                latLabel.setText("0.0");
                cityLabel.setText(editForm.city);
                zipLabel.setText(editForm.zip);
                // update changes to map also
                this.updateSave();
            }
            else if (!editForm.foundAddress && editForm.foundCoordinates){
                streetLabel.setText(editForm.street);
                lonLabel.setText(editForm.lon.toString());
                latLabel.setText(editForm.lat.toString());
                cityLabel.setText(editForm.city);
                zipLabel.setText(editForm.zip);
                // update changes to map also
                this.updateSave();
            }
            
        }
    }//GEN-LAST:event_editLocationButtonActionPerformed

    @Override
    public void update(Observable o, Object arg) {
        if (o.getClass() == PODList.class) {
            //jList2.setSelectedIndex(podList.get_number_of_pods() - 1);
            //jList2.ensureIndexIsVisible(podList.get_number_of_pods() - 1);
            jList2.repaint();
        } else {
            DirectPosition2D pos = locSelector.getLocationSelected();
            setLocationCoords(pos);
        }
    }

    public void setLocationCoords(DirectPosition2D pos) {

        //TODO: Handle error case.. what if no selection is made in the list
        /*
        jTextField5.setText(Double.toString(pos.x));
        jTextField6.setText(Double.toString(pos.y));
        */
        
        // ***UPDATED FOR NEW UI:***
        lonLabel.setText(Double.toString(pos.x));
        latLabel.setText(Double.toString(pos.y));
        
        
        /*
        // TAMU's Reverse Geocoding API:
       String TAMU_latLon = "lat=" + latLabel.getText().trim() + "&lon=" + lonLabel.getText().trim();
       addressComps = revGeo.getData_TAMU_RGeocoder(TAMU_latLon);
       
       streetLabel.setText(addressComps[0]);
       cityLabel.setText(addressComps[1]);
       zipLabel.setText(addressComps[2]);
        */
        
        if (useGeocoding) {
            // Auto fill in the address when location is selected
            
            /*
             // TAMU's Reverse Geocoding API:
            ReverseGeocoding revGeo = new ReverseGeocoding();
            String TAMU_latLon = "lat=" + latLabel.getText().trim() + "&lon=" + lonLabel.getText().trim();
            String[] addressComps = new String[3];
            addressComps = revGeo.getData_TAMU_RGeocoder(TAMU_latLon);
            
            System.out.println("*******=>"+addressComps[0]);
            streetLabel.setText(addressComps[0]);
            cityLabel.setText(addressComps[1]);
            zipLabel.setText(addressComps[2]);
            */
            
            
            
            
           /*
            // Google's Reverse Geocoding API:
            ReverseGeocoding revGeo = new ReverseGeocoding();
            String[] tempAddressComps = new String[3];
            String latLon = latLabel.getText().trim() + "," + lonLabel.getText().trim(); 
            tempAddressComps = revGeo.getData_Google_RGeocoder(latLon);
             streetLabel.setText(tempAddressComps[0]);
            cityLabel.setText(tempAddressComps[1]);
            zipLabel.setText(tempAddressComps[2]);
            */
            
        }
        locSelector.getSelCoords().clearChange();
        //scenario.getMapPane().setRepaint(true); // copied from jpod2, do we need this?
       
        // update UI to save and show the new selected location
        boolean type;
        String podtype;
        podtype = String.valueOf(jComboBox1.getSelectedItem());

        if (podtype.equals("Public") || podtype.equals("TRUE")) {
            type = true;
        } else {
            type = false;
        }
        // check for any changes
         System.out.println("podTracker.hasChanges = "+ podTracker.hasChanges);

         
        // ***UPDATED FOR NEW UI:***
         podTracker.checkForChanges(
                podTracker.index,
                jTextField1.getText(),
                streetLabel.getText(),
                cityLabel.getText(),
                zipLabel.getText(),
                jTextArea1.getText(),
                lonLabel.getText(),
                latLabel.getText(),
                Integer.parseInt((jSpinner1.getValue()).toString()),
                podList.access_POD(0).get_id(),
                podList.access_POD(0).get_fid(),
                jToggleButton1.isSelected(),
                type
        );
        
        
        System.out.println("podTracker.hasChanges = "+ podTracker.hasChanges);
        if (podTracker.hasChanges == true) {


                // update the pod that was just switched off of
                System.out.println("*******update with the the info:\n" 
                    +podTracker.index +"  " +podTracker.name +"  " +podTracker.address +"  " 
                    +podTracker.city +"  " +podTracker.zip +"  " +podTracker.lon +"  " +podTracker.lat +"  " +podTracker.type +"  " 
                    +podTracker.status +"  " +podTracker.comments +"  " +podTracker.booths +"  " );

                try {
                    podListModel.updateElement(
                                podTracker.index,
                                podTracker.name,
                                podTracker.address,
                                podTracker.city,
                                podTracker.zip,
                                podTracker.lon,
                                podTracker.lat,
                                podTracker.type,
                                podTracker.status,
                                podTracker.comments,
                                podTracker.booths
                            );
                } catch (SQLException ex) {
                    System.out.println("couldn't update the pod");
                }
        }

    }
    
    public void setMapPane(JMapPane mapPane) {
        this.mapPane = mapPane;

    }

    public class selectListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            
            if (selectListenerFlag == 1) {
                return;
            }
            int selectedIndex = jList2.getSelectedIndex();
            if (selectedIndex == -1) {
                selectedIndex = 0;
            }
            int id = selectedIndex + 1; // to adjust from index from 0 to index from 1
            int fid = podList.get_pod_fid(id);
            boolean type;
            String podtype;
            
            // logic for the pod tracker to follow pods only when it was changed indexs and save only when there
            // are changes from its last save
            if (podTracker.hasOne && fid != -1) {
                podTracker.next = podList.access_POD(fid).get_name();
                if (podTracker.needsToSave(jList2.getSelectedIndex() + 1)){
                    
                    System.out.println("Check for changes...");
                    
                   // catch all changes at time of switch
                    podtype = String.valueOf(jComboBox1.getSelectedItem());

                    if (podtype.equals("Public") || podtype.equals("TRUE")) {
                        type = true;
                    } else {
                        type = false;
                    }
                    // check for any changes
                     System.out.println("podTracker.hasChanges = "+ podTracker.hasChanges);
                     
                     
                    // ***UPDATED FOR NEW UI:***
                     podTracker.checkForChanges(
                            podTracker.index,
                            jTextField1.getText(),
                            streetLabel.getText(),
                            cityLabel.getText(),
                            zipLabel.getText(),
                            jTextArea1.getText(),
                            lonLabel.getText(),
                            latLabel.getText(),
                            Integer.parseInt((jSpinner1.getValue()).toString()),
                            podList.access_POD(0).get_id(),
                            podList.access_POD(0).get_fid(),
                            jToggleButton1.isSelected(),
                            type
                    ); 
                    
                    System.out.println("podTracker.hasChanges = "+ podTracker.hasChanges);
                    if (podTracker.hasChanges == true) {
                        // check for valid input
                        if (podTracker.hasValidInput()) {
                        
                            // update the pod that was just switched off of
                            System.out.println("*******update with the the info:\n" 
                                +podTracker.index +"  " +podTracker.name +"  " +podTracker.address +"  " 
                                +podTracker.city +"  " +podTracker.zip +"  " +podTracker.lon +"  " +podTracker.lat +"  " +podTracker.type +"  " 
                                +podTracker.status +"  " +podTracker.comments +"  " +podTracker.booths +"  " );

                            try {
                                podListModel.updateElement(
                                            podTracker.index,
                                            podTracker.name,
                                            podTracker.address,
                                            podTracker.city,
                                            podTracker.zip,
                                            podTracker.lon,
                                            podTracker.lat,
                                            podTracker.type,
                                            podTracker.status,
                                            podTracker.comments,
                                            podTracker.booths
                                        );
                            } catch (SQLException ex) {
                                System.out.println("couldn't update the pod");
                            }
                        }
                    }
                    
                   // set the UI to show the currently selected & saved pod
                    jTextField1.setText(podList.access_POD(fid).get_name());

                    String typeSelected;
                    if (podList.access_POD(fid).get_type()) {
                        typeSelected = "Public";
                    } else {
                        typeSelected = "Private";
                    }
                    jComboBox1.setSelectedItem(typeSelected);
                    jSpinner1.setValue((Object) podList.access_POD(fid).get_numBooths());
                    jTextArea1.setText(podList.access_POD(fid).get_comments());
                    jToggleButton1.setSelected(podList.access_POD(fid).get_status());
                    podListModel.PODStatusIsOn(podList.access_POD(fid).get_status());
                    
                    // Set the NEW UI to show the currently selected & saved POD
                    if (podList.access_POD(fid).get_address().equals("NULL") || 
                        podList.access_POD(fid).get_address().isEmpty()    ||
                        podList.access_POD(fid).get_city().equals("<unnamed>") || 
                        podList.access_POD(fid).get_city().isEmpty()){
                            streetLabel.setText("(Current address not given)");
                            cityLabel.setText(" ");
                            zipLabel.setText("00000");
                    }
                    else {
                        streetLabel.setText(podList.access_POD(fid).get_address());
                        cityLabel.setText(podList.access_POD(fid).get_city());
                        zipLabel.setText(podList.access_POD(fid).get_zip());
                    }
                    latLabel.setText(Double.toString(podList.access_POD(fid).get_latitude()));
                    lonLabel.setText(Double.toString(podList.access_POD(fid).get_longitude()));
                    
                
                    // track the new pod from the change in selection
                    podtype = String.valueOf(jComboBox1.getSelectedItem());

                    if (podtype.equals("Public") || podtype.equals("TRUE")) {
                        type = true;
                    } else {
                        type = false;
                    }

                    
                    // ***UPDATED FOR NEW UI:***
                    podTracker.setNew(
                           jList2.getSelectedIndex()+1,
                           jTextField1.getText(),
                           streetLabel.getText(),
                           cityLabel.getText(),
                           zipLabel.getText(),
                           jTextArea1.getText(),
                           Double.valueOf(lonLabel.getText()),
                           Double.valueOf(latLabel.getText()),
                           Integer.parseInt((jSpinner1.getValue()).toString()),
                            podList.access_POD(0).get_id(),
                            podList.access_POD(0).get_fid(),
                            jToggleButton1.isSelected(),
                            type
                    );
                    
                    podTracker.hasChanges = false;
                    podTracker.isNew = false;
                    System.out.println("now tracking pod with name ->" + podTracker.name + " and index -> " + podTracker.index);
                }
            }
            
            // initial setup
            if (fid != -1 && podTracker.hasOne == false) {
                
                jTextField1.setText(podList.access_POD(fid).get_name());

                String typeSelected;
                if (podList.access_POD(fid).get_type()) {
                    typeSelected = "Public";
                } else {
                    typeSelected = "Private";
                }

                //jList1.setSelectedValue(typeSelected, true);

                jComboBox1.setSelectedItem(typeSelected);
                jSpinner1.setValue((Object) podList.access_POD(fid).get_numBooths());
                jTextArea1.setText(podList.access_POD(fid).get_comments());
                jToggleButton1.setSelected(podList.access_POD(fid).get_status());
                podListModel.PODStatusIsOn(podList.access_POD(fid).get_status());
                
                // Set the NEW UI to show the currently selected & saved POD
                if (podList.access_POD(fid).get_address().equals("NULL") || 
                    podList.access_POD(fid).get_address().isEmpty()    ||
                    podList.access_POD(fid).get_city().equals("<unnamed>") || 
                    podList.access_POD(fid).get_city().isEmpty()){
                        streetLabel.setText("(Current address not given)");
                        cityLabel.setText(" ");
                        zipLabel.setText(" ");
                }
                else {
                    streetLabel.setText(podList.access_POD(fid).get_address());
                    cityLabel.setText(podList.access_POD(fid).get_city());
                    zipLabel.setText(podList.access_POD(fid).get_zip());
                }
                latLabel.setText(Double.toString(podList.access_POD(fid).get_latitude()));
                lonLabel.setText(Double.toString(podList.access_POD(fid).get_longitude()));
            }
            
        }
    }

    class IconListRenderer extends JLabel implements ListCellRenderer {

        DiamondIcon greenIcon = new DiamondIcon(Color.green);
        DiamondIcon blueIcon = new DiamondIcon(Color.blue);
        DiamondIcon redIcon = new DiamondIcon(Color.red);

        public Component getListCellRendererComponent(
                JList list,
                Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // the list and the cell have the focus
        {
            String s = String.valueOf(index + 1) + " " + value.toString();
            setText(s);

            // I will probably need all of this back!!!
            if (podList.access_POD_by_id(index + 1).get_type() == false) {
                setIcon(blueIcon);
            } else {
                if (podList.access_POD_by_id(index + 1).get_status() == true) {
                    setIcon(greenIcon);
                } else {
                    setIcon(redIcon);
                }

            }//*/


            // This is causing the POD Editor to crash. I don't see where index is initialized. Temporarily commenting out this code
/*
            if (podList.access_POD(index).get_type() == false) {
            setIcon(blueIcon);
            } else {
            setIcon(greenIcon);

            }

             */

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    public ScenarioState getScenarioState() {
        return state;
    }
    /*
    // main method for development purposes only
    public static void main(String args[]) {
    final PODList pods = new PODList();

    //pods.add_pod("Marty O", "1208 N Locust St", "Denton", "76201", 123.456, 321.456, true, true, "This is my old address", 23);
    //pods.add_pod("Tricka Hardini", "2418 Stella St Apt 3", "Denton", "76201", 123.456, 321.456, true, true, "Robot Tricka attack!", 23);
    //pods.add_pod("April", "4009 Caddo Trail", "Fort Worth", "76135", 123.456, 321.456, true, true, "She's a lil' girl", 23);
    //        pods.add_pod("Shaun Szkolnik", "4312 English Cove", "Gulfport", "39503", 123.456, 321.456, true, true, "Shaun is pretty cool", 23);
    //pods.delete_pod(2);
    System.out.println("***************************************************");
    System.out.println("***************************************************");
    System.out.println("***************************************************");
    System.out.println("number of pods: " + pods.get_number_of_pods());
    //        System.out.println("new pod id: " + pods.get_pod_id(0));
    System.out.println("number of pods:" + pods.get_number_of_pods());
    System.out.println("***************************************************");
    System.out.println("***************************************************");
    System.out.println("***************************************************");
    java.awt.EventQueue.invokeLater(new Runnable() {

    public void run() {
    new PODEditor_View("delete me", pods, state).setVisible(true);
    }
    });
    }
     *
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cityLabel;
    private javax.swing.JButton editLocationButton;
    private javax.swing.JButton hidePODEditorButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel latLabel;
    private javax.swing.JLabel lonLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel streetLabel;
    private javax.swing.JLabel zipLabel;
    // End of variables declaration//GEN-END:variables
}
