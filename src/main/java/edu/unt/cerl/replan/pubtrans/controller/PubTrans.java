/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pubtrans.controller;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import java.sql.Connection;

/**
 *
 * @author martyo
 */
public class PubTrans {

    public static void performAnalysis() {
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        Connection c = REPLAN.getController().getConnection();
        
        // Get three different mappings of stations to PODs based on three different dw estimates
        edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateStation2POD(state, c, 0.01, "class01");
        
       edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateStation2POD(state, c, 0.02, "class02");
      //  edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreas(state, c, 0.02, "class02_blocks", "class02");
        edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateStation2POD(state, c, 0.04, "class04");
      //  edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreas(state, c, 0.04, "class04_blocks", "class04");
      /*
        edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreasPT(state, c, 0.01,0.00, "class01_blocks", "class01");
        edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreasPT(state, c, 0.02,0.01, "class02_blocks", "class02");
        edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreasPT(state, c, 0.04,0.02, "class04_blocks", "class04");
        edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreasPOD(state, c, 0.01, "to_pod01","class01");
        edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreasPOD(state, c, 0.02, "to_pod02","class02");
        
       edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateServiceAreasPOD(state, c, 0.04, "to_pod04","class04");
       
       */
    }
}
