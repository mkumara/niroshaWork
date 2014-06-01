/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.tools;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.ResourceAtPod;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.ResourceDescriptor;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.Structures;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.VulnerabilityAtPod;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sarat
 */
public class AnalysisTools {

    //ScenarioPanel scenario;
    Structures structs;
    //Layers layers;

    public AnalysisTools() {
        //this.scenario = scenario;

        //init tables and layers
        structs = Structures.getInstance();
    }

    public void analyzeVulnerability(int index, String vulnName) {
        //calculate vuln at each pod 
        //build need (resource req)
        HashMap<Integer, VulnerabilityAtPod> vulnsByPod = vulnerablePopulationsByPOD(vulnName);
        structs.setVulnAtPodList(vulnsByPod, index);
        DbTools.getInstance().createVulnColorCodesTable(GeneralTools.returnVulnPercent(index), index);
        
        //Layers.get_instance().createType2VulnLayer();

        structs.addResource(new ResourceDescriptor(index));
        structs.setResourceNeedAtPodList(computeNeedByPod(index), index);
        DbTools.getInstance().getScenario().getState().setType2VulnAnalysisPeformed(true);
        DbTools.getInstance().getScenario().getState().setChangedAndNotify();
        REPLAN.getMainFrame().getREPLANMenuBar().reAdjustToolsMenu();
    }

//    public void analyzeVulnerability(String vulnName) {
//
//        Tables.get_instance().createVulnColorCodesTable(GeneralTools.returnVulnClasses(index));
//        Layers.get_instance().createType2VulnLayer();
//    }
    public HashMap<Integer, VulnerabilityAtPod> vulnerablePopulationsByPOD(String vulnName) {
        //HashMap<Integer, Integer> podsToVulnPop = new HashMap<Integer, Integer>();
        HashMap<Integer, VulnerabilityAtPod> vulnByPod = new HashMap<Integer, VulnerabilityAtPod>();

        try {
            ResultSet rs = DbTools.getInstance().getLanguageVulnByPod(vulnName);
            while (rs.next()) {
                vulnByPod.put(rs.getInt("pod"), new VulnerabilityAtPod(rs.getInt("pod"), rs.getInt("vulnerable")));
                //podsToVulnPop.put(rs.getInt("pod"), rs.getInt("vulnerable"));
            }
            //c.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserState.class.getName()).log(Level.SEVERE, null,
                    ex);
            System.err.println("Error retrieving users from DB");
        }

        return vulnByPod;
    }

    //index is the vuln. num
    //Compute resource need
    public HashMap<Integer, ResourceAtPod> computeNeedByPod(int index) {

        HashMap<Integer, VulnerabilityAtPod> vulnbypod = structs.getVulnAtPodList(index);
        double resToVuln = Structures.getInstance().getResource(index).getResourceToVuln();

        HashMap<Integer, ResourceAtPod> need = new HashMap<Integer, ResourceAtPod>();

        Iterator<Integer> it = vulnbypod.keySet().iterator();
        while (it.hasNext()) {
            int pod = it.next();
            VulnerabilityAtPod vuln = vulnbypod.get(pod);
            ResourceAtPod respod = new ResourceAtPod(vuln.getVuln() * resToVuln, vuln.getPod());
            need.put(pod, respod);
        }

        return need;
    }

}
