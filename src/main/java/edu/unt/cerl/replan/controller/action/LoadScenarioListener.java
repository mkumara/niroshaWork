package edu.unt.cerl.replan.controller.action;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

/**
 * Class LoadScenarioListener
 */
public class LoadScenarioListener implements EventListener {

    /**
     * This methods creates a new scenario based on the parameters provided by
     * the NewScenarioEvent.
     *
     * @param e NewScenarioEvent that contains information about the scenario to
     * be created.
     */
    public void loadScenarioEventPerformed(LoadScenarioEvent e) throws
            SQLException {


        String originalName = e.getName();
        String originalAuthor = e.getAuthor();

        String newName = this.validateScenarioNameForUser(originalName, originalAuthor);

        System.out.println("LoadScenarioListener: name found = \t " + newName);
        // Load necessary state information of saved scenario from database
        List scenarioInfo = REPLAN.getQueries().getScenarioInfo(originalAuthor,
                originalName, REPLAN.getController().getConnection());

        // Retrieve the geographies of the scenario
        String[] geographies = REPLAN.getQueries().getGeographies(originalAuthor,
                originalName, REPLAN.getController().getConnection());

        // Create state information for scenario
        ScenarioState state = new ScenarioState(scenarioInfo, geographies);
        state.setIsNew(false);
        state.setName(newName);
        if(!UserState.userId.equals(originalAuthor)) {
            state.setIsNew(true);
        }
        // create the tables based on selected geographies
        this.createGeography(state.getGeographies(), state.getWorkingCopyName());

        // copy saved tables into users workspace
        this.copySavedTablesIntoWorkspace(originalAuthor, originalName, state.getWorkingCopyName(), state);
        this.cliffGeography(state.getWorkingCopyName(), state.getGeographies());

        // create a new scenario tab
        //Creating a new scenario tab and loading an existing scenario into it
        REPLAN.getMainFrame().getTabs().loadScenario(state);
        REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
    }

    /**
     * Validates if name of loaded scenario is permissible; otherwise
     * permissible name is created by adding a suffix
     *
     * @param scenarioName name of scenario to be loaded
     * @param originalAuthor original author of scenario
     * @return a valid name for the scenario to be loaded
     * @throws SQLException
     */
    private String validateScenarioNameForUser(String scenarioName,
            String originalAuthor) throws
            SQLException {

        boolean nameFound = false;
        String name = scenarioName;
        int counter = 1;

        while (!nameFound) {
            nameFound = true;
            /*
             * If the user was not the original author, check if the author
             * already has a scenario with that name; otherwise check also open
             * tabs
             */
            if (!UserState.userId.equals(originalAuthor) && REPLAN.getQueries().
                    entryExists(
                    "public", "scenarios", "author", UserState.userId, "name",
                    scenarioName, REPLAN.getController().getConnection())) {
                nameFound = false;
                name = scenarioName + "_" + (new Integer(counter)).toString();
            } else {
                String[] tabNames =
                        REPLAN.getMainFrame().getTabs().getTabNames();
                for (int i = 0; i < tabNames.length; i++) {
                    if (tabNames[i].equals(name)) {
                        nameFound = false;
                        name = scenarioName + "_" + (new Integer(counter)).toString();
                        break;
                    }
                }
            }
            counter++;
        }
        return name;
    }

    /**
     * Based on the database-stored geographies, corresponding tables are
     * created in the users workspace (database schema)
     *
     * @param geographies a String array containing the selected geographies
     * @param name the name of the new scenario
     */
    private void createGeography(String[] geographies, String name) {
        String id = UserState.userId;
        Map<String, Map> map = REPLAN.getDatasets();
        Connection c = REPLAN.getController().getConnection();
        REPLAN.getQueries().createScenarioTables(id, name, geographies, map,
                DefaultConstants.BLOCK_TABLE, DefaultConstants.BLOCK_SUFFIX, c);
        REPLAN.getQueries().createScenarioTables(id, name, geographies, map,
                DefaultConstants.OUTLINE_TABLE, DefaultConstants.OUTLINE_SUFFIX,
                c);
        REPLAN.getQueries().createScenarioTables(id, name, geographies, map,
                DefaultConstants.CENTROID_TABLE,
                DefaultConstants.CENTROID_SUFFIX, c);
        REPLAN.getQueries().createScenarioTables(id, name, geographies, map,
                DefaultConstants.ROAD_TABLE, DefaultConstants.ROAD_SUFFIX, c);
        REPLAN.getQueries().createScenarioTables(id, name, geographies, map,
                DefaultConstants.POPULATION_TABLE,
                DefaultConstants.POPULATION_SUFFIX, c);
        //added to cliff the map if user selected a sub section of the map to execute REPLAN
        
       
    }
    //created by Nirosha to cliff the Map
    private void cliffGeography(String name, String[] geographies){
         String id = UserState.userId;
         Map<String, Map> map = REPLAN.getDatasets();
         Connection c = REPLAN.getController().getConnection();
         REPLAN.getQueries().cliffRoadTable(id, name, geographies, map, c);
         REPLAN.getQueries().cliffCentroidTable(id, name, geographies, map, c);
         REPLAN.getQueries().cliffCensusBlockTable(id, name, geographies, map, c);   
    }

    private void copySavedTablesIntoWorkspace(String savedSchema,
            String savedName, String newName, ScenarioState state) {
        REPLAN.getQueries().saveTableAs(savedSchema, savedName
                + DefaultConstants.GEOGRAPHIES_SUFFIX, UserState.userId,
                newName + DefaultConstants.GEOGRAPHIES_SUFFIX, REPLAN.getController().getConnection());
        
        REPLAN.getQueries().saveTableAs(savedSchema, savedName
                + DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX, UserState.userId,
                newName + DefaultConstants.CLIFFED_GEOGRAPHIES_SUFFIX, REPLAN.getController().getConnection());
        if (state.arePodsSelected()) {
            REPLAN.getQueries().saveTableAs(savedSchema, savedName
                    + DefaultConstants.POD_SUFFIX, UserState.userId,
                    newName + DefaultConstants.POD_SUFFIX, REPLAN.getController().
                    getConnection());
        }
        if (state.getCatchmentAreasGiven()) {
            REPLAN.getQueries().saveTableAs(savedSchema, savedName
                    + DefaultConstants.CATCHMENT_SUFFIX, UserState.userId,
                    newName + DefaultConstants.CATCHMENT_SUFFIX, REPLAN.getController().getConnection());
            REPLAN.getQueries().saveTableAs(savedSchema, savedName
                    + DefaultConstants.B2P_SUFFIX, UserState.userId,
                    newName + DefaultConstants.B2P_SUFFIX, REPLAN.getController().getConnection());
        }
        if (state.isTrafficAnalysisPerformed()) {
            REPLAN.getQueries().saveTableAs(savedSchema, savedName
                    + DefaultConstants.RINGS_SUFFIX, UserState.userId,
                    newName + DefaultConstants.RINGS_SUFFIX, REPLAN.getController().getConnection());
            REPLAN.getQueries().saveTableAs(savedSchema, savedName
                    + DefaultConstants.CROSSINGPT_SUFFIX, UserState.userId,
                    newName + DefaultConstants.CROSSINGPT_SUFFIX, REPLAN.getController().getConnection());
        }
        if (state.isCoverageAnalysisPerformed()) {
            REPLAN.getQueries().saveTableAs(savedSchema, savedName
                    + DefaultConstants.COVERAGE_SUFFIX, UserState.userId,
                    newName + DefaultConstants.COVERAGE_SUFFIX, REPLAN.getController().getConnection());
        }
    }
}
