/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

import edu.unt.cerl.replan.optimization.DBTools;
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

    public void createCentroidTable(Connection c, Map<String, String> m) {
        try {
            String centroidTable = m.get(Tables.CENTROIDS);
            String blockTable    = m.get(Tables.BLOCKS);
            if (!DBTools.tableExists( UserState.userId, centroidTable, c)) {
                String query = Queries.createCentroidsFromBlocks(centroidTable,
                        blockTable);
                Statement stmt = c.createStatement();
                System.out.println(query);
                stmt.executeUpdate(query);
            } else{
                System.out.println("Table " + centroidTable + " already exists.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PrepareTables.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
}
