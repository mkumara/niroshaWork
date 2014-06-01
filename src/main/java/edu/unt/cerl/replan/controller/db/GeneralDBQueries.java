//Copied from old CVS by Sarat
package edu.unt.cerl.replan.controller.db;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.CliffCoordinate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

/**
 * Class GeneralDBQueries
 */
public class GeneralDBQueries {

    //
    // Constructors
    //
    protected GeneralDBQueries() {
    }

    /**
     * This method checks for a given table, column name and entry value, if 
     * a record of value "entry" in the column "columnName" already exists
     *
     * @param tableName name of the table
     * @param columnName name of the column
     * @param entry value of the entry
     * @param c database connection
     * @return true if the entry exists in the database, false otherwise
     */
    protected boolean entryExists(String tableName, String columnName,
            String entry,
            Connection c) {
        boolean exists = false;
        try {
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
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Problem checking for existing database entry", ex);
        }
        return exists;
    }

    /**
     * This method adds a new entry into a table with 2 columns
     * @param tableName table name
     * @param entry1 value of first entry
     * @param entry2 value of second entry
     * @param c database connection
     */
    protected void addEntry(String tableName, String entry1, String entry2,
            Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query =
                    "INSERT INTO " + tableName + " VALUES('" + entry1 + "','"
                    + entry2 + "');";
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Error inserting new user into database", ex);
        }
    }

    /**
     * This method adds a new schema to the database
     * @param schemaName name of new schema
     * @param c database connection 
     */
    void addSchema(String schemaName, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "CREATE SCHEMA " + schemaName + ";";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Error creating schema for new user", ex);
        }

    }

    void deleteEntry(String tableName, String columnName, String entry,
            Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query =
                    "DELETE FROM " + tableName + " WHERE " + columnName + " = '"
                    + entry + "';";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Error deleting database entry", ex);
        }
    }

    void deleteSchema(String schemaName, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "DROP SCHEMA " + schemaName + " CASCADE;";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Error deleting schema entry", ex);
        }
    }

    void createViews(String schema, String name, String[] geographies,
            Map<String, Map> datasets, String key, String suffix, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "CREATE VIEW " + schema + "." + name + suffix
                    + " AS (";
            for (int i = 0; i < geographies.length; i++) {
                query += "SELECT * FROM public." + ((Map<String, String>) datasets.
                        get(geographies[i])).get(key);
                if (i == geographies.length - 1) {
                    query += ");";
                } else {
                    query += " UNION ";
                }
            }
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }

    void dropView(String schema, String name, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "DROP VIEW " + schema + "." + name + ";";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(GeneralDBQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param schema
     * @param table
     * @param values values to be inserted, must contain single quotes for strings
     * @param c
     */
    void insertValuesIntoTable(String schema, String table, List<String> values,
            Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "INSERT INTO " + schema + "." + table + " VALUES(";
            Iterator<String> it = values.iterator();

            while (it.hasNext()) {
                String value = it.next();
                if (it.hasNext()) {
                    query += "'"+value + "',";
                } else {
                    query += "'"+value + "')";
                }
            }
            System.out.println("GeneralDBQueries " + query);
        } catch (SQLException ex) {
            Logger.getLogger(GeneralDBQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    boolean entryExists(String tableName, String columnName1, String entry1,
            String columnName2, String entry2, Connection c) {
        boolean exists = false;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT " + columnName1 + ", " + columnName2
                    + " FROM " + tableName + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String name1 = rs.getString(columnName1);
                String name2 = rs.getString(columnName2);
                if (entry1.equals(name1) && entry2.equals(name2)) {
                    exists = true;
                    break;
                }
            }
            //c.close();
            return exists;
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Problem checking for existing database entry", ex);
        }
        return exists;
    }

    boolean entryExists(String schema, String tableName, String columnName,
            String entry, Connection c) {
        boolean exists = false;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT " + columnName + " FROM " + schema + "."
                    + tableName + ";";
            System.out.println("GeneralDBQueries: \t " + query);
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
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Problem checking for existing database entry", ex);
        }
        return exists;
    }

    boolean entryExists(String schema, String tableName, String columnName1,
            String entry1, String columnName2, String entry2, Connection c) {
        boolean exists = false;
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT " + columnName1 + ", " + columnName2
                    + " FROM " + schema + "." + tableName + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String name1 = rs.getString(columnName1);
                String name2 = rs.getString(columnName2);
                if (entry1.equals(name1) && entry2.equals(name2)) {
                    exists = true;
                    break;
                }
            }
            //c.close();
            return exists;
        } catch (SQLException ex) {
            REPLAN.logger.log(Level.SEVERE,
                    "Problem checking for existing database entry", ex);
        }
        return exists;
    }

    void saveTableAs(String schema1, String fromTable, String schema2,
            String toTable, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query =
                    "SELECT * INTO " + schema2 + "." + toTable + " FROM "
                    + schema1 + "." + fromTable + ";";
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(GeneralDBQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
        void renameTable(String schema, String fromTable,  String toTable, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query =
                    "ALTER TABLE " + schema + "." + fromTable + " RENAME TO "
                    + schema + "." + toTable + ";";
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(GeneralDBQueries.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }


    void createScenarioTables(String schema, String name, String[] geographies,
            Map<String, Map> datasets, String key, String suffix, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "SELECT * INTO " + schema + "." + name + suffix
                    + " FROM (";

            for (int i = 0; i < geographies.length; i++) {
                query += "SELECT * FROM public." + ((Map<String, String>) datasets.
                        get(geographies[i])).get(key);
                if (i == geographies.length - 1) {
                    query += ") as temp;";
                } else {
                    query += " UNION ";
                }
            }
            System.out.println("GeneralDBQueries:  " + query);
            stmt.executeUpdate(query);
            query = "VACUUM ANALYZE " + schema + "." + name + suffix + ";";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    void dropTable(String schema, String name, Connection c) {
        try {
            Statement stmt = c.createStatement();
            String query = "DROP TABLE " + schema + "." + name + ";";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GeneralDBQueries.class.getName()).
                    log(Level.SEVERE, null, ex);

        }
    }

        /**
     * @return       boolean
     * @param        tableName
     * @param        c
     */
    public boolean tableExists(String tableName, Connection c) {
         boolean exists = false;
        try {

            Statement stmt = c.createStatement();
            ResultSet rs = c.getMetaData().getTables(null, null, tableName, null);
            if (rs.next()) {
                exists = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
          return exists;
    }

    /**
     * @return       boolean
     * @param        schemaName
     * @param        tableName
     * @param        c
     */
    public boolean tableExists(String schemaName, String tableName, Connection c) {
         boolean exists = false;
        try {

            Statement stmt = c.createStatement();
            ResultSet rs = c.getMetaData().getTables(null, schemaName, tableName, null);
            if (rs.next()) {
                System.out.println("table exists method without sqlexception\n");
                exists = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
                     return exists;
    }


//    public boolean tableExistsTemp(String schemaName, String tableName, Connection c) throws SQLException{
//        Statement stmt = c.createStatement();
//        System.out.println("table exists method with sqlexception\n");
//        String query = "SELECT COUNT(*) FROM pg_tables WHERE tablename = '" + tableName + "' AND schemaname = '" + schemaName + "';";
//        System.out.println(query);
//        ResultSet rs = stmt.executeQuery(query);
//        rs.next();
//        if (rs.getInt("count")==1) {
//            return true;
//                }
//        return false;
//    }

        /**
     * Check if a column exists in a table of the DB
     * @param tableName name of table
     * @param columnName name of column
     * @param c connection
     * @return true or false
     * @throws SQLException
     */
//    public static boolean columnExists(String tableName, String columnName,
//            Connection c) throws SQLException {
//        boolean exists = false;
//        //GISConversionTools gisConvTools = new GISConversionTools();
//        ResultSet rs = c.getMetaData().getColumns(null, null, tableName, null);
//        while (rs.next()) {
//            String name = rs.getString("COLUMN_NAME");
//            if (columnName.equals(name)) {
//                exists = true;
//                break;
//            }
//        }
//        //c.close();
//        return exists;
//    }


        public static boolean columnExists(String schemaName, String tableName, String columnName,
            Connection c) throws SQLException {
        boolean exists = false;
        //GISConversionTools gisConvTools = new GISConversionTools();
        ResultSet rs = c.getMetaData().getColumns(null, schemaName, tableName, null);
        while (rs.next()) {
            String name = rs.getString("COLUMN_NAME");
            if (columnName.equals(name)) {
                exists = true;
                break;
            }
        }
        //c.close();
        if(exists) {
            System.out.println("columnExistsTemp: Column exists \n");
        } else {
            System.out.println("columnExistsTemp: Column doesnot exist \n");
        }
        return exists;
    }

    public void cliffRoadTable(String id, String name, String[] geographies, Map map, Connection c) {
        double x1=0.0,x2=0.0,y1=0.0,y2=0.0; 
    try {
            if(this.tableExists(id, name+DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX, c)){
            String geom="";
            Statement stmt = c.createStatement();
            String queryTest ="SELECT x1, y1, x2, y2 FROM "+id+"."+name + DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX+ " LIMIT 1";
            ResultSet rs = stmt.executeQuery(queryTest);
            if(rs.next()) {
                 x1 = rs.getDouble(1);
                y1 = rs.getDouble(2);
                x2 = rs.getDouble(3);
                y2 = rs.getDouble(4);
            }

            if(x1!=0.0){
            
            
            String query1 ="UPDATE "+id+"."+name + DefaultConstants.ROAD_SUFFIX+ " SET "+
                    "the_geom=st_intersection(the_geom ,ST_MakeEnvelope("+x1+", "+y1+", "+x2+", "+y2+", 4326))";
            
            System.out.println("Query in GeneralQueries.cliffRoad: " + query1 + "\n");
            stmt.executeUpdate(query1);
            
            }// end of if geom!=""
            
            }//table exists
           
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
}
    }

    public void cliffCentroidTable(String id, String name, String[] geographies, Map map, Connection c) {
     double x1=0.0,x2=0.0,y1=0.0,y2=0.0;      
       try {
           if(this.tableExists(id, name+DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX, c)){
               System.out.println("CENTROIDS CLIFFED TABLE THERE");
            Statement stmt = c.createStatement();
            String queryTest ="SELECT x1, y1, x2, y2 FROM "+id+"."+name + DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX+ " LIMIT 1";
            ResultSet rs = stmt.executeQuery(queryTest);
            if(rs.next()) {
                x1 = rs.getDouble(1);
                y1 = rs.getDouble(2);
                x2 = rs.getDouble(3);
                y2 = rs.getDouble(4);               
            }
            
            System.out.println("Clipping coords ="+x1+" "+y1+" "+x2+" "+y2);
            if(x1!=0.0){
                           
            String query1 ="SELECT logrecno from "+id+"."+name + DefaultConstants.CENTROID_SUFFIX + 
                    " where centroid && ST_MakeEnvelope("+x1+", "+y1+", "+x2+", "+y2+", 4326)";
            
            String query2="DELETE from "+id+"."+name + DefaultConstants.CENTROID_SUFFIX+
                    " WHERE logrecno NOT IN ("+query1+")";

            System.out.println("Query in GeneralQueries.cliffCentroids: " + query2 + "\n");
            stmt.executeUpdate(query2);
            
            }//geom!=""
           }//table exists
           
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

        
    }

    public void cliffCensusBlockTable(String id, String name, String[] geographies, Map map, Connection c) {
        double x1=0.0;
          try {
            if(this.tableExists(id, name+DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX, c)){
            Statement stmt = c.createStatement();
            String queryTest ="SELECT x1 FROM "+id+"."+name + DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX+ " LIMIT 1";
            ResultSet rs = stmt.executeQuery(queryTest);
            if(rs.next()) {
               x1=rs.getDouble(1);
            }
            
            if(x1!=0.0){
            
            String query1 ="SELECT logrecno from "+id+"."+name + DefaultConstants.CENTROID_SUFFIX;
                  
            
            String query2="DELETE from "+id+"."+name + DefaultConstants.BLOCK_SUFFIX+
                    " WHERE logrecno NOT IN ("+query1+")";

            System.out.println("Query in GeneralQueries.cliffBlocks: " + query2 + "\n");
            stmt.executeUpdate(query2);
            
            }//geom!=""
            }// table exists
           
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

   public  void updateCliffedGeographies(CliffCoordinate coord) {
        String id = UserState.userId;
        Connection c = REPLAN.getController().getConnection();
        ScenarioState state=REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String name = state.getWorkingCopyName();
         try {
            if(this.tableExists(id, name+DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX, c)){
            
            Statement stmt = c.createStatement();
            String query ="UPDATE "+id+"."+name + DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX+ " SET "+
                    "x1="+String.valueOf(coord.getX1())+", y1="+String.valueOf(coord.getY1())+", x2="+String.valueOf(coord.getX2())+", y2="+String.valueOf(coord.getY2());
            
            stmt.executeUpdate(query);
            System.out.println("Query in GeneralQueries.update cliffgeographies: " + query + "\n");
            
               
           }// table exists
           
        } catch (SQLException ex) {
            Logger.getLogger(ReplanQueries.class.getName()).log(Level.SEVERE,
                    null, ex);
        }     
        
    }


}