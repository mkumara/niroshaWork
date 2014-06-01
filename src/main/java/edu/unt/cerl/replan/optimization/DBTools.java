package edu.unt.cerl.replan.optimization;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBTools {

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
            Logger.getLogger(DBTools.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DBTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
