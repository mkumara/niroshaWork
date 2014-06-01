/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller;

import edu.unt.cerl.replan.controller.db.PODQueries;
import edu.unt.cerl.replan.model.POD;
import edu.unt.cerl.replan.model.PODList;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martyo
 */

/*
public class POD_DB_Controller implements Observer {

    public POD_DB_Controller(Observable obs) { // must pass the PODList object to the constructor
        obs.addObserver(this);
    }

    @Override
    public void update(Observable o, Object o1) {
        int fid = ((ChangeState) ((PODList) o).getMessage()).get_fid();
        int type = ((ChangeState) ((PODList) o).getMessage()).get_type();
        POD changedPOD;
        switch (type) {
            case 0: // add POD
                // *** code to handle an added POD ***
                changedPOD = ((ChangeState) ((PODList) o).getMessage()).get_changedPOD();
                try {
                    PODQueries.addNewPOD(changedPOD.get_name(), changedPOD.get_address(), changedPOD.get_city(), changedPOD.get_zip(), changedPOD.get_longitude(), changedPOD.get_latitude(), changedPOD.get_type(), changedPOD.get_status(), changedPOD.get_comments(), changedPOD.get_numBooths());
                } catch (SQLException ex) {
                    Logger.getLogger(POD_DB_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 1: // delete POD
                // *** code to handle a deleted POD ***
                try {
                    // delete POD
                    // *** code to handle a deleted POD ***
                    PODQueries.delete_POD(fid);
                } catch (SQLException ex) {
                    Logger.getLogger(POD_DB_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 2: // update POD
                // *** code to handle an updated POD ***
                changedPOD = ((ChangeState) ((PODList) o).getMessage()).get_changedPOD();
                try {
                    PODQueries.update_POD(fid, changedPOD.get_name(), changedPOD.get_address(), changedPOD.get_city(), changedPOD.get_zip(), changedPOD.get_longitude(), changedPOD.get_latitude(), changedPOD.get_type(), changedPOD.get_status(), changedPOD.get_comments(), changedPOD.get_numBooths());
                } catch (SQLException ex) {
                    Logger.getLogger(POD_DB_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            default:
                break;
        }
    }
}
*/