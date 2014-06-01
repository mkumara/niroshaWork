/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.tools;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author saratchandraindrakanti
 */
public class DbTools {

    private ScenarioPanel scenario;

    private static final DbTools dbtools_instance = new DbTools();

    public static DbTools getInstance() {
        return dbtools_instance;
    }

    public void setSceanrio(ScenarioPanel scenario) {
        this.scenario = scenario;
    }

    public ScenarioPanel getScenario() {
        return this.scenario;
    }

    public int findEnclosingPod(double latitude, double longitude) {
        int pod = 0;

        try {
            //create a postgis point using the lat, long value
            //create a postgis point using the lat, long value
            //return pod corresponding to catchment area that contains this point
            //Access table catchment areas

            Connection c = REPLAN.getController().getConnection();
            String query = "SELECT pods.id FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " pods, "
                    + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX
                    + " cas WHERE st_contains(cas.the_geom, SETSRID(MAKEPOINT( " + longitude + ", " + latitude + " ),4326)) and pods.id = cas.id;";
            ResultSet rs = c.createStatement().executeQuery(query);
            System.out.println("FIndEnClosingPod: " + query);
            if (rs.next()) {
                pod = rs.getInt("id");
            } else {
                System.out.println("No enclosing catchment found, returning -1");
                pod = -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pod;
    }

    public ResultSet getLanguageVulnByPod(String vulnName) {

        String query = null;
        ResultSet rs = null;
        Connection c = REPLAN.getController().getConnection();

        if (vulnName.compareTo("Spanish") == 0) {
            query = "SELECT b2p.pod as pod, sum(b16004_008e + B16004_009e + B16004_030e + B16004_031e + B16004_052e + B16004_053e) as vulnerable from " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p, tarrant_bg_acs_08_12_language p WHERE b2p.block = p.logrecno and b2p.pod > 0 GROUP BY b2p.pod ORDER BY b2p.pod;";
        } else if (vulnName.compareTo("Other Indo-European") == 0) {
            query = "SELECT b2p.pod as pod, sum(b16004_013e + B16004_014e + B16004_035e + B16004_036e + B16004_057e + B16004_058e) as vulnerable from " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p, tarrant_bg_acs_08_12_language p WHERE b2p.block = p.logrecno and b2p.pod > 0 GROUP BY b2p.pod ORDER BY b2p.pod;";
        } else if (vulnName.compareTo("Asian and Pacific Island") == 0) {
            query = "SELECT b2p.pod as pod, sum(b16004_018e + B16004_019e + B16004_040e + B16004_041e + B16004_062e + B16004_063e) as vulnerable from " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p, tarrant_bg_acs_08_12_language p WHERE b2p.block = p.logrecno and b2p.pod > 0 GROUP BY b2p.pod ORDER BY b2p.pod;";
        } else if (vulnName.compareTo("Other") == 0) {
        }

        System.out.println(query);
        try {
            rs = c.createStatement().executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public HashMap<Integer, Integer> getPodPopulationMap() {
        HashMap<Integer, Integer> popMap = new HashMap<Integer, Integer>();
        ResultSet rs = null;
        Connection c = REPLAN.getController().getConnection();
        String query = "SELECT b2p.pod as pod, sum(p.p0010001) as population from "
                + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX
                + " b2p, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.POPULATION_SUFFIX
                + " p WHERE b2p.block = p.logrecno and b2p.pod > 0 GROUP BY b2p.pod ORDER BY b2p.pod;";
        System.out.println("Catchment area pop query: " + query);

        try {
            rs = c.createStatement().executeQuery(query);
            while (rs.next()) {
                popMap.put(rs.getInt("pod"), rs.getInt("population"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbTools.class.getName()).log(Level.SEVERE, null, ex);
        }

        return popMap;
    }

    public void createVulnColorCodesTable(HashMap<Integer, Integer> podsByClass, int index) {
        //retrieve <pod, class> from hashmap,
        //create table on db : <pod, class, block_id, block_geom>
        //resultset = select block_id, block_geom, pod from b2p, blocks where b2p.block_id = blocks.block_id 
        //populate hashmap2<pod, block_details>
        //create table type2vulns<pod, class, block_id, block_geom>
        //insert into table values (pod, hashmap.get(pod), hashmap2.get(block_id), hashmap2.get(block_id))

        try {
            Connection c = REPLAN.getController().getConnection();

            //String query = "SELECT block, the_geom, pod FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p WHERE b2p.block = blocks.logrecno;";
            String createTempTable = "CREATE TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp (pod integer, class integer);";
            System.out.println(createTempTable);
            c.createStatement().executeUpdate(createTempTable);

            Iterator<Integer> it = podsByClass.keySet().iterator();
            int size = podsByClass.size();
            int counter = 0;
            String values = "";

            while (it.hasNext()) {
                int pod = it.next();
                if (counter < (size - 1)) {
                    values += "(" + pod + ", " + podsByClass.get(pod) + "), ";
                } else {
                    values += "(" + pod + ", " + podsByClass.get(pod) + ");";
                }
                counter++;
            }

            String insertToTemp = "INSERT INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp (pod, class) VALUES ";
            insertToTemp += values;
            System.out.println(insertToTemp);
            c.createStatement().executeUpdate(insertToTemp);

            if (index == 0) {
                String vulnClassTab = "SELECT block, the_geom, b2p.pod, class INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_VULN_CLASSES + " FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp podclasses WHERE b2p.block = blocks.logrecno AND podclasses.pod = b2p.pod;";
                System.out.println(vulnClassTab);
                c.createStatement().executeUpdate(vulnClassTab);
            } else {
                String addClassCol = "ALTER TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_VULN_CLASSES + " ADD COLUMN vulnerability" + index +" integer;";
                System.out.println(addClassCol);
                c.createStatement().executeUpdate(addClassCol);

                String tempClassTab = "CREATE TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podvulnclasses_temp (p integer, class integer);";
                System.out.println(tempClassTab);
                c.createStatement().executeUpdate(tempClassTab);

                String updatevulnClassTab = "UPDATE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_VULN_CLASSES + " SET vulnerability" + index + " = temp.class FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podvulnclasses_temp temp WHERE temp.p = pod;";
                System.out.println(updatevulnClassTab);
                c.createStatement().executeUpdate(updatevulnClassTab);

                String droptempClassTab = "DROP TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podvulnclasses_temp;";
                System.out.println(droptempClassTab);
                c.createStatement().executeUpdate(droptempClassTab);
            }

            String dropTemp = "DROP TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp;";
            System.out.println(dropTemp);
            c.createStatement().executeUpdate(dropTemp);

        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }

    }

    public void updateVulnColorCodesTable(HashMap<Integer, Integer> podsByClass) {
        try {
            Connection c = REPLAN.getController().getConnection();

            //String query = "SELECT block, the_geom, pod FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p WHERE b2p.block = blocks.logrecno;";
            String dropVulnClTab = "DROP TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_VULN_CLASSES + ";";
            System.out.println(dropVulnClTab);
            c.createStatement().executeUpdate(dropVulnClTab);

            String createTempTable = "CREATE TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp (pod integer, class integer);";
            System.out.println(createTempTable);
            c.createStatement().executeUpdate(createTempTable);

            Iterator<Integer> it = podsByClass.keySet().iterator();
            int size = podsByClass.size();
            int counter = 0;
            String values = "";

            while (it.hasNext()) {
                int pod = it.next();
                if (counter < (size - 1)) {
                    values += "(" + pod + ", " + podsByClass.get(pod) + "), ";
                } else {
                    values += "(" + pod + ", " + podsByClass.get(pod) + ");";
                }
                counter++;
            }

            String insertToTemp = "INSERT INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp (pod, class) VALUES ";
            insertToTemp += values;
            System.out.println(insertToTemp);
            c.createStatement().executeUpdate(insertToTemp);

            String vulnClassTab = "SELECT block, the_geom, b2p.pod, class INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_VULN_CLASSES
                    + " FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, "
                    + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p, "
                    + UserState.userId + "." + scenario.getState().getWorkingCopyName()
                    + "_podclasses_temp podclasses WHERE b2p.block = blocks.logrecno AND podclasses.pod = b2p.pod;";
            System.out.println(vulnClassTab);
            c.createStatement().executeUpdate(vulnClassTab);

            String dropTemp = "DROP TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp;";
            System.out.println(dropTemp);
            c.createStatement().executeUpdate(dropTemp);

        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }

    }

    public void createType2VulnTable(HashMap<Integer, Integer> podsByClass) {
        try {
            Connection c = REPLAN.getController().getConnection();

            //String query = "SELECT block, the_geom, pod FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p WHERE b2p.block = blocks.logrecno;";
            String createTempTable = "CREATE TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp (pod integer, class integer);";
            System.out.println(createTempTable);
            c.createStatement().executeUpdate(createTempTable);

            Iterator<Integer> it = podsByClass.keySet().iterator();
            int size = podsByClass.size();
            int counter = 0;
            String values = "";

            while (it.hasNext()) {
                int pod = it.next();
                if (counter < (size - 1)) {
                    values += "(" + pod + ", " + podsByClass.get(pod) + "), ";
                } else {
                    values += "(" + pod + ", " + podsByClass.get(pod) + ");";
                }
                counter++;
            }

            String insertToTemp = "INSERT INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podclasses_temp (pod, class) VALUES ";
            insertToTemp += values;
            System.out.println(insertToTemp);
            c.createStatement().executeUpdate(insertToTemp);

        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }

    }

    public void createAllocationTable(HashMap<Integer, Integer> resClassesbyPods) {
        try {
            Connection c = REPLAN.getController().getConnection();

            //String query = "SELECT block, the_geom, pod FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p WHERE b2p.block = blocks.logrecno;";
            String createTempTable = "CREATE TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_respodclasses_temp (pod integer, class integer);";
            System.out.println(createTempTable);
            c.createStatement().executeUpdate(createTempTable);

            Iterator<Integer> it = resClassesbyPods.keySet().iterator();
            int size = resClassesbyPods.size();
            int counter = 0;
            String values = "";

            while (it.hasNext()) {
                int pod = it.next();
                if (counter < (size - 1)) {
                    values += "(" + pod + ", " + resClassesbyPods.get(pod) + "), ";
                } else {
                    values += "(" + pod + ", " + resClassesbyPods.get(pod) + ");";
                }
                counter++;
            }

            String insertToTemp = "INSERT INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_respodclasses_temp (pod, class) VALUES ";
            insertToTemp += values;
            System.out.println(insertToTemp);
            c.createStatement().executeUpdate(insertToTemp);

        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }

    }

    public void createAllocColorCodesTable(HashMap<Integer, Integer> podsByClass, int index) {
        //retrieve <pod, class> from hashmap,
        //create table on db : <pod, class, block_id, block_geom>
        //resultset = select block_id, block_geom, pod from b2p, blocks where b2p.block_id = blocks.block_id 
        //populate hashmap2<pod, block_details>
        //create table type2vulns<pod, class, block_id, block_geom>
        //insert into table values (pod, hashmap.get(pod), hashmap2.get(block_id), hashmap2.get(block_id))

        try {
            Connection c = REPLAN.getController().getConnection();

            //String query = "SELECT block, the_geom, pod FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p WHERE b2p.block = blocks.logrecno;";
            String createTempTable = "CREATE TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_respodclasses_temp (pod integer, class integer);";
            System.out.println(createTempTable);
            c.createStatement().executeUpdate(createTempTable);

            Iterator<Integer> it = podsByClass.keySet().iterator();
            int size = podsByClass.size();
            int counter = 0;
            String values = "";

            while (it.hasNext()) {
                int pod = it.next();
                if (counter < (size - 1)) {
                    values += "(" + pod + ", " + podsByClass.get(pod) + "), ";
                } else {
                    values += "(" + pod + ", " + podsByClass.get(pod) + ");";
                }
                counter++;
            }

            String insertToTemp = "INSERT INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_respodclasses_temp (pod, class) VALUES ";
            insertToTemp += values;
            System.out.println(insertToTemp);
            c.createStatement().executeUpdate(insertToTemp);

            if(index ==  0) {
            String vulnClassTab = "SELECT block, the_geom, b2p.pod, class INTO " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_ALLOC_CLASSES + " FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " blocks, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p, " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_respodclasses_temp podclasses WHERE b2p.block = blocks.logrecno AND podclasses.pod = b2p.pod;";
            System.out.println(vulnClassTab);
            c.createStatement().executeUpdate(vulnClassTab);
            } else {
                String addClassCol = "ALTER TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_ALLOC_CLASSES + " ADD COLUMN alloc" + index +" integer;";
                System.out.println(addClassCol);
                c.createStatement().executeUpdate(addClassCol);

                String tempClassTab = "CREATE TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podallocclasses_temp (p integer, class integer);";
                System.out.println(tempClassTab);
                c.createStatement().executeUpdate(tempClassTab);

                String updatevulnClassTab = "UPDATE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_ALLOC_CLASSES + " SET alloc" + index + " = temp.class FROM " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podallocclasses_temp temp WHERE temp.p = pod;";
                System.out.println(updatevulnClassTab);
                c.createStatement().executeUpdate(updatevulnClassTab);

                String droptempClassTab = "DROP TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_podallocclasses_temp;";
                System.out.println(droptempClassTab);
                c.createStatement().executeUpdate(droptempClassTab);
            }

            String dropTemp = "DROP TABLE " + UserState.userId + "." + scenario.getState().getWorkingCopyName() + "_respodclasses_temp;";
            System.out.println(dropTemp);
            c.createStatement().executeUpdate(dropTemp);

        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }

    }

}
