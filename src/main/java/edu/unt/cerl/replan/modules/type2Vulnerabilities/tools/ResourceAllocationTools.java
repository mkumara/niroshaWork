/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 Algorithms for Resource Allocation
 1. Determining algorithm to apply: -Resources suffcient -Resources Insuffecient
 2. Trivial allocation (Sufficient Resources)
 3. Multi-resource optimization (Optimizing between dfferent resources - Sufficient Resources)
 4. Insufficient resources: spatial modification / temporal scheduling
 5. Real-Time resource scheduling: sharing / reallocation of resources based on ground sitiation at PODs
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.tools;

import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.ResourceAtPod;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.Structures;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.view.ResMngmtTempLang;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.view.ResMngmtTempLang.Translator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author saratchandraindrakanti
 */
public class ResourceAllocationTools {
    //Determine Algorithm to apply

    Structures structs;
    AllocationAlgorithms algos;

    public ResourceAllocationTools() {
        this.structs = Structures.getInstance();
        algos = new AllocationAlgorithms();
        //this.scenario = scenario;
    }

    public void analyzeAllocation(int index) {
        structs.setResourceAvailAtPodList(computeResourceAvailability(index), index);
        //determineAllocationAlgo(index);
        algos.trivialAllocation(index);

        DbTools.getInstance().updateVulnColorCodesTable(GeneralTools.returnVulnClasses(index));
        DbTools.getInstance().createAllocColorCodesTable(GeneralTools.resAllocByPercent(index), index);
        DbTools.getInstance().getScenario().getState().setResourceAllocationPerformed(true);
        DbTools.getInstance().getScenario().getState().setChangedAndNotify();
        //Layers.get_instance().refreshLayer("vulnerability");
        //Layers.get_instance().createAllocationLayer();

    }

    /*
     Calcuate the resource avaiability at each POD based on direct interpretation 
     of user provided resource data
     */
    public HashMap<Integer, ResourceAtPod> computeResourceAvailability(int index) {

        HashMap<Integer, ResourceAtPod> avail = new HashMap<Integer, ResourceAtPod>();
        HashMap<Integer, Integer> resToCA_avail = new HashMap<Integer, Integer>();

        ResMngmtTempLang resman = ResMngmtTempLang.getInstance();
        resman.populateTranslatorsList(index);

        ArrayList<Translator> transList = resman.getTransaltorsList();
        Iterator<Translator> transIt = transList.iterator();

        while (transIt.hasNext()) {
            Translator curr = transIt.next();

            int pod = findEnclosingPod(curr.getLatitude(), curr.getLongitude());
            if (pod == -1) {
                System.out.println("No suitable pod found to add resource, skipping");
                continue;
            }
            System.out.println("Adding 1 resource to the availability at pod " + pod);
            if (avail.get(pod) == null) {
                ResourceAtPod res = new ResourceAtPod(1.0, pod);
                res.getResourceList().add(curr);
                avail.put(pod, res);
                resToCA_avail.put(curr.getId(), pod);
                //resList.put(curr.getId(), curr);

                //podToRes.put(pod, 1.0);
            } else {
                double numTrans = avail.get(pod).getQuantity();
                avail.get(pod).getResourceList().add(curr);
                avail.get(pod).setQauntity((numTrans + 1.0));
            }

        }

        Structures.getInstance().setResourceToCA_Allocation(resToCA_avail, index);
        return avail;
    }

    //return pod # of the catchment area that contains this point
    private int findEnclosingPod(double latitude, double longitude) {

        return DbTools.getInstance().findEnclosingPod(latitude, longitude);
    }

    private void determineAllocationAlgo(int index) {

        double netResAvail = GeneralTools.getNetResource(structs.getResourceAvailAtPodList(index));
        double netResNeed = GeneralTools.getNetResource(structs.getResourceNeedAtPodList(index));

        if (netResAvail >= netResNeed) {

            if (enoughResAtEachPod(index)) {
                System.out.println("Case: netResAvail >= netResNeed; enoughResAtEachPod");
                algos.trivialAllocation(index);   //OptimalSchedule()
            } else {
                System.out.println("Case: netResAvail >= netResNeed; not enoughResAtEachPod");
                redistributeResources(index);   //RedistributeResourcesAmongPODs()
                algos.trivialAllocation(index);   //OptimalSchedule()
            }
        } else {
            System.out.println("Case: netResAvail < netResNeed");
            redistributeResources(index);           //RedistributeResourcesByCompromise()
            algos.heuristicAllocation(index);              //OptimalScheduleByCompromise()
        }
    }

    private void redistributeResources(int index) {
//populate structs.setResourceAllocAtPodList(null, index);
    }

    private Boolean enoughResAtEachPod(int index) {
        Boolean val = true;
        Iterator<Integer> availIt = structs.getResourceAvailAtPodList(index).keySet().iterator();
        //Iterator<ResourceAtPod> needIt = structs.getResourceNeedAtPodList(index).iterator();

        while (availIt.hasNext()) {
            ResourceAtPod currAvail = structs.getResourceAvailAtPodList(index).get(availIt.next());
            int currPod = currAvail.getPod();
            double resAvail = currAvail.getQuantity();
            double resNeed = structs.getResourceNeedAtPodList(index).get(currPod).getQuantity();

            if (resAvail < resNeed) {
                val = false;
                break;
            }
        }
        return val;
    }

}
