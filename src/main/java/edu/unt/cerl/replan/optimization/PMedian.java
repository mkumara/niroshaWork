/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.GISConversionTools;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.model.ScenarioState;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class PMedian {

    /**
     *
     * @param m map containing
     * @param s
     */
    public PMedian(Map<String, String> m, ScenarioState s) {
        GISConversionTools gisConvTools = new GISConversionTools();
        try {
            Connection c = REPLAN.getController().getConnection();

            // create a centroid table if it does not exist
            PrepareTables pt = new PrepareTables();
            pt.createCentroidTable(c, m);
            DBInteractions db = new DBInteractions(m, gisConvTools.getPOSTGIS(),
                    gisConvTools.getJDBC_CONNECTION_STRING() );
//            db.establishNewConnection();
            Partitioner partitioner = new Partitioner(db, m,s);

//            s.reRender();
           // try {
           // s.createCatchmentLayer();
           // s.createPODLayer();
           // ReadPODsFromDB readDB = new ReadPODsFromDB(s);
           // s.setMap(readDB.getMap());
           // s.setMaxFID(readDB.getMaxFID());
           // } catch (IOException ex) {
           //     Logger.getLogger(PMedian.class.getName()).
           //             log(Level.SEVERE, null, ex);
           // }
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }
}
