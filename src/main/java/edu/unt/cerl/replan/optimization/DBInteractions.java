/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private String connString;

    public DBInteractions(Map m, Map db, String connString) {
        this.m = m;
        this.db = db;
        this.connString = connString;
        this.init();
    }

    private void init() {
        try {

            Class.forName("org.postgresql.Driver");
            //connString =
            //        "jdbc:postgresql://" + m.get("host") + ":" + m.get("port")
            //        + "/" + m.get("database");
            jdbc_properties = new Properties();
//            jdbc_properties.put("user", db.get("user"));
//            jdbc_properties.put("password", db.get("passwd"));
//            jdbc_properties.put("role", db.get("role"));

        } catch (ClassNotFoundException ex) {
            System.err.println("Postgre Driver class not found");
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
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

    public List<POD> getPodsAsArray() {

        List<POD> result = new LinkedList();
        try {
            ResultSet rs = getPods();
            rs.last();
            int length = rs.getRow();
            rs.beforeFirst();
            for (int i = 0; i < length; i++) {
                rs.next();
                result.add(new POD(rs.getInt("id"),
                        new Point(rs.getString("location"))));
                System.out.println(rs.getInt("id") + " \t "
                        + rs.getString("location"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return result;

    }

    public ResultSet getPods() {
        ResultSet rs = null;
        try {
            if (c == null) {
                c = REPLAN.getController().getConnection();
            }

            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT id, AsText(location) AS location FROM "
                    + m.get(Tables.PODS) + ";";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public POD[] getPODIdLocationArray(ResultSet rs,
            Map<Integer, Integer> podIdMap) {

        POD result[] = null;
        try {
            rs.last();
            int length = rs.getRow();
            rs.beforeFirst();
            result = new POD[length];
            for (int i = 0; i < length; i++) {
                rs.next();
                result[podIdMap.get(rs.getInt("id"))] =
                        new POD(rs.getInt("id"),
                        new Point(rs.getString("location")));
                System.out.println(i + "\t" + rs.getInt("id") + "\t"
                        + rs.getString("location"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return result;

    }

    public POD[] getPODInfoArray(ResultSet rs,
            Map<Integer, Integer> podIdMap) {

        POD result[] = null;
        try {
            rs.last();
            int length = rs.getRow();
            rs.beforeFirst();
            result = new POD[length];
            for (int i = 0; i < length; i++) {
                rs.next();
                result[podIdMap.get(rs.getInt("id"))] =
                        new POD(rs.getInt("id"), rs.getInt("numbooths"),
                        new Point(rs.getString("location")));
//                System.out.println( i + "\t" + rs.getInt("id") + "\t" +
//                        rs.getString( "location"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return result;

    }

    public Map<Integer, Integer> getPODIndexMap(ResultSet rs) {
        Map<Integer, Integer> result = new TreeMap<Integer, Integer>();
        try {
            rs.last();
            int length = rs.getRow();
            rs.beforeFirst();
            for (int i = 0; i < length; i++) {
                rs.next();
                result.put(rs.getInt("id"), i);
//                System.out.println(rs.getInt("id") + " \t " + i );
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return result;

    }

    public ResultSet getPods(String tableName) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT id, numbooths, AsText(location) AS location, numbooths FROM "
                    + tableName + ";";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public int[] getPodIds(ResultSet rs, Map<Integer, Integer> podIdMap) {
        int result[] = null;
        try {
            rs.last();
            int length = rs.getRow();
            result = new int[length];
            rs.beforeFirst();
            for (int i = 0; i < length; i++) {
                rs.next();
                result[podIdMap.get(rs.getInt("id"))] = rs.getInt("id");
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public int[] getPODCensusBlock(String blockTableName,
            String podTableName,
            Map<String, Integer> cbIdHash,
            Map<Integer, Integer> podIdMap) {
        int result[] = new int[podIdMap.size()];

        //ST_Covers(geometry geomA, geometry geomB)
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT b1.logrecno AS cb, b2.id AS pod FROM "
                    + UserState.userId + "." + blockTableName + " b1, "
                    + podTableName + " b2 "
                    + "WHERE ST_Covers( b1.the_geom, b2.location );";
            System.out.println(query);
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                result[podIdMap.get(rs.getInt("pod"))] =
                        cbIdHash.get(rs.getString("cb"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }


        return result;
    }

    public TreeSet<Integer>[] getCBAdjacencyAsTreeSetArray(String tableName,
            int censusBlockCount,
            Map<String, Integer> cbIdHash) {
        int s = 0, t = 0;
        ResultSet rs = null;
        TreeSet[] adj = new TreeSet[censusBlockCount];

        for (int i = 0; i < censusBlockCount; i++) {
            adj[i] = new TreeSet();
        }

        rs = getCensusBlockAdjacency(tableName);

        try {
            while (rs.next()) {
                s = cbIdHash.get(rs.getString("s"));
                t = cbIdHash.get(rs.getString("t"));
                adj[s].add(t);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return adj;
    }

    public TreeSet<CensusBlockDistance>[] getCBAdjDistAsTreeSetArray(
            String tableName,
            int censusBlockCount,
            Map<String, Integer> cbIdHash) {
        double d = -1.0;
        int s = -1, t = -1;
        ResultSet rs = null;
        TreeSet<CensusBlockDistance>[] adj = new TreeSet[censusBlockCount];

        Comparator<CensusBlockDistance> comparator =
                new CensusBlockDistanceComparator();

        for (int i = 0; i < censusBlockCount; i++) {
            adj[i] = new TreeSet(comparator);
        }

        rs = getCensusBlockAdjDist(tableName);

        try {
            while (rs.next()) {
                String ss = rs.getString("s");
                s = cbIdHash.get(ss);
                String st = rs.getString("t");
                t = cbIdHash.get(st);
                d = rs.getDouble("d");
//                System.out.println( "getCBAdjDistAsTreeSetArray adding\ts\t" + s
//                        + "\tss\t" + ss + "\tt\t" + t + "\tst\t" + st
//                        + "\td\t" + d );
                adj[s].add(new CensusBlockDistance(t, -1, d));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return adj;
    }

    public ResultSet getCensusBlockAdjacency(String tableName) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT b1.logrecno As s,"
                    + " b2.logrecno As t FROM "
                    + UserState.userId + "." + tableName + " b1, "
                    + UserState.userId + "." + tableName
                    + " b2 WHERE ST_Touches(b1.the_geom, b2.the_geom);";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public ResultSet getCensusBlockAdjDist(String tableName) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT b1.logrecno As s,"
                    + " b2.logrecno As t, ST_Distance( ST_Centroid(b1.the_geom),"
                    + " ST_Centroid(b2.the_geom) ) AS d FROM "
                    + UserState.userId + "." + tableName + " b1, "
                    + UserState.userId + "." + tableName
                    + " b2 WHERE ST_Touches(b1.the_geom, b2.the_geom);";

            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public ResultSet getPODcensusBlockDist(String pods,
            String population, String centroids) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String query = "SELECT b1.id As s,"
                    + " b2.logrecno As t,"
                    + " b2.p0010001 As p,"
                    + " ST_Distance( b1.location, b3.centroid ) AS d FROM "
                    + pods + " b1, "
                    + UserState.userId + "." + population + " b2, "
                    + UserState.userId + "." + centroids + " b3 "
                    + "WHERE "
                    + "b2.logrecno = b3.logrecno;";

            System.out.println(query);
            rs = stmt.executeQuery(query);

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public ResultSet getPODcensusBlockShortestDist(String pods,
            String population, String censusBlocks) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String query = "SELECT b1.id As s,"
                    + " b2.logrecno As t,"
                    + " b2.p0010001 As p,"
                    + " ST_Distance( b1.location,"
                    + "     ST_ClosestPoint( b3.the_geom, b1.location ) ) AS d"
                    + " FROM " + pods + " b1, "
                    + UserState.userId + "." + population + " b2, "
                    + UserState.userId + "." + censusBlocks + " b3 "
                    + "WHERE "
                    + "b2.logrecno = b3.logrecno;";

            System.out.println(query);
            rs = stmt.executeQuery(query);

        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public CensusBlockDistance[][] getPODcensusBlockDistArray(
            int podCount, ResultSet rs,
            Map<String, Integer> cbIdHash) {
        CensusBlockDistance result[][] =
                new CensusBlockDistance[podCount][cbIdHash.size()];

        try {
            while (rs.next()) {
                int s = rs.getInt("s") - 1;
                String st = rs.getString("t");
                int t = cbIdHash.get(st);
                int p = rs.getInt("p");
                double d = rs.getDouble("d");
                result[s][t] = new CensusBlockDistance(t, p, d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return result;
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
            System.out.println(query);
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
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return rs;
    }

    public Map<String, Integer> getMapOfCensusBlockIds(
            ResultSet rs) {

        Map<String, Integer> result =
                new HashMap(getCensusBlockCount(rs), (float) 1.0);
        int i = 0;

        try {
            rs.beforeFirst();
            while (rs.next()) {
                result.put(rs.getString("id"), new Integer(i++));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return result;
    }

    public Map<String, Integer> getMapOfCensusBlockIds(
            String tableName,
            int censusBlockCount) {
        Map<String, Integer> result = new HashMap(censusBlockCount, (float) 1.0);
        ResultSet rs = null;
        int i = 0;

        try {
            rs = getCensusBlockIds(tableName);

            while (rs.next()) {
                result.put(new String(rs.getString("id")), new Integer(i++));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return result;
    }

    public String[] getCensusBlockIdsAsArray(
            ResultSet rs,
            int censusBlockCount,
            Map<String, Integer> cbIdHash) {
        String result[] = new String[censusBlockCount];
        int i = 0;

        try {
            rs.beforeFirst();
            while (rs.next()) {
                String id = rs.getString("id");
                int index = cbIdHash.get(id);
//                System.out.println( "getCensusBlockIdsAsArray\tindex\t" + index
//                        + "\tid\t" + id );
                result[index] = id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return result;
    }

    public String[] getCensusBlockIdsAsArray(
            String tableName,
            int censusBlockCount,
            Map<String, Integer> cbIdHash) {
        String result[] = new String[censusBlockCount];
        ResultSet rs = null;
        int i = 0;

        try {
            rs = getCensusBlockIds(tableName);

            while (rs.next()) {
                result[cbIdHash.get(rs.getString("id"))] = rs.getString("id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return result;
    }

    public int getCensusBlockCount(ResultSet rs) {
        int result = -1;
        try {
            rs.last();
            result = rs.getRow();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return result;
    }

    public int getCensusBlockCount(String tableName) {
        int result = -1;
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT b.logrecno As id FROM "
                    + UserState.userId + "." + tableName + " b;";
            System.out.println(query);
            rs = stmt.executeQuery(query);
            rs.last();
            result = rs.getRow();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return result;
    }

    public ResultSet getCensusBlockIds(String tableName) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT b.logrecno As id FROM "
                    + UserState.userId + "." + tableName + " b;";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return rs;
    }

    public Point[] getCentroidsAsArray(Map<String, Integer> cbIdHash) {
        Point[] result = null;
        try {
            ResultSet rs = getCentroids(cbIdHash);
            rs.last();
            result = new Point[rs.getRow()];
            rs.beforeFirst();
            for (int i = 0; i < result.length; i++) {
                rs.next();
                result[cbIdHash.get(rs.getString("id"))] =
                        new Point(rs.getString("centroid"));
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
            String query = "SELECT logrecno, AsText(centroid) AS centroid FROM "
                    + UserState.userId + "." + m.get(Tables.CENTROIDS) + ";";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public ResultSet getCentroids(Map<String, Integer> cbIdHash) {
        ResultSet rs = null;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT b.logrecno AS id, AsText( ST_Centroid(b.the_geom) ) AS centroid FROM "
                    + UserState.userId + "." + m.get(Tables.BLOCKS) + " b;";
            System.out.println(query);
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }

        return rs;
    }

    public int getTotalPopulationSize() {
        ResultSet rs = null;
        int population = -1;
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT SUM(p0010001) As pop FROM "
                    + UserState.userId + "." + m.get(Tables.POPULATION);
            rs = stmt.executeQuery(query);
            rs.next();
            population = rs.getInt("pop");
            System.out.println("Total population of region = " + population);
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return population;
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

    public Map<Integer, Integer> getPopulationAsMap() {
        Map<Integer, Integer> result = new HashMap();
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM "
                    + UserState.userId + "." + m.get(Tables.POPULATION) + ";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.put(new Integer(rs.getInt("logrecno")),
                        new Integer(rs.getInt("p0010001")));
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return result;
    }

    public int[] getPopulationAsArray(String tableName, Map<String, Integer> cbIdHash) {
        int result[] = new int[cbIdHash.size()];

        for (int i = 0; i < cbIdHash.size(); i++) {
            result[i] = -1;
        }

        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM "
                    + UserState.userId + "." + tableName + ";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result[cbIdHash.get(rs.getString("logrecno"))] =
                        rs.getInt("p0010001");
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
        return result;
    }

    public Map<Integer, Integer> populationDistribution(Map<String, String> m,
            Map<Integer, Integer> pop, String values) {
        Map<Integer, Integer> distro = new HashMap();
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String centroidTable = UserState.userId + "." + m.get(Tables.CENTROIDS);
            System.out.println("centroidTable = " + centroidTable);
            String podTable = m.get(Tables.PODS);
            System.out.println("podTable = " + podTable);
            String query =
                    "SELECT b2.logrecno as block, p2.id as pod FROM "
                    + "(SELECT DISTINCT b.logrecno as logrecno, MIN(distance(b.centroid,p.location)) AS dist "
                    + "FROM " + centroidTable + " AS b, " + podTable + " AS p "
                    + " WHERE p.type ='true' AND status = 'true'"
                    + "AND p.id IN " + values
                    + " GROUP BY b.logrecno ) AS mins, " + podTable + " AS p2, "
                    + centroidTable + " as b2 " + "WHERE b2.logrecno = mins.logrecno "
                    + " AND mins.dist = distance(b2.centroid,p2.location);";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Integer block = rs.getInt("block");
                Integer pod = rs.getInt("pod");
                if (distro.containsKey(pod)) {
                    distro.put(pod, distro.get(pod) + pop.get(block));
                } else {
                    distro.put(pod, pop.get(block));
                }
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return distro;
    }

    public Map<Integer, ArrayList<Integer>> neighboringCatchments(Map<String, String> m,
            Map<Integer, Integer> pop, String values, ScenarioState s) {
        Map<Integer, ArrayList<Integer>> touches = new HashMap();
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String centroidTable = UserState.userId + "." + m.get(Tables.CENTROIDS);
            System.out.println("centroidTable = " + centroidTable);
            String podTable = m.get(Tables.PODS);
            System.out.println("podTable = " + podTable);

            String b2pQuery =
                    "SELECT b2.logrecno as block, p2.id as pod FROM "
                    + "(SELECT DISTINCT b.logrecno as logrecno, MIN(distance(b.centroid,p.location)) AS dist "
                    + "FROM " + centroidTable + " AS b, " + podTable + " AS p "
                    + " WHERE p.type ='true' AND status = 'true'"
                    + "AND p.id IN " + values
                    + " GROUP BY b.logrecno ) AS mins, " + podTable + " AS p2, "
                    + centroidTable + " as b2 " + "WHERE b2.logrecno = mins.logrecno "
                    + " AND mins.dist = distance(b2.centroid,p2.location)";

            String catchsQuery = "SELECT b2p.pod AS id, ST_UNION(cblocks.the_geom) AS the_geom"
                    + " FROM (" + b2pQuery+") AS b2p,"
                    + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS cblocks"
                    + " WHERE b2p.block=cblocks.logrecno "
                    + " GROUP BY b2p.pod "
                    + " ORDER BY b2p.pod";

            String query  = "WITH temp AS ("+catchsQuery+") SELECT pods.id as pod, neighbors.id as neighbors FROM temp as pods, temp as neighbors WHERE ST_TOUCHES(pods.the_geom, neighbors.the_geom);";


            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Integer neighbor = rs.getInt("neighbors");
                Integer pod = rs.getInt("pod");
                if (touches.containsKey(pod)) {

                   ArrayList<Integer> neighArr = touches.get(pod);
                   neighArr.add(neighbor);
                    touches.put(pod, neighArr);
                } else {
                    ArrayList<Integer> neighArr = new ArrayList<Integer>();
                    neighArr.add(neighbor);
                    touches.put(pod, neighArr);
                }
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return touches;
    }

    void turnPODsOff(String values) {
        try {
            Statement stmt = c.createStatement();
            String query = "UPDATE "
                    + m.get(Tables.PODS)
                    + " SET status = 'false' WHERE id NOT IN "
                    + values + ";";
            System.out.println(query);
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
}
