/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.equalpopulation;

import edu.unt.cerl.applicationframework.controller.DBQueries;
import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.UserState;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class PrepareTables {

    synchronized public void createCentroidTable(Connection c, Map<String, String> m) {
        try {

            wait(3000);

            String centroidTable =
                    m.get(DefaultConstants.CENTROID_TABLE);
            System.out.println("createCentroidTable centroidTable\t" + centroidTable);

            String blockTable =
                    m.get(DefaultConstants.BLOCK_TABLE);
            System.out.println("createCentroidTable blockTable\t" + blockTable);

            String schema = UserState.userId;

            boolean exists = DBQueries.tableExists(schema, centroidTable, c);
            System.out.println("createCentroidTable exists\t" + exists);

            if (!exists) {
                String query = Queries.createCentroidsFromBlocks(centroidTable,
                        blockTable);
                Statement stmt = c.createStatement();
                System.out.println(query);
                stmt.executeUpdate(query);
            } else {
                System.out.println("Table " + centroidTable + " already exists.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PrepareTables.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (java.lang.InterruptedException e) {
            Logger.getLogger(REPLAN.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
