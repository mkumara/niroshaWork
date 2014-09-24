
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Settings;
import dataOperations.SummarizePodStatsFromDB;

import action.MenuListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import javax.swing.JButton;

/**
 * contains a window that displays all the PODs and allows for changing the settings
 * @author tamara
 */
public class PodFrame extends JFrame {

    private int numberOfPods;
    private PodPanel[] panels;
    private Settings settings;
    private Statement stmt;
    private Statement stmt2, stmt3, stmt4;
    private String prefix;
    private ResultSet pods;
    private ResultSet population;
    private Map<String, String> tables;
    private double[] booths;

    public PodFrame(String prefix, Connection c, Map<String, String> tables) {
        //super("Numerical Analysis of PODS");
        super("Numerical Analysis: " + prefix);
        this.settings = new Settings();
        this.tables = tables;
        this.prefix = prefix;
        try {
            stmt = (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt2 = (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt3 = (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt4 = (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            System.err.println("PODAnalyzer: Connection problem");
        }
        this.numberOfPods = this.getNumberOfPods(stmt);
        this.getPopulationOfCatchmentAreas(stmt2);
        System.out.println("number of pods = " + this.numberOfPods);
        init();
    }

    private void getPopulationOfCatchmentAreas(Statement stmt) {
//        String query =
//                "SELECT b2p.pod, sum(p.population) as population from " + prefix
//                + "_block_to_pods b2p, " + tables.get("population_table") + " p WHERE b2p.block = p.block_id GROUP BY b2p.pod ORDER BY b2p.pod;";
//        String query = "SELECT b2p.pod, sum(p.p0010001) as population from " + prefix + DefaultConstants.B2P_SUFFIX + " b2p, " + tables.get(DefaultConstants.POPULATION_TABLE) + " p WHERE b2p.block = p.logrecno GROUP BY b2p.pod ORDER BY b2p.pod;";
        String query = "SELECT b2p.pod, sum(p.p0010001) as population from " + prefix + DefaultConstants.B2P_SUFFIX + " b2p, " + prefix + DefaultConstants.POPULATION_SUFFIX + " p WHERE b2p.block = p.logrecno and b2p.pod > 0 GROUP BY b2p.pod ORDER BY b2p.pod;";
        System.out.println(query);
        try {
            population = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(PodFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    private int getNumberOfPods(Statement stmt) {
        int num = 0;
//        String query =
//                "SELECT * FROM " + prefix
//                + "_pods WHERE (type = 'public' OR type = 'Public') "
//                + "AND (onoff = 'true' OR onoff = 't') ORDER BY id";
        String query =
                "SELECT * FROM " + prefix
                + DefaultConstants.POD_SUFFIX + " WHERE (type = 'true' OR type = 'Public') "
                + "AND (status = 'true' OR status = 't') ORDER BY id";
        System.out.println(query);
        try {
            pods = stmt.executeQuery(query);
            pods.last();
            num = pods.getRow();
            pods.beforeFirst();
        } catch (SQLException ex) {
            Logger.getLogger(PodFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("PODAnalyzer: Error retrieving number of PODs");
        }
        return num;
    }

    private void init() {

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(350, 650));
        this.setJMenuBar(new PodMenu(new MenuListener(this)));
        this.setResizable(false);
        Container content = this.getContentPane();

        content.setLayout(new BorderLayout());

        JScrollPane podScroller = new JScrollPane();
        podScroller.setAutoscrolls(true);
        JPanel podPanel = new JPanel(new GridLayout(0, 1));
        podScroller.getViewport().add(podPanel);

        content.add(podScroller, BorderLayout.CENTER);

        // find out the number of actual pods
        for (int i = 0; i < numberOfPods; i++) {
        }

        panels = new PodPanel[numberOfPods];

        SummarizePodStatsFromDB sps = new SummarizePodStatsFromDB(
                this.numberOfPods);
        int[] ids = sps.getIds(pods);
        for (int i = 0; i < numberOfPods; i++) {
            panels[i] = new PodPanel(ids[i], "POD " + ids[i], this);
            podPanel.add(panels[i], BorderLayout.CENTER);
        }
        JButton updateButton = new JButton("Update Booths to Scenario");
        updateButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onUpdateButtonPress(evt);
            }
        });
        JButton boothAssignmentButton = new JButton("Reassign Booths Proportionally");
        boothAssignmentButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onBoothAssignmentButtonPress(evt);
            }
        });

        podPanel.add(updateButton);
        podPanel.add(boothAssignmentButton);
        this.fill();

        podScroller.setVisible(true);
        podPanel.setVisible(true);
        content.setVisible(true);

        // frame.pack();
        this.setVisible(true);


    }

    public PodPanel[] getPanels() {
        return panels;
    }

    public void fill() {
        SummarizePodStatsFromDB sps = new SummarizePodStatsFromDB(
                this.numberOfPods);
        booths = sps.getBooths(this.pods);
        String[] name = sps.getNames(this.pods);
        int[] population = sps.summarizePopulation(this.population);
        for (int i = 0; i < numberOfPods; i++) {
            panels[i].fill(booths[i], population[i], name[i]);
        }
    }

    public int[] getPopulation() {
        int[] population = new int[numberOfPods];
        for (int i = 0; i < numberOfPods; i++) {
            population[i] = panels[i].getPopulation();
        }
        return population;
    }

    public void saveValues(File f) {
        BufferedWriter output;
        if (!(f.getName().toLowerCase()).endsWith(".pdat")) {
            f = new File(f.getPath() + ".pdat");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot create file", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        try {
            output = new BufferedWriter(new FileWriter(
                    f, false));
            for (int i = 0; i < numberOfPods; i++) {
                output.write(panels[i].saveValues());
            }
            output.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot save data", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void readValues(File f) {
        try {
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                int id = s.nextInt();
                int population = s.nextInt();
                double booths = s.nextDouble();
//                this.panels[id - 1].readInValues(id, population, booths);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot read file", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public Settings getSettings() {
        return this.settings;

    }
    /*
     * st_distancew_sphere function used for distance calculation.. distances are acceptable but not perfect
     */

    public double[] getDistanceRange() {
        ResultSet distRange = null;
        double[] dist = new double[2];
        String query = "select max(st_distance_sphere)/1000 as maximum, min(st_distance_sphere)/1000 as minimum from (select st_distance_sphere(cents.centroid, pods.location) from " + prefix + DefaultConstants.BLOCK_SUFFIX + " cb, " + prefix + DefaultConstants.B2P_SUFFIX + " bp, "
                + prefix + DefaultConstants.CENTROID_SUFFIX + " cents, "
                + prefix + DefaultConstants.POD_SUFFIX + " pods,"
                + prefix + DefaultConstants.POPULATION_SUFFIX + " pop where cb.logrecno = bp.block and cents.logrecno = cb.logrecno and pods.id = bp.pod and cb.logrecno = pop.logrecno) as st_distance_sphere;";
        System.out.println(query);
        try {
            distRange = stmt.executeQuery(query);
            while (distRange.next()) {
                dist[0] = distRange.getDouble("minimum");
                dist[1] = distRange.getDouble("maximum");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PodFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }


        return dist;

    }

    public double[] getDistanceRange(int podNum) {
        ResultSet distRange = null;
        double[] dist = new double[2];
        String query = "select max(st_distance_sphere)/1000 as maximum, min(st_distance_sphere)/1000 as minimum from (select st_distance_sphere(cents.centroid, pods.location) from " + prefix + DefaultConstants.BLOCK_SUFFIX + " cb, " + prefix + DefaultConstants.B2P_SUFFIX + " bp, "
                + prefix + DefaultConstants.CENTROID_SUFFIX + " cents, "
                + prefix + DefaultConstants.POD_SUFFIX + " pods,"
                + prefix + DefaultConstants.POPULATION_SUFFIX + " pop where cb.logrecno = bp.block and cents.logrecno = cb.logrecno and pods.id = bp.pod and cb.logrecno = pop.logrecno and bp.pod = " + podNum + " ) as st_distance_sphere;";
        System.out.println(query);
        try {
            distRange = stmt.executeQuery(query);
            while (distRange.next()) {
                dist[0] = distRange.getDouble("minimum");
                dist[1] = distRange.getDouble("maximum");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PodFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }


        return dist;

    }

    /*
     * st_distancew_sphere function used for distance calculation.. distances are acceptable but not perfect
     */
    public ResultSet getPopVsDistResultSet() {
        ResultSet results = null;
        String query = "select cb.logrecno as id,  bp.pod as podnum , pop.p0010001 as population, st_distance_sphere(cents.centroid, pods.location)/1000 as distance from " + prefix + DefaultConstants.BLOCK_SUFFIX + " cb, "
                + prefix + DefaultConstants.B2P_SUFFIX + " bp, "
                + prefix + DefaultConstants.CENTROID_SUFFIX + " cents, "
                + prefix + DefaultConstants.POD_SUFFIX + " pods, "
                + prefix + DefaultConstants.POPULATION_SUFFIX + " pop where cb.logrecno = bp.block and cents.logrecno = cb.logrecno and pods.id = bp.pod and cb.logrecno = pop.logrecno order by podnum, distance;";
        System.out.println(query);

        try {
            results = stmt.executeQuery(query);

        } catch (SQLException ex) {
            Logger.getLogger(PodFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

        return results;
    }

    public PopVsDistResults getPopVsDistResults() {
        ResultSet resultSet1 = getPopVsDistResultSet();
        int size = 0;
        try {
            while (resultSet1.next()) {
                size++;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        PopVsDistResults results = new PopVsDistResults(size);

        ResultSet resultSet2 = getPopVsDistResultSet();
        int i = 0;
        try {
            while (resultSet2.next()) {
                //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
                results.dist[i] = resultSet2.getDouble("distance");
                results.numPop[i] = resultSet2.getInt("population");
                i++;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public ResultSet getPopVsDistForPod(int podNum) {
        ResultSet results = null;
        String query = "select cb.logrecno as id,  bp.pod as podnum , pop.p0010001 as population, st_distance_sphere(cents.centroid, pods.location)/1000 as distance from " + prefix + DefaultConstants.BLOCK_SUFFIX + " cb, "
                + prefix + DefaultConstants.B2P_SUFFIX + " bp, "
                + prefix + DefaultConstants.CENTROID_SUFFIX + " cents, "
                + prefix + DefaultConstants.POD_SUFFIX + " pods, "
                + prefix + DefaultConstants.POPULATION_SUFFIX + " pop where cb.logrecno = bp.block and cents.logrecno = cb.logrecno and pods.id = bp.pod and cb.logrecno = pop.logrecno and bp.pod = " + podNum + " order by distance;";
        System.out.println(query);

        try {
            results = stmt2.executeQuery(query);

        } catch (SQLException ex) {
            Logger.getLogger(PodFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        return results;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void onUpdateButtonPress(ActionEvent e) {

        //update guestone.workingcpy_sdemoden25vor_pods set numbooths = 15 where id = 1;

        //            update guestone.workingcpy_sdemoden25vor_pods
//set numbooths = CASE id 
//when 1  then 25
//when 2 then 26
//when 3 then 27
//end 
//where id in (1, 2, 3);
        int [] podIds = new int[panels.length];
        for(int i = 0; i< panels.length; i++) {
            podIds[i] = panels[i].getId();
        }

        String query = "update " + prefix + DefaultConstants.POD_SUFFIX + " set numbooths =  CASE id ";
        String whenBlock = "";
        String idSeq = "(";
        int k = 0;
        for (k = 1; k < numberOfPods; k++) {
            idSeq = idSeq.concat(Integer.toString(podIds[k]) + ", ");
        }
        idSeq = idSeq.concat(Integer.toString(k) + ");");

        for (int j = 0; j < numberOfPods; j++) {
            //int index = j + 1;
            int index = podIds[j];
            whenBlock = whenBlock.concat("when " + index + " then " + panels[j].getBooths() + " ");
        }
        whenBlock = whenBlock.concat("end");
        query = query.concat(whenBlock + " where id in " + idSeq);
        System.out.println(query);
        //System.out.println("Pod " + i + ": " + panels[i].getBooths() + "\n");

        try {
            stmt.executeUpdate(query);

        } catch (SQLException ex) {
            Logger.getLogger(PodFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

        JOptionPane.showMessageDialog(this, "Update complete. Use the Refresh button in POD Editor to refresh the POD list.");

    }

    public void onBoothAssignmentButtonPress(ActionEvent e) {
        //get a list of all pods and their assigned populations
        //sort the list in descending order of populations served
        //find the total population of the region; total num of available booths
        //Assign booths to pods in descending order of populations served proportional to populations served
        ResultSet catchmentPop = null;
        ResultSet netPop = null;

        String query = "SELECT b2p.pod as podid, sum(p.p0010001) as population from " + prefix + DefaultConstants.B2P_SUFFIX + " b2p, " + prefix + DefaultConstants.POPULATION_SUFFIX + " p WHERE b2p.block = p.logrecno and b2p.pod > 0 GROUP BY b2p.pod ORDER BY podid;";
        System.out.println(query);
        String query2 = "SELECT SUM(p0010001) from " + prefix + DefaultConstants.POPULATION_SUFFIX + ";";

        try {
            catchmentPop = stmt3.executeQuery(query);



            int[] populationCount = new int[numberOfPods];
            int[] podIds = new int[numberOfPods];
            for (int i = 0; i < populationCount.length; i++) {
                populationCount[i] = 0;
                podIds[i] = 0;

            }

            catchmentPop.beforeFirst();
            int id = 0;
            while (catchmentPop.next()) {
                populationCount[id] = catchmentPop.getInt("population");
                podIds[id] = catchmentPop.getInt("podid");
                id++;
            }

            for (int i = 0; i < populationCount.length; i++) {
                System.out.println("popcount, podids: " + populationCount[i] + " " + podIds[i] + "\n");
            }

            netPop = stmt4.executeQuery(query2);

            long netPopulation = 0;
            while (netPop.next()) {
                netPopulation = netPop.getLong(1);
            }

            double totalBooths = 0;
            for (int i = 0; i < booths.length; i++) {
                totalBooths += booths[i];
            }

            double[] boothAssignment = new double[numberOfPods];
            System.out.println("panels length: " + panels.length + " podIds length: " + podIds.length + "netPop: " + netPopulation + "booths length: " + booths.length + "\n");
            DecimalFormat df = new DecimalFormat(".#");
            for (int i = 0; i < populationCount.length; i++) {
                boothAssignment[i] = totalBooths * populationCount[i] / netPopulation;
                //panels[podIds[i] - 1].fillBooths(Double.valueOf(df.format((boothAssignment[i]))));
                panels[i].fillBooths(Double.valueOf(df.format((boothAssignment[i]))));
            }


        } catch (SQLException ex) {
            Logger.getLogger(SummarizePodStatsFromDB.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("PODAnalyzer: Error retrieving population sizes from DB");
        }





    }

    public class PopVsDistResults {

        double[] dist;
        int[] numPop;

        public PopVsDistResults(int size) {
            dist = new double[size];
            numPop = new int[size];
        }
    }
}
