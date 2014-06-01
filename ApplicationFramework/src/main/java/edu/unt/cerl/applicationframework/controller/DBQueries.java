/*
 * This is a temporary class. All the queries will be replaced via DB functions
 */
package edu.unt.cerl.applicationframework.controller;

import com.vividsolutions.jts.geom.MultiPolygon;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgis.PGgeometry;
//import org.postgis.PGgeometry;

/**
 *
 * @author tamara
 */
public class DBQueries {

    /**
     * Sums up the values of a table column
     * @param table table name
     * @param column column name
     * @param c database connection
     * @return sum of values of column in table
     */
    public static int getSumOfField(String table, String column, Connection c) {
        int sum = 0;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT SUM(" + column + ") AS result FROM " + table
                    + ";";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            sum = rs.getInt("result");
        } catch (SQLException ex) {
            //TODO replace with generic logger
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        return sum;
    }

    /**
     * Returns a polygon containing the bounding box of a geometry
     * @param table table name
     * @param column column name
     * @param c database connection
     * @return
     */
    public static PGgeometry getBoundingBox(String table, String column,
            Connection c) {
        PGgeometry p = null;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT ST_Envelope(" + column + ") AS result FROM "
                    + table
                    + ";";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            p = (PGgeometry) rs.getObject("result");

        } catch (SQLException ex) {
            //TODO replace with generic logger
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        return p;
    }

    /**
     * Returns a polygon containing the bounding box of a geometry in meters
     * @param table table name
     * @param column column name
     * @param c database connection
     * @return
     */
    public static PGgeometry getBoundingBoxInMeters(String table, String column,
            Connection c) {
        PGgeometry p = null;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT ST_Transform(ST_Envelope(" + column
                    + "),2163) AS result FROM " + table + ";";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            p = (PGgeometry) rs.getObject("result");
        } catch (SQLException ex) {
            //      TODO replace with generic logger
            //      Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
            //             ex);
        }
        return p;
    }

    public static double getAreaInMeters(String table, String column,
            Connection c) {
        double area = 0;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT ST_Area(ST_Transform(" + column
                    + ",2163)) AS result FROM " + table + ";";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            area = rs.getDouble("result");
        } catch (SQLException ex) {
            //TODO replace with generic logger
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        return area;
    }

    public static double getArea(String table, String column, Connection c) {
        double area = 0;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT ST_Area(" + column
                    + ") AS result FROM " + table + ";";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            area = rs.getDouble("result");
        } catch (SQLException ex) {
            //TODO replace with generic logger
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        return area;
    }

    /**
     * Check if a table exists in the DB
     * @param tableName name of table
     * @param c connection
     * @return true or false
     * @throws SQLException
     */
    public static boolean tableExists(String tableName, Connection c) throws
            SQLException {
        boolean exists = false;
        Statement stmt = c.createStatement();
        ResultSet rs = c.getMetaData().getTables(null, null, tableName, null);
        if (rs.next()) {
            exists = true;
        }
        //c.close();
        return exists;
    }

    public static boolean tableExists(String schemaName, String tableName,
            Connection c) throws SQLException {
        boolean exists = false;
        Statement stmt = c.createStatement();
        ResultSet rs = c.getMetaData().getTables(null, schemaName, tableName,
                null);
        if (rs.next()) {
            exists = true;
        }
        //c.close();
        return exists;
    }

    /**
     * Check if a column exists in a table of the DB
     * @param tableName name of table
     * @param columnName name of column
     * @param c connection
     * @return true or false
     * @throws SQLException
     */
    public static boolean columnExists(String tableName, String columnName,
            Connection c) throws SQLException {
        boolean exists = false;
        ResultSet rs = c.getMetaData().getColumns(null, null, tableName, null);
        while (rs.next()) {
            String name = rs.getString("COLUMN_NAME");
            if (columnName.equals(name)) {
                exists = true;
                break;
            }
        }
        //c.close();
        return exists;
    }

    /**
     * Check if a column exists in a table of the DB
     * @param tableName name of table
     * @param columnName name of column
     * @param c connection
     * @return true or false
     * @throws SQLException
     */
    public static boolean columnExists(String tableName, String schemaName,
            String columnName, Connection c) throws SQLException {
        boolean exists = false;
        ResultSet rs = c.getMetaData().getColumns(null, schemaName, tableName,
                null);
        while (rs.next()) {
            String name = rs.getString("COLUMN_NAME");
            if (columnName.equals(name)) {
                exists = true;
                break;
            }
        }
        //c.close();
        return exists;
    }

    /**
     * Check if an entry exists in a DB table
     * @param tableName name of table
     * @param columnName name of column
     * @param entry entry string
     * @param c connection
     * @return true or false
     */
    public static boolean entryExists(String tableName, String columnName,
            String entry, Connection c) throws SQLException {
        boolean exists = false;
        Statement stmt = c.createStatement();
        String query = "SELECT " + columnName + " FROM " + tableName + ";";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String name = rs.getString(columnName);
            if (entry.equals(name)) {
                exists = true;
                break;
            }
        }
        //c.close();
        return exists;
    }

    public static boolean entryExists(String tableName, String columnName1,
            String entry1, String columnName2, String entry2, Connection c)
            throws SQLException {
        boolean exists = false;
        Statement stmt = c.createStatement();
        String query = "SELECT " + columnName1 + ", " + columnName2 + " FROM "
                + tableName + ";";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String col1 = rs.getString(columnName1);
            String col2 = rs.getString(columnName2);
            if (entry1.equals(col1) && entry2.equals(col2)) {
                exists = true;
                break;
            }
        }
        //c.close();
        return exists;
    }

    public static int tableSize(String tableName, Connection c) throws
            SQLException {

        int count = 0;
        Statement stmt = c.createStatement();
        ResultSet res = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
        while (res.next()) {
            count = res.getInt(1);
        }
        res.close();
        stmt.close();
        return count;

    }

    public static void setFieldTo(Connection c, String tableName,
            String columnName, String value) {
        try {
            Statement stmt = c.createStatement();
            String query = "UPDATE " + tableName + " SET " + columnName + " = '"
                    + value + "';";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            //  Logger.getLogger(DBTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setFieldTo(Connection c, String tableName,
            String columnName, int value) {
        try {
            Statement stmt = c.createStatement();
            String query = "UPDATE " + tableName + " SET " + columnName + " = "
                    + value + ";";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            // Logger.getLogger(DBTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createCountyOutline(String prefix, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT ST_UNION(the_geom) AS singlegeom "
                    + "INTO public." + prefix + "_shp "
                    + "FROM public." + prefix + "_census_blocks;";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

    }

    public static void createSpatialIndex(String tableName, String columnName,
            Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "CREATE INDEX " + tableName + "_index ON "
                    + tableName + " USING GIST(" + columnName + ");";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

    }

    public static void createPopulationTable(String prefix, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query =
                    "CREATE TABLE " + prefix
                    + "_population (block_id integer PRIMARY KEY, population integer);";
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    public static void insertValueIntoTable(String tableName, String value1,
            String value2, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "INSERT INTO  " + tableName
                    + " VALUES(" + value1 + "," + value2 + ");";
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueries.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    public static ResultSet getTableAsResultSet(String tableName, Connection c) {
        ResultSet rs = null;
          //      System.out.println("==============" + tableName);
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + tableName + ";";
            rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException ex) {
            //  Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE,
            //          null,
            //         ex);
            ex.printStackTrace();
        }
        return rs;
    }

    public static Map<Integer, Integer> getPopulationAsMap(String tableName,
            Connection c) {
        Map<Integer, Integer> result = new HashMap();
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM " + tableName + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.put(new Integer(
                        rs.getInt(DefaultConstants.BLOCK_ID_FIELD)), new Integer(rs.
                        getInt(DefaultConstants.POLULATION_FIELD)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static Map<Integer, org.postgis.MultiPolygon> getBlocksAsMap(
            String tableName,
            Connection c) {

        Map<Integer, org.postgis.MultiPolygon> result =
                new HashMap<Integer, org.postgis.MultiPolygon>();
        // ((org.postgresql.PGConnection)DBController.connectToDB()).addDataType ("multipolygon","org.postgis.MultiPolygon");
        try {
            Statement stmt =
                    (Statement) c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT id, AsText(" + DefaultConstants.GEOM_FIELD
                    + ") AS the_geom FROM " + tableName + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                org.postgis.MultiPolygon poly = new org.postgis.MultiPolygon(rs.
                        getString(DefaultConstants.GEOM_FIELD));
                int idfield = rs.getInt(DefaultConstants.ID_FIELD);
                //  org.postgis.Geometry geom = (org.postgis.Geometry) rs.getObject(
                //         DefaultConstants.GEOM_FIELD);
                // MultiPolygon poly = (MultiPolygon)((PGgeometry) rs.getObject(DefaultConstants.GEOM_FIELD)).getGeometry();

                 result.put(new Integer(rs.getInt(DefaultConstants.ID_FIELD)), poly);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
