//Copied from old CVS by Sarat
package edu.unt.cerl.replan.controller.db;

import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.view.CliffCoordinate;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Class DBQueriesPostgreSQL
 */
public class DBQueriesPostgreSQL implements DBQueries {

    //
    // Fields
    //
    //
    // Constructors
    //
    public DBQueriesPostgreSQL() {
    }

    ;

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
     * @return       boolean
     * @param        tableName
     * @param        c
     */
    public boolean tableExists(String tableName, Connection c) {
        return false;
    }

    /**
     * @return       boolean
     * @param        schemaName
     * @param        tableName
     * @param        c
     */
    public boolean tableExists(String schemaName, String tableName, Connection c) {
        return false;
    }

    /**
     * @return       boolean
     * @param        tableName
     * @param        columnName
     * @param        c
     */
    public boolean columnExists(String tableName, String columnName,
            Connection c) {
        return false;
    }

    /**
     * @return       boolean
     * @param        schemaName
     * @param        tableName
     * @param        columnName
     * @param        c
     */
    public boolean columnExists(String schemaName, String tableName,
            String columnName, Connection c) {
        return false;
    }

    /**
     * @return       boolean
     * @param        tableName
     * @param        columnName1
     * @param        entry1
     * @param        columnName2
     * @param        entry2
     * @param        c
     */
    public boolean entryExists(String tableName, String columnName1,
            String entry1, String columnName2, String entry2, Connection c) {
        return false;
    }

    /**
     * @return       int
     * @param        tableName
     * @param        c
     */
    public int tableSize(String tableName, Connection c) {
        return 0;
    }

    /**
     * @param        c
     * @param        tableName
     * @param        columnName
     * @param        value
     */
    public void setFieldTo(Connection c, String tableName, String columnName,
            String value) {
    }

    /**
     * @param        c
     * @param        tableName
     * @param        columnName
     * @param        value
     */
    public void setFieldTo(Connection c, String tableName, String columnName,
            int value) {
    }

    /**
     * @param        s
     * @param        c
     */
    public void calculateCatchmentAreas(ScenarioState s, Connection c) {
    }

    /**
     * @param        prefix
     * @param        number
     * @param        ringDistance
     * @param        s
     * @param        c
     */
    public void calculateTrafficRings(String prefix, int number,
            double ringDistance, ScenarioState s, Connection c) {
    }

    /**
     * @param        s
     * @param        c
     */
    public void createCatchmentAreaPolygons(ScenarioState s, Connection c) {
    }

    /**
     * @param        s
     * @param        numRings
     * @param        ringDistance
     * @param        c
     */
    public void createRings(ScenarioState s, int numRings, double ringDistance,
            Connection c) {
    }

    /**
     * @param        s
     * @param        numRings
     * @param        c
     */
    public void createRingsBasedOnMaxDist(ScenarioState s, int numRings,
            Connection c) {
    }

    /**
     * @param        s
     * @param        c
     */
    public void createRingPolygons(ScenarioState s, Connection c) {
    }

    /**
     * @param        s
     * @param        c
     */
    public void createCrossingPoints(ScenarioState s, Connection c) {
    }

    /**
     * @param        s
     * @param        c
     */
    public void createBlock2PodMapping(ScenarioState s, Connection c) {
    }

    /**
     * @param        s
     * @param        c
     * @param        tableName
     * @param        newColumnName
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
    public void removePOD() {
    }

    /**
     * @return       Map
     * @param        state
     * @param        c
     */
    public Map readPODsFromDB(ScenarioState state, Connection c) {
        return null;
    }

        /**
     * @return       Map
     * @param        state
     * @param        c
     */
    public Map readWrkCpyPODsFromDB(ScenarioState state, Connection c) {
        return null;
    }

    /**
     * @param        tableName
     * @param        field1
     * @param        fieldInParenthesis
     * @param        c
     */
    public void getFieldFromDB(String tableName, String field1,
            String fieldInParenthesis, Connection c) {
    }

    /**
     * @param        tableName
     * @param        field1
     * @param        field2
     * @param        valueField2
     * @param        c
     */
    public void getArrayOfField1IfCertainValueInField2(String tableName,
            String field1, String field2, String valueField2, Connection c) {
    }

    /**
     * @return       Map<String,String>
     * @param        tableName
     * @param        field1
     * @param        field2
     * @param        field3
     * @param        c
     */
    public Map<String, String> getMapofFields1And2IfCertainValueInField3(
            String tableName, String field1, String field2, String field3,
            Connection c) {
        return null;
    }

    public void removeOldWorkingCopies(Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getUsersFromDB(Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean entryExists(String tableName, String columnName, String entry,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addSchema(String schemaName, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addEntry(String tableName, String entry1, String entry2,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteEntry(String tableName, String columnName, String entry,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteSchema(String schemaName, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insertTimestamp(String userId, String workingCopyName,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addNewPOD(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createViews(String schema, String name, String[] geographies,Map<String,Map> datasets, String key,String suffix,Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dropView(String schema, String name, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insertValuesIntoTable(String schema, String table, List<String> values, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean entryExists(String schema, String tableName,
            String columnName, String entry, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean entryExists(String schema, String tableName,
            String columnName1, String entry1, String columnName2, String entry2,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveTableAs(String schema, String string, String schema0,
            String string0, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createScenarioTables(String schema, String name,
            String[] geographies,
            Map<String, Map> datasets, String key, String suffix, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dropTable(String schema, String name, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createGeographyTable(String id, String name, String[] strings,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createCliffedGeographyTable(String id, String name, String[] strings,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveScenarioInformation(ScenarioState state, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateScenarioInformation(ScenarioState state, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List getScenarioInfo(String schema, String name, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getScenariosForUser(String userId, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getDescriptions(String userId,
            Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getGeographies(String userId, String scenario, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteScenario(String author, String name, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void renameTable(String schema, String fromTable, String toTable,
            Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTimestamp(String schema, String scenario, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cleanCrossingPoints(ScenarioState s, Connection c, double buffer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void crossingPointsEliminateDuplicateRoads(ScenarioState s, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void crossingPointsEliminateFuncl(ScenarioState s, Connection c, int funcl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void crossingPointsBreakApartMultipoints(ScenarioState s, Connection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cliffRoadTable(String id, String name, String[] geographies, Map map, Connection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
}

    @Override
    public void cliffCentroidTable(String id, String name, String[] geographies, Map map, Connection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cliffCensusBlockTable(String id, String name, String[] geographies, Map map, Connection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCliffedGeographies(CliffCoordinate coord) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
