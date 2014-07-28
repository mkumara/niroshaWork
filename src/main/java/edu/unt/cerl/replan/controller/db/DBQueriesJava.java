//Copied from old CVS by Sarat
package edu.unt.cerl.replan.controller.db;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.optimization.POD;
import edu.unt.cerl.replan.view.CliffCoordinate;
import edu.unt.cerl.replan.view.CliffCoordinateIrregular;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class DBQueriesJava
 */
public class DBQueriesJava implements DBQueries {

    //
    // Fields
    //
    private ReplanQueries replanQueries;
    private PODQueries podQueries;
    private GeneralDBQueries generalQueries;
    //
    // Constructors
    //

    public DBQueriesJava() {
        replanQueries = new ReplanQueries();
        podQueries = new PODQueries();
        generalQueries = new GeneralDBQueries();
    }

    //
    // Methods
    //
    //
    // Accessor methods
    //
    //
    // Other methods
    //
    /**
     * @return boolean
     * @param tableName
     * @param c
     */
    @Override
    public boolean tableExists(String tableName, Connection c) {
        return this.generalQueries.tableExists(tableName, c);
    }

//    public boolean tableExists(Connection c, String schemaName, String tableName) throws SQLException {
//        return this.generalQueries.tableExistsTemp(schemaName, tableName, c);
//    }

    /**
     * @return boolean
     * @param schemaName
     * @param tableName
     * @param c
     */
    @Override
    public boolean tableExists(String schemaName, String tableName, Connection c) {
        return this.generalQueries.tableExists(schemaName, tableName, c);
    }

    /**
     * Determines whether the specified column already exists in the specified table in the public schema
     * @return boolean TRUE if the column already exists, FALSE otherwise
     * @param tableName name of the table being specified
     * @columnName name of the column we are looking for
     * @param c Connection
     */
    public boolean columnExists(String tableName, String columnName,
            Connection c) {
        return columnExists("public", tableName,columnName,c);
    }

    /**
     * Determines whether the specified column already exists in the specified table
     * @return boolean TRUE if the column already exists, FALSE otherwise
     * @param schemaName schema of the table being specified
     * @param tableName name of the table being specified
     * @columnName name of the column we are looking for
     * @param c Connection
     */
    public boolean columnExists(String schemaName, String tableName,
            String columnName, Connection c) {
        try {
         //   String query = "SELECT TRUE AS boolean " + "FROM   pg_attribute " + "WHERE  attrelid = '" + schemaName + "." + tableName + "'::regclass  -- cast to a registered class (table) " + "AND    attname = '" + columnName + "' " + "AND    NOT attisdropped ;";
            String query ="SELECT TRUE AS boolean\n "
                    + "FROM   pg_attribute \n "
                    + "WHERE  attrelid = '"+schemaName + "." + tableName +"'::regclass  -- cast to a registered class (table)\n "
                    + "AND    attname = 'catchment_area'\n "
                    + "AND    NOT attisdropped ;";
            System.out.println(query);
            ResultSet results = c.createStatement().executeQuery(query);
            if (!results.next()){ //if there is no next record, return false
                return false;
            }
            System.out.println("columnExists: "+ results.getString("boolean"));
            return results.getString("boolean").equals("t");



        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean entryExists(String tableName, String columnName, String entry,
            Connection c) {
        return this.generalQueries.entryExists(tableName, columnName, entry, c);
    }

    /**
     * @return boolean
     * @param tableName
     * @param columnName1
     * @param entry1
     * @param columnName2
     * @param entry2
     * @param c
     */
    public boolean entryExists(String tableName, String columnName1,
            String entry1, String columnName2, String entry2, Connection c) {
        return this.generalQueries.entryExists(tableName, columnName1, entry1, columnName2, entry2, c);
    }

    /**
     * @return int
     * @param tableName
     * @param c
     */
    public int tableSize(String tableName, Connection c) {
        return 0;
    }

    /**
     * @param c
     * @param tableName
     * @param columnName
     * @param value
     */
    public void setFieldTo(Connection c, String tableName, String columnName,
            String value) {
    }

    /**
     * @param c
     * @param tableName
     * @param columnName
     * @param value
     */
    public void setFieldTo(Connection c, String tableName, String columnName,
            int value) {
    }

    /**
     * @param s
     * @param c
     */
    public void calculateCatchmentAreas(ScenarioState s, Connection c) {
    }

    /**
     * @param prefix
     * @param number
     * @param ringDistance
     * @param s
     * @param c
     */
    public void calculateTrafficRings(String prefix, int number,
            double ringDistance, ScenarioState s, Connection c) {
    }

    /**
     * @param s
     * @param numRings
     * @param ringDistance
     * @param c
     */
    public void createRings(ScenarioState s, int numRings, double ringDistance,
            Connection c) {
        String query = "ALTER TABLE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " ADD ring integer;";
        try {
            System.out.println("createRings - adding column ring to table:");
            System.out.println(query);
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Set all blocks to be part of outer ring - different methodology here than REPLAN 1.0
        query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " "
                + "SET ring = " + (numRings) + ";";
        System.out.println("Query to set all blocks to be part of outer ring: ");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

// correct for all blocks that shouldn't be part of outer ring
        for (int i = 0; i < numRings - 1; i++) {
            double factor1 = (double) i * ringDistance;
            double factor2 = (double) (i + 1) * ringDistance;

            query = "UPDATE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " "
                    + "SET ring = " + (i + 1) + " "
                    + "FROM ("
                    + "SELECT b2p.block as block2 "
                    + "FROM "
                    + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " p,"
                    + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " b2p,"
                    + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX + " c "
                    + "WHERE "
                    + "p.id=b2p.pod AND "
                    + "c.logrecno=b2p.block AND "
                    + "("
                    + "p.type ='true' "
                    + ") AND "
                    + "status = 'true' AND "
                    + "distance(p.location,c.centroid)>" + factor1 + " AND "
                    + "distance(p.location,c.centroid)<=" + factor2 + " "
                    + ") ring_blocks "
                    + "WHERE block = ring_blocks.block2;";

            System.out.println("CreateRings Query:");
            System.out.println(query);
            try {
                c.createStatement().executeUpdate(query);
            } catch (SQLException ex) {
                Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.createRingPolygons(s, c);
    }

    /**
     * @param s
     * @param numRings
     * @param ringDistance
     * @param c
     */
    public void createCoverageZones( ScenarioState s, int numPODs,
            POD podInfoArray[], List catchment[], String censusBlockIds[],
            Connection c )
    {

        String query = "ALTER TABLE " + ScenarioState.getAuthor() + "."
                + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX
                + " ADD coverage integer;";
        try {
            System.out.println("createCoverageZones -"
                    + " adding column coverage to table:");
            System.out.println(query);
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Set all blocks to be part of outer ring - different methodology here than REPLAN 1.0
        query = "UPDATE " + ScenarioState.getAuthor() + "."
                + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " "
                + "SET coverage = 0;";
        System.out.println("Query to set all blocks to be part of uncovered zone: ");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

// correct for all blocks that shouldn't be part of outer ring

        for (int i = 0; i < numPODs; i++) {
            int podId                  = podInfoArray[i].getId() - 1;

            String string = "(";
            boolean first = true;

            Iterator it = catchment[podId].iterator();
            while( it.hasNext() )
            {
                int current = ( (Integer) it.next() ).intValue();
                if (!first) {
                    string += ",";
                }
                first = false;
                string += (" " + censusBlockIds[current]);
            }

            string += ")";

            query = "UPDATE "
                    + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " AS b2p "
                    + "SET coverage = " + (podId+1)
                    + " WHERE "
                    + " b2p.block IN " + string + " "
                    + ";";

            System.out.println("CreateCoverage Query:");
            System.out.println(query);
            try {
                c.createStatement().executeUpdate(query);
            } catch (SQLException ex) {
                Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
/*
        query = "UPDATE "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " AS b2p "
                + "SET coverage = 2 WHERE b2p.block IN ( 912380, 912377, 912379, 912376, 912375, 912378, 912381, 912336 );";

        System.out.println("CreateCoverage Query:");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
        this.createCoveragePolygons(s, c);
    }

    /**
     * @param s
     * @param numRings
     * @param ringDistance
     * @param c
     */
    public void createProportionalPartitioningMapping( ScenarioState s, int numPODs,
            POD podInfoArray[], List catchment[], String censusBlockIds[],
            Connection c )
    {
        if (REPLAN.getQueries().tableExists( s.getAuthor(), s.getWorkingCopyName()
                + DefaultConstants.B2P_SUFFIX, c)) {
            REPLAN.getQueries().dropTable( s.getAuthor(), s.getWorkingCopyName()
                    + DefaultConstants.B2P_SUFFIX, c);
        }
/*
        String query = "CREATE TABLE "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName()
                    + DefaultConstants.B2P_SUFFIX
                + " ( block integer UNIQUE,"
                + " pod integer );";

        System.out.println("Create B2P Query:");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
       String query = "SELECT b.logrecno as block "
                + " INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName()
                + DefaultConstants.B2P_SUFFIX
                + " FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS b;";

        System.out.println("Create B2P Query:");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "ALTER TABLE " + ScenarioState.getAuthor() + "."
                + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX
                + " ADD pod integer;";
        try {
            System.out.println("createRings - adding column ring to table:");
            System.out.println(query);
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < numPODs; i++) {
            int podId = podInfoArray[i].getId() - 1;

            String string = "(";
            boolean first = true;

            Iterator it = catchment[podId].iterator();
            while( it.hasNext() )
            {
                int current = ( (Integer) it.next() ).intValue();
                if (!first) {
                    string += ",";
                }
                first = false;
                string += (" " + censusBlockIds[current]);
            }

            string += ")";

            query = "UPDATE "
                    + ScenarioState.getAuthor() + "." + s.getWorkingCopyName()
                    + DefaultConstants.B2P_SUFFIX + " AS b2p "
                    + "SET pod = " + (podId+1)
                    + " WHERE "
                    + " b2p.block IN " + string + " "
                    + ";";

            System.out.println("CreateCoverage Query:");
            System.out.println(query);
            try {
                c.createStatement().executeUpdate(query);
            } catch (SQLException ex) {
                Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param s
     * @param numRings
     * @param ringDistance
     * @param c
     */
    public void dropCoverageTable( ScenarioState s, Connection c )
    {
        String query = "DROP TABLE IF EXISTS " + ScenarioState.getAuthor() + "."
                + s.getWorkingCopyName() + DefaultConstants.COVERAGE_SUFFIX;
        try {
            System.out.println(query);
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param s
     * @param numRings
     * @param c
     */
    public void createRingsBasedOnMaxDist(ScenarioState s, int numRings,
            Connection c) {
    }

    /**
     * @param s
     * @param c
     */
    public void createRingPolygons(ScenarioState s, Connection c) {
        String dropQuery = "DROP SEQUENCE IF EXISTS " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.RINGS_SEQ_SUFFIX + ";";
        String query = "CREATE SEQUENCE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.RINGS_SEQ_SUFFIX + ";";
        System.out.println("CreateRingPolygons creating sequence query:");
        System.out.println(query);


        try {
            c.createStatement().executeUpdate(dropQuery);
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "SELECT "
                + "nextval('" + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.RINGS_SEQ_SUFFIX + "') AS id, "
                + "b2p.pod AS pod, "
                + "b2p.ring as ring, "
                + "ST_UNION(b.the_geom) AS the_geom "
                + "INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX + " "
                + "FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " AS b2p, "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS b "
                + "WHERE b2p.block=b.logrecno "
                + "GROUP BY "
                + "b2p.pod, "
                + "b2p.ring;";
        System.out.println("CreateRingPolygons query 1:");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param s
     * @param c
     */
    public void createCoveragePolygons(ScenarioState s, Connection c) {
        String dropQuery = "DROP SEQUENCE IF EXISTS " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.COVERAGE_SEQ_SUFFIX + ";";
        String query = "CREATE SEQUENCE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.COVERAGE_SEQ_SUFFIX + ";";
        System.out.println("CreateCoveragePolygons creating sequence query:");
        System.out.println(query);

        try {
            c.createStatement().executeUpdate(dropQuery);
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "SELECT "
                + "nextval('" + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.COVERAGE_SEQ_SUFFIX + "') AS id, "
                + "b2p.pod AS pod, "
                + "b2p.coverage as coverage, "
                + "ST_UNION(b.the_geom) AS the_geom "
                + "INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.COVERAGE_SUFFIX + " "
                + "FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " AS b2p, "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS b "
                + "WHERE b2p.block=b.logrecno "
                + "GROUP BY "
                + "b2p.pod, "
                + "b2p.coverage;";
        System.out.println("CreateCoveragePolygons query 1:");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param s
     * @param c
     */
    public void createCrossingPoints(ScenarioState s, Connection c) {
        String query = "CREATE SEQUENCE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SEQ_SUFFIX + ";";
        System.out.println("createCrossingPoints: Creating sequence");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "SELECT "
                + "ST_CollectionExtract(temp.intersection,1) AS crossing_point, "
                + "(temp.pod-1) AS class, " //This line is for testing purposes only! It must be removed once classes are assigned to crossingpoints
                + "temp.pod AS pod, "
                + "temp.ring AS ring, "
                + "nextval('" + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SEQ_SUFFIX + "') AS id, "
                + "temp.road_id AS road_id "
                + "INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " "
                + "FROM ("
                + "SELECT DISTINCT "
                + "ST_Intersection(ST_Boundary(ri.the_geom), ro.the_geom) AS intersection, "
                + "ri.pod AS pod, "
                + "ri.ring AS ring, "
                + "ro.id AS road_id "
                + "FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.ROAD_SUFFIX + " ro, "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.RINGS_SUFFIX + " ri "
                + "WHERE ri.ring < 3 AND "
                + "ST_Boundary(ri.the_geom) && ro.the_geom) AS temp "
                + "WHERE GeometryType(ST_CollectionExtract(temp.intersection,1))='POINT' OR "
                + "GeometryType(ST_CollectionExtract(temp.intersection,1))='MULTIPOINT';";

        System.out.println("createCrossingPoints: Creating Points Table");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "DROP SEQUENCE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SEQ_SUFFIX + ";";
        System.out.println("createCrossingPoints: Dropping sequence");
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param s
     * @param c
     */
    public void createBlock2PodMapping(ScenarioState s) {
        Connection c = REPLAN.getController().getConnection();

        String query = "SELECT b2.id AS block, p2.id AS pod "
                + "INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " "
                + "FROM (SELECT DISTINCT b.id AS id, MIN(distance(b.centroid,p.location)) AS dist "
                + "FROM " + DefaultConstants.BLOCK_SUFFIX + " AS b, " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " AS p"
                + "WHERE "
                + "(p.type = 'public' OR p.type = 'Public') AND "
                + "onoff = 'true' GROUP BY b.id) AS mins, "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " AS p2, " + DefaultConstants.CENTROID_TABLE + "AS b2 "
                + "WHERE b2.id = mins.id "
                + "AND mins.dist = distance(b2.centroid, p2.location);";
        System.out.println(query);

    }

    /**
     * @param s
     * @param c
     * @param tableName
     * @param newColumnName
     */
    public void addColumn(ScenarioState s, Connection c, String tableName,
            String newColumnName) {

    }

    /**
     */
    public void saveChangesToPOD() {
    }

    /**
     */
    public void addNewPOD(ScenarioState state, int fid, int id, String name, String address, String city, String zip, double latitude, double longitude, Boolean type, Boolean status, String comments, int numBooths) throws SQLException {
//        this.podQueries.addNewPOD(id);

        // if the table does not yet exist
        if (!tableExists(UserState.userId + "." + state.getWorkingCopyName() + DefaultConstants.POD_SUFFIX, REPLAN.getController().getConnection())) {
            try {
                // create it
                edu.unt.cerl.replan.controller.db.PODQueries.createPODTable(state);
            } catch (SQLException ex) {
                Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // add new POD
        //      edu.unt.cerl.replan.controller.db.PODQueries.addNewPOD(id, id, null, null, null, null, id, id, Boolean.TRUE, Boolean.TRUE, null, id);
    }

    /**
     */
    public void removePOD() {
    }

    /**
     * @return Map
     * @param state
     * @param c
     */
    public Map readPODsFromDB(ScenarioState state, Connection c) {
        Map list = null;
        try {
            list = podQueries.readPODsFromDB(state);
        } catch (SQLException e) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("We had problems");
        }
        return list;
    }

        /**
     * @return Map
     * @param state
     * @param c
     */
    public Map readWrkCpyPODsFromDB(ScenarioState state, Connection c) {
        Map list = null;
        try {
            list = podQueries.readWrkCpyPODsFromDB(state);
        } catch (SQLException e) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("We had problems");
        }
        return list;
    }

    /**
     * @param tableName
     * @param field1
     * @param fieldInParenthesis
     * @param c
     */
    public void getFieldFromDB(String tableName, String field1,
            String fieldInParenthesis, Connection c) {
    }

    /**
     * @param tableName
     * @param field1
     * @param field2
     * @param valueField2
     * @param c
     */
    @Override
    public void getArrayOfField1IfCertainValueInField2(String tableName,
            String field1, String field2, String valueField2, Connection c) {
    }

    /**
     * @return Map<String,String>
     * @param tableName
     * @param field1
     * @param field2
     * @param field3
     * @param c
     */
    @Override
    public Map<String, String> getMapofFields1And2IfCertainValueInField3(
            String tableName, String field1, String field2, String field3,
            Connection c) {
        return null;
    }

    @Override
    public void removeOldWorkingCopies(Connection c) {
        this.replanQueries.removeOldWorkingCopies(c);
    }

    @Override
    public String[] getUsersFromDB(Connection c) {
        return this.replanQueries.getUsersFromDB(c);
    }

    @Override
    public void addSchema(String schemaName, Connection c) {
        this.generalQueries.addSchema(schemaName, c);
    }

    @Override
    public void addEntry(String tableName, String entry1, String entry2,
            Connection c) {
        this.generalQueries.addEntry(tableName, entry1, entry2, c);
    }

    @Override
    public void deleteEntry(String tableName, String columnName, String entry,
            Connection c) {
        this.generalQueries.deleteEntry(tableName, columnName, entry, c);
    }

    @Override
    public void deleteSchema(String schemaName, Connection c) {
        this.generalQueries.deleteSchema(schemaName, c);
    }

    @Override
    public void insertTimestamp(String userId, String workingCopyName,
            Connection c) {
        this.replanQueries.insertTimestamp(userId, workingCopyName, c);
    }

    @Override
    public void createViews(String schema, String name, String[] geographies, Map<String, Map> datasets, String key, String suffix, Connection c) {
        this.generalQueries.createViews(schema, name, geographies, datasets, key, suffix, c);
    }

    @Override
    public void dropView(String schema, String name, Connection c) {
        this.generalQueries.dropView(schema, name, c);
    }

    @Override
    public void insertValuesIntoTable(String schema, String table, List<String> values, Connection c) {
        this.generalQueries.insertValuesIntoTable(schema, table, values, c);
    }

    @Override
    public void addNewPOD(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean entryExists(String schema, String tableName,
            String columnName, String entry, Connection c) {
        return this.generalQueries.entryExists(schema, tableName, columnName, entry, c);
    }

    @Override
    public boolean entryExists(String schema, String tableName,
            String columnName1, String entry1, String columnName2, String entry2,
            Connection c) {
        return this.generalQueries.entryExists(schema, tableName, columnName1, entry1, columnName2, entry2, c);
    }

    @Override
    public void saveTableAs(String schema1, String fromTable, String schema2,
            String toTable, Connection c) {
        this.generalQueries.saveTableAs(schema1, fromTable, schema2, toTable, c);
    }

    @Override
    public void createScenarioTables(String schema, String name,
            String[] geographies,
            Map<String, Map> datasets, String key, String suffix, Connection c) {
        this.generalQueries.createScenarioTables(schema, name, geographies, datasets, key, suffix, c);
    }

    @Override
    public void dropTable(String schema, String name, Connection c) {
        this.generalQueries.dropTable(schema, name, c);
    }

    @Override
    public void createGeographyTable(String id, String name, String[] strings,
            Connection c) {
        this.replanQueries.createGeographyTable(id, name, strings, c);
    }

    @Override
    public void createCliffedGeographyTable(String id, String name, String[] strings,
            Connection c) {
        this.replanQueries.createCliffedGeographyTable(id, name, strings, c);
    }
    

    @Override
    public void saveScenarioInformation(ScenarioState state, Connection c) {
        this.replanQueries.saveScenarioInformation(state, c);
    }

    @Override
    public void updateScenarioInformation(ScenarioState state, Connection c) {
        this.replanQueries.updateScenarioInformation(state, c);
    }

    @Override
    public List getScenarioInfo(String schema, String name, Connection c) {
        return this.replanQueries.getScenarioInfo(schema, name, c);
    }

    @Override
    public String[] getScenariosForUser(String userId, Connection c) {
        return this.replanQueries.getScenariosForUser(userId, c);
    }

    @Override
    public Map<String, String> getDescriptions(String userId,
            Connection c) {
        return this.replanQueries.getDescriptions(userId, c);
    }

    @Override
    public String[] getGeographies(String userId, String scenario, Connection c) {
        return this.replanQueries.getGeographies(userId, scenario, c);
    }

    @Override
    public void deleteScenario(String author, String name, Connection c) {
        this.replanQueries.deleteScenario(author, name, c);
    }

    @Override
    public void renameTable(String schema, String fromTable, String toTable,
            Connection c) {
        this.generalQueries.renameTable(schema, fromTable, toTable, c);
    }

    @Override
    public void removeTimestamp(String schema, String scenario, Connection c) {
        this.replanQueries.removeTimestamp(schema, scenario, c);
    }

    @Override
    public void createCatchmentAreaPolygons(ScenarioState s, Connection c) {
        String query = "DROP TABLE IF EXISTS "
                 + ScenarioState.getAuthor() + "." + s.getWorkingCopyName()
                 + DefaultConstants.CATCHMENT_SUFFIX + ";";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("We had problems");
        }

        query = "SELECT b2p.pod AS id, ST_UNION(b.the_geom) AS the_geom"
                + " INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX
                + " FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " AS b2p,"
                + " " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.BLOCK_SUFFIX + " AS b"
                + " WHERE b2p.block=b.logrecno "
                + " GROUP BY b2p.pod "
                + " ORDER BY b2p.pod;";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("We had problems");
        }
    }

    @Override
    public void createBlock2PodMapping(ScenarioState s, Connection c) {
//        throw new UnsupportedOperationException("Not supported yet.");
        //Statement stmt = c.createStatement();
        String query = "SELECT b2.logrecno AS block, p2.id AS pod "
                + "INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.B2P_SUFFIX + " "
                + "FROM (SELECT DISTINCT b.logrecno AS id, MIN(distance(b.centroid,p.location)) AS dist "
                + "FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX + " AS b, " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " AS p "
                + "WHERE "
                + "p.type = 'true' AND "
                + "status = 'true' GROUP BY b.logrecno) AS mins, "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.POD_SUFFIX + " AS p2, " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CENTROID_SUFFIX + " AS b2 "
                + "WHERE b2.logrecno = mins.id "
                + "AND mins.dist = distance(b2.centroid, p2.location);";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
    
    @Override
    public void crossingPointsBreakApartMultipoints(ScenarioState s, Connection c) {
        // The following two queries need to be broken apart to use the proper table names:

        // This query takes all MULTIPOINT records and creates a new 
        String query = "INSERT INTO " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " "
                + "SELECT geom,class,pod,ring,id,road_id "
                + "FROM ( "
                + "SELECT class, pod,ring,id,road_id,(ST_DumpPoints(crossing_point)).* "
                + "FROM "
                + "(SELECT "
                + "class,pod,ring,id,road_id, crossing_point FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " AS geom "
                + "WHERE GEOMETRYTYPE(crossing_point)='MULTIPOINT' "
                + ") AS g "
                + " ) j;";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "DELETE FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " WHERE GEOMETRYTYPE(crossing_point)='MULTIPOINT';";
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    /**
     * Reasoning for the crossingPointsEliminateDuplicateRoads method: There may
     * be more than one crossing point for a particular road segment. This
     * method eliminates all but one crossing point per road segment. Road
     * segments in the GIS data generally extend from one intersection to the
     * next. It is unlikely that a single road segment will pass through a ring
     * twice in a way that is meaningful. If the road segment passes from one
     * ring to another and back again but has no intersections in one of the
     * rings, there is no roadway route on which people would be able to travel
     * directly to the POD via this road segment. Further, if rings are drawn
     * along census block boundaries, and census blocks often are drawn along
     * actual physical boundaries such as roads, it is likely that there would
     * be an intersection at the transition from one ring to the next, thus
     * causing the beginning of a new road segment.
     */
    public void crossingPointsEliminateDuplicateRoads(ScenarioState s, Connection c) {
        String query = "DELETE FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX
                + " WHERE " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + ".id "
                + " NOT IN(SELECT DISTINCT ON (dt.road_id) "
                + " dt.id "
                + " FROM " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " dt "
                + " ORDER BY dt.road_id DESC) ;";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void crossingPointsEliminateFuncl(ScenarioState s, Connection c, int funcl) {
        // may need to only do if using real traffic data
        String query = "DELETE FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " "
                + " USING "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.ROAD_SUFFIX + " "
                + " WHERE "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.ROAD_SUFFIX + ".id  = "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + ".road_id "
                + "AND " + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.ROAD_SUFFIX + ".funcl = " + funcl + ";";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);

        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    //@Override
    public void cleanCrossingPoints(ScenarioState s, Connection c, double buffer) {
        System.out.println("Still need to port cleanCrossingPoints!");

        String query =
                "  DELETE FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " "
                + "WHERE "
                + " id "
                + "NOT IN "
                + " ( "
                + "  SELECT "
                + "   cpb.id "
                + "  FROM "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CATCHMENT_SUFFIX + " AS c, "
                + "   ( "
                + "    SELECT "
                + "     ST_Buffer(cp.crossing_point," + buffer + ") AS buffer,* "
                + "    FROM  "
                + ScenarioState.getAuthor() + "." + s.getWorkingCopyName() + DefaultConstants.CROSSINGPT_SUFFIX + " AS cp "
                + "   ) AS cpb "
                + "  WHERE "
                + "   ST_Within(cpb.buffer,c.the_geom) "
                + " ) "
                + "  ;";
        System.out.println(query);
        try {
            c.createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DBQueriesJava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //called when save is clicked
    //workingCopy tables are copied to new tables without the working copy prefix
    public void copyWorkingCopyOnSave(ScenarioState s, Connection c) {
        //copy workingCopy to newTable without workingCopy prefix

        String fromTablePrefix = s.getAuthor() + "." + s.getWorkingCopyName();
        String toTablePrefix = s.getAuthor() + "." + s.getName();

        //block_to_pod
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.BLOCK2POD_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.BLOCK2POD_SUFFIX, c);

        //catchment
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.CATCHMENT_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.CATCHMENT_SUFFIX, c);

        //coverage
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.COVERAGE_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.COVERAGE_SUFFIX, c);

        //crossingpoints
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.CROSSINGPT_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.CROSSINGPT_SUFFIX, c);

        //pods
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.POD_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.POD_SUFFIX, c);

        //rings
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.RINGS_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.RINGS_SUFFIX, c);
        if (s.isNewScenario()) {
            replanQueries.saveScenarioInformation(s, c);
        } else {
            replanQueries.updateScenarioInformation(s, c);
        }

    }

    //called when save as is selected from replan menu bar
    //copies workingCopies to tables under newScenario
    public void copyWorkingCopyToNewScenarioOnSaveAs(ScenarioState s, String newScenario, Connection c) {

        String fromTablePrefix = s.getAuthor() + "." + s.getWorkingCopyName();
        String toTablePrefix = s.getAuthor() + "." + newScenario;

        //block_to_pod
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.BLOCK2POD_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.BLOCK2POD_SUFFIX, c);

        //catchment
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.CATCHMENT_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.CATCHMENT_SUFFIX, c);

        //coverage
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.COVERAGE_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.COVERAGE_SUFFIX, c);

        //crossingpoints
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.CROSSINGPT_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.CROSSINGPT_SUFFIX, c);

        //pods
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.POD_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.POD_SUFFIX, c);

        //rings
        saveTableAs(s.getAuthor(), fromTablePrefix + DefaultConstants.RINGS_SUFFIX, s.getAuthor(), toTablePrefix + DefaultConstants.RINGS_SUFFIX, c);
        if (s.isNewScenario()) {
            replanQueries.saveScenarioInformation(s, c);
        } else {
            replanQueries.updateScenarioInformation(s, c);
        }
    }

    @Override
    public void cliffRoadTable(String id, String name, String[] geographies, Map map, Connection c) {
        generalQueries.cliffRoadTable(id, name, geographies, map, c);
}

    @Override
    public void cliffCentroidTable(String id, String name, String[] geographies, Map map, Connection c) {
         generalQueries.cliffCentroidTable(id, name, geographies, map, c);
    }

    @Override
    public void cliffCensusBlockTable(String id, String name, String[] geographies, Map map, Connection c) {
         generalQueries.cliffCensusBlockTable(id, name, geographies, map, c);
    }

    @Override
    public void updateCliffedGeographies(CliffCoordinate coord) {
        generalQueries.updateCliffedGeographies(coord);
    }
    
    @Override
    public void cliffRoadTableIrregular(String id, String name, String[] geographies, Map map, Connection c) {
        generalQueries.cliffRoadTableIrregular(id, name, geographies, map, c);
}

    @Override
    public void cliffCentroidTableIrregular(String id, String name, String[] geographies, Map map, Connection c) {
         generalQueries.cliffCentroidTableIrregular(id, name, geographies, map, c);
    }

    @Override
    public void cliffCensusBlockTableIrregular(String id, String name, String[] geographies, Map map, Connection c) {
         generalQueries.cliffCensusBlockTableIrregular(id, name, geographies, map, c);
    }

    @Override
    public void updateCliffedGeographiesIrregular(CliffCoordinateIrregular coord) {
        generalQueries.updateCliffedGeographiesIrregular(coord);
    }
}
