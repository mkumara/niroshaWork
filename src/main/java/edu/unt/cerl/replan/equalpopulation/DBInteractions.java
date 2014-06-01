/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.equalpopulation;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.controller.DBQueries;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.REPLAN;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

;

import org.postgis.MultiPolygon;

import org.postgis.Point;

/**
 *
 * @author tamara
 */
public class DBInteractions {

    private Map<String, String> m;
    private Map<String, String> db;
    private Connection c;
    private Properties jdbc_properties;
    //private String connString;

    public DBInteractions(Map m, Map db) {
        this.m = m;
        this.db = db;
        //this.connString = connString;
        this.c = c = REPLAN.getController().getConnection();
//        this.init();
    }

//    private void init() {
//        try {
//
//            Class.forName("org.postgresql.Driver");
//            //connString =
//            //        "jdbc:postgresql://" + m.get("host") + ":" + m.get("port")
//            //        + "/" + m.get("database");
//            jdbc_properties = new Properties();
//            jdbc_properties.put("user", db.get("user"));
//            jdbc_properties.put("password", db.get("passwd"));
//            jdbc_properties.put("role", db.get("role"));
//
//        } catch (ClassNotFoundException ex) {
//            System.err.println("Postgre Driver class not found");
//            Logger.getLogger(DBInteractions.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void tempPrep(){
//        try {
//            Statement stmt = c.createStatement();
//            String query = "DROP TABLE census_blocks_tarrant_pods;";
//          //  stmt.executeUpdate(query);
//           query = "ALTER TABLE census_blocks DROP COLUMN pod;";
//            stmt.executeUpdate(query);
//            stmt.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(DBInteractions.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//    }

    public void prepTables(ScenarioState s) {
        try {
            Statement stmt = c.createStatement();
            String query = Queries.createBlock2PodTable(m.get(DefaultConstants.SCHEMA), s.getWorkingCopyName());
            System.out.println(query);
            stmt.executeUpdate(query);
            query = Queries.addPodColumnToB2P(m.get(DefaultConstants.SCHEMA), s.getWorkingCopyName());
            System.out.println(query);
            stmt.executeUpdate(query);
            query = Queries.fillBlocksIntoBlock2PodTable(m.get(DefaultConstants.SCHEMA), s.getWorkingCopyName(),
                    m.get(DefaultConstants.BLOCK_TABLE));
            System.out.println(query);
            boolean exists = DBQueries.tableExists( m.get(DefaultConstants.SCHEMA),
                    s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX, c);
            System.out.println( "prepTables " + m.get(DefaultConstants.SCHEMA) +
                    "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX
                    + " exists\t" + exists );
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public Connection establishNewConnection() {
        try {
            c = REPLAN.getController().getConnection();
            /*
             * Add the geometry types to the connection. Note that you
             * must cast the connection to the pgsql-specific connection
             * implementation before calling the addDataType() method.
             */
            ((org.postgresql.PGConnection) c).addDataType("geometry",
                    org.postgis.PGgeometry.class);
            ((org.postgresql.PGConnection) c).addDataType("box3d",
                    org.postgis.PGbox3d.class);

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return c;
    }

    public void closeConnection() {
        try {
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
    }

    public ResultSet getCensusBlocks() {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + m.get("block_table") + ";";
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return rs;
    }

    public ResultSet getCensusBlocks(int pod) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + m.get("block_table")
                    + " WHERE pod = " + pod + ";";
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return rs;
    }

    public void writePodsToDB(Map<Integer, CensusBlock> blockMap) {

        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + m.get("block_table") + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                CensusBlock currBlock = blockMap.get(
                        new Integer(rs.getInt("id")));
                //  System.out.println(currBlock + "   AND  pod = " + rs.getInt(
                //          "pod"));
                if (rs.getInt("pod") != currBlock.getPod()) {
                    rs.updateInt("pod", currBlock.getPod());
                    rs.updateRow();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
    }

    public void writePodsToDBFaster(Map<Integer, CensusBlock> blockMap, int val1,
            int val2, String prefix) {
        String values1 = "(";
        String values2 = "(";
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + ScenarioState.getAuthor() + "." +
                    prefix + DefaultConstants.B2P_SUFFIX;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int currInt = rs.getInt("block");
                int currPod = rs.getInt("pod");
                CensusBlock currBlock = blockMap.get(new Integer(currInt));

                if (currPod != currBlock.getPod()) {
                    if (currBlock.getPod() == val1) {
                        values1 = values1 + currBlock.getId() + ",";
                    }
                    if (currBlock.getPod() == val2) {
                        values2 = values2 + currBlock.getId() + ",";
                    }
                    //  rs.updateInt("pod", currBlock.getPod());
                    //  rs.updateRow();
                }
            }
            values1 = values1.substring(0, values1.length() - 1);
            values2 = values2.substring(0, values2.length() - 1);
            values1 = values1 + ")";
            values2 = values2 + ")";
            query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
                    + DefaultConstants.B2P_SUFFIX + " SET pod = "
                    + val1 + " WHERE block IN " + values1 + ";";
            //    System.out.println(query);
            stmt.executeUpdate(query);
            query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
                    + DefaultConstants.B2P_SUFFIX + " SET pod = "
                    + val2 + " WHERE block IN " + values2 + ";";
            //    System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
    }

    public void initPodsTo(int pod) {
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "ALTER TABLE " + m.get(DefaultConstants.B2P_TABLE) + DefaultConstants.B2P_SUFFIX
                    + " ADD PRIMARY KEY(block);";
         //   System.out.println(query);
        //    stmt.executeUpdate(query);
            query = "UPDATE " + m.get(DefaultConstants.B2P_TABLE) + DefaultConstants.B2P_SUFFIX
                    + " SET pod = " + pod + ";";
            System.out.println();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }


    }

    public Point[] getCentroidsAsArray() {
        Point[] result = null;
        try {
            ResultSet rs = getCentroids();
            rs.last();
            result = new Point[rs.getRow()];
            rs.beforeFirst();
            for (int i = 0; i < result.length; i++) {
                rs.next();
                result[i] = new Point(rs.getString("centroid"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return result;

    }

    public ResultSet getCentroids() {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT logrecno, AsText(centroid) AS centroid FROM " +
                    m.get(DefaultConstants.SCHEMA) + "." +
                    m.get(DefaultConstants.CENTROID_TABLE) + ";";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public ResultSet getPopulation() {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + m.get("block_table")
                    + "_population;";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return rs;
    }

    public Map getPopulationAsMap() {
        Map<Integer, Integer> result = new HashMap();
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + m.get(DefaultConstants.SCHEMA) +
                    "." + m.get(DefaultConstants.POPULATION_TABLE) + ";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.put(new Integer(rs.getInt("logrecno")),
                        new Integer(rs.getInt("p0010001")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return result;
    }

    public MultiPolygon joinCensusBlocks() {
        MultiPolygon join = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query =
                    "SELECT AsText(Multi(ST_Union(f.the_geom)))AS single_geom FROM "
                    + m.get(DefaultConstants.SCHEMA) + "."
                    + m.get(DefaultConstants.BLOCK_TABLE) + " as f;";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            join = new MultiPolygon(rs.getString("single_geom"));
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return join;
    }

    public MultiPolygon joinCensusBlocks(int pod, String prefix) {
        MultiPolygon join = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query =
                    "SELECT AsText(Multi(ST_Union(b.the_geom)))AS single_geom FROM "
                    + m.get(DefaultConstants.SCHEMA) + "." + prefix + DefaultConstants.B2P_SUFFIX
                    + " as b2p, " + m.get(DefaultConstants.SCHEMA) + "."+ m.get(DefaultConstants.BLOCK_TABLE)
                    + " b where b2p.pod = " + pod + " AND b2p.block = b.logrecno;";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            join = new MultiPolygon(rs.getString("single_geom"));
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return join;
    }

    void createAndWriteCatchmentTable(MultiPolygon[] podsArray, String prefix) {
        try {
            
            // Use default constants instead of hard coding _catchment"
            
            Statement stmt = c.createStatement();
            String tableNameWithoutSchema = prefix + "_catchment";
            String tableName = UserState.userId + "." + prefix + "_catchment";  
           String masterQuery="BEGIN WORK;\n";  
           String query =
                    Queries.createCatchmentTable(UserState.userId, prefix);
            //masterQuery = masterQuery + Queries.createCatchmentTable(UserState.userId, prefix) + "\n";
            //System.out.println(query);
           // stmt.executeUpdate(query);
            masterQuery += query + "\n";
            
            query =
                    "SELECT AddGeometryColumn('" + UserState.userId + "','"
                    + tableNameWithoutSchema
                    + "', 'the_geom', 4326, 'MULTIPOLYGON',2);";
            
            //System.out.println(query);
            //stmt.executeQuery(query);
            masterQuery += query + "\n";
            masterQuery += "LOCK TABLE " + tableName + ";\n";
            //masterQuery += "LOCK TABLE " + UserState.userId + "." + prefix + DefaultConstants.POD_SUFFIX + ";\n";
            //String masterQuery="BEGIN WORK;\n LOCK TABLE " + tableName + ";\n";
            /*
            query = "BEGIN WORK; " + 
                    "LOCK TABLE " + tableNameWithoutSchema + ";" + 
                    "SELECT AddGeometryColumn('" + UserState.userId + "','"
                    + tableNameWithoutSchema
                    + "', 'the_geom', 4326, 'MULTIPOLYGON',2);" +
                    "COMMIT WORK;";
            */
            //System.out.println(query);
            //stmt.executeQuery(query);
            for (int i = 0; i < podsArray.length; i++) {
                String geom = "ST_GeomFromText('" + podsArray[i] + "',4326)";
                query = "INSERT INTO " + tableName + " VALUES (" + (i + 1) + ","
                        + geom
                        + ");";
                //   System.out.println(query);
                //stmt.executeUpdate(query);
                masterQuery = masterQuery + query + "\n";
            }
            query = "UPDATE " + ScenarioState.getAuthor() + "." + prefix
                    + "_catchment SET the_geom = SetSRID(the_geom,4326);";
            //stmt.executeUpdate(query);
                masterQuery = masterQuery + query + "\n";
            query = "UPDATE geometry_columns SET srid=4326 WHERE f_table_name='"
                    + prefix + "_catchment';";
            //stmt.executeUpdate(query);
                masterQuery = masterQuery + query + "\n";
                masterQuery = masterQuery + "COMMIT WORK;";
                System.out.println(masterQuery);
                stmt.executeUpdate(masterQuery);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    void createAndInitPodTable(String prefix) {
        try {
            Statement stmt = c.createStatement();
            
            String masterQuery="BEGIN WORK;\n";  
            
            String query =
                    Queries.createPodTable(UserState.userId, prefix);
            System.out.println(query);
            
            masterQuery += query + "\n";
            
            //stmt.executeUpdate(query);
            query =
                    Queries.addColumnsToPodTable(UserState.userId, prefix);
            System.out.println(query);
            //stmt.executeUpdate(query);
            
            masterQuery += query + "\n";
            
            String podTable =
                    UserState.userId + "." + prefix + DefaultConstants.POD_SUFFIX;
            
            masterQuery += "LOCK TABLE " + podTable + ";\n";
            /*
            Statement stmt = c.createStatement();
            String query = "UPDATE " + tableName + " SET " + columnName + " = '"
                    + value + "';";
            stmt.executeUpdate(query);
            */
            
            query = "UPDATE " + podTable + " SET " + 
                        "name = '<unnamed>', " +
                        "address = 'NULL', " +
                        "city = '<unnamed>', " +
                        "zip = '0', " +
                        "status = 'true', " +
                        "comments = 'NULL', " +
                        "onoff = 'true', " +
                        "numbooths = '1', " +
                        "type = 'true';";
            
            masterQuery += query + "\n";
            masterQuery += "COMMIT WORK;";
            System.out.println(masterQuery);
            stmt.executeUpdate(masterQuery);
            
            /*
            DBQueries.setFieldTo(c, podTable, "name", "<unnamed>");
            DBQueries.setFieldTo(c, podTable, "address", "NULL");
            DBQueries.setFieldTo(c, podTable, "city", "<unnamed>");
            DBQueries.setFieldTo(c, podTable, "zip", "0");
            DBQueries.setFieldTo(c, podTable, "status", "true");
            DBQueries.setFieldTo(c, podTable, "comments", "NULL");
            DBQueries.setFieldTo(c, podTable, "onoff", "true");
            DBQueries.setFieldTo(c, podTable, "numbooths", 1);
            DBQueries.setFieldTo(c, podTable, "type", "true");
            */
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
}
