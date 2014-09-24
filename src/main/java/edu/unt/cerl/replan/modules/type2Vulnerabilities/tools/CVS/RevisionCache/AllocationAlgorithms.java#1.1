/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.tools;

import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.ResourceAtPod;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.Structures;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.VulnerabilityAtPod;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author sarat
 */
public class AllocationAlgorithms {

    //Resource allocation : Generate a schedule based on availability and need
    //1.Compute resource availability
    //2. Match availability with need
    //3. Case determination algo: Determine allocation case(algorithm) based on res need/availabilty
    //4. Apply resource allocation algo to come up with schedule (resource allocation)
    //Allocate resources and adjust vulnerable counts at pods
    public void trivialAllocation(int index) {

        Structures structs = Structures.getInstance();
        HashMap<Integer, ResourceAtPod> resAllocation = new HashMap<Integer, ResourceAtPod>();
        HashMap<Integer, Integer> resToCA_alloc = new HashMap<Integer, Integer>();
        double netResAvail = GeneralTools.getNetResource(structs.getResourceAvailAtPodList(index));
        double netResNeed = GeneralTools.getNetResource(structs.getResourceNeedAtPodList(index));
        double allocationRatio = netResAvail / netResNeed;

        HashMap<Integer, ResourceAtPod> need = Structures.getInstance().getResourceNeedAtPodList(index);
        Iterator<Integer> itn = need.keySet().iterator();
        while (itn.hasNext()) {
            ResourceAtPod needAtPod = need.get(itn.next());
            int pod = needAtPod.getPod();
            double resAlloc = needAtPod.getQuantity() * allocationRatio;
            ResourceAtPod alloc = new ResourceAtPod(resAlloc, pod);
            resAllocation.put(pod, alloc);
            int vulnpop = Structures.getInstance().getVulnAtPodList(index).get(pod).getVuln();
            int popServed = (int) (resAlloc / Structures.getInstance().getResource(index).getResourceToVuln());
            if (vulnpop - popServed > 0) {
                vulnpop = vulnpop - popServed;
            } else {
                vulnpop = 0;
            }
            Structures.getInstance().getVulnAtPodList(index).put(pod, new VulnerabilityAtPod(pod, vulnpop));
        }

        Structures.getInstance().setResourceAllocAtPodList(resAllocation, index);
        Structures.getInstance().setResourceToCA_Allocation(resToCA_alloc, index);

    }

    public void heuristicAllocation(int index) {
    //populate structs.setResourceAllocAtPodList(null, index);
    }

    public void universalAllocationAlgo(int index) {
        /* setResourceAllocaAtPodList() : Allocate resources to each pod such that
         1. Net [ResDeficitAtPod = |ResDeficit - ResAlloc|] is minimized
         2. Reosurce displacement is minimized
         */
        HashMap<Integer, ResourceAtPod> resAllocation = new HashMap<Integer, ResourceAtPod>();
        Structures.getInstance().setResourceAllocAtPodList(resAllocation, index);
    }
}
