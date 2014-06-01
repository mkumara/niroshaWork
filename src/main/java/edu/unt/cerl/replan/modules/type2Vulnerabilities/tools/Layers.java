/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.tools;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.applicationframework.model.DefaultStyles;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replanexecution.Replan;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.map.Layer;



/*
=========Deprecated Class==================

*/
/**
 *
 * @author saratchandraindrakanti
 */

/*
 1. Vulnerability Layer
 2. Allocation layer
 */
public class Layers {

    ScenarioPanel scenario;
    static Layers layers_instance = new Layers();
    Layer type2VulnLayer;
    Layer type2AllocLayer;

    public Layers() {
        this.scenario = DbTools.getInstance().getScenario();
    }

    public static Layers get_instance() {
        return layers_instance;
    }

    public void createType2VulnLayer() {
        try {

            String tableName = scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_VULN_CLASSES;
            type2VulnLayer = scenario.createLayer(
                    tableName,
                    DefaultStyles.createtype2VulnStyle(10),
                    "Type2 Vulnerability");

            scenario.settype2VulnLayer(type2VulnLayer);
        } catch (Exception ex) {
            Logger.getLogger(Replan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createAllocationLayer() {
        try {

            String tableName = scenario.getState().getWorkingCopyName() + DefaultConstants.TYPE2_ALLOC_CLASSES;
            type2AllocLayer = scenario.createLayer(
                    tableName,
                    DefaultStyles.createtype2VulnStyle(10),
                    "Resource Allocation");

            scenario.settype2AllocLayer(type2AllocLayer);
        } catch (Exception ex) {
            Logger.getLogger(Replan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshLayer(String name) {
        
        if(name.contentEquals("vulnerability")) {
            scenario.refreshLayer(type2VulnLayer);
        } else if (name.contentEquals("allocation")) {
            scenario.refreshLayer(type2AllocLayer);
        }    
        
    }
    
    public void addLayersToMap() {
        

    }
            
    

}
