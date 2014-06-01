/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.tools;

import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.ResourceAtPod;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.Structures;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.model.VulnerabilityAtPod;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author saratchandraindrakanti
 */
public class GeneralTools {

    public static double getNetResource(HashMap<Integer, ResourceAtPod> resList) {
        double net = 0.0;
        Iterator<Integer> it = resList.keySet().iterator();
        while (it.hasNext()) {
            ResourceAtPod curr = resList.get(it.next());
            net += curr.getQuantity();
        }
        return net;
    }

    public static HashMap<Integer, Integer> returnVulnClasses(int index) {

        //The meaning of percentile can be captured by stating that the pth percentile of a distribution
        //is a number such that approximately p percent (p%) of the values in the distribution are
        //equal to or less than that number
        //percentiles used
        //0-20
        //20-40
        //40-60
        //60-80
        //80-100
        int classes = 5;    //classes for percentile calc.
        HashMap<Integer, Integer> podsByPercentile = new HashMap<Integer, Integer>();
        HashMap<Integer, VulnerabilityAtPod> vulnByPod = Structures.getInstance().getVulnAtPodList(index);
        VulnerabilityAtPod[] arr =  vulnByPod.values().toArray(new VulnerabilityAtPod[0]);
        Arrays.sort(arr);
        int size = vulnByPod.size();
        int startingIndex = 1;
        //Iterator<VulnerabilityAtPod> it = vulnByPod.iterator();
        int arrCounter = 0;
        for (int i = 0; i < classes; i++) {
            int ind = (i + 1) * size / classes;
            //ArrayList<Integer> pods = new ArrayList<Integer>();
            for (int j = startingIndex; j <= ind; j++) {
                VulnerabilityAtPod next = arr[arrCounter];
                podsByPercentile.put(next.getPod(), i);
                //pods.add(next.pod);
                startingIndex++;
                arrCounter++;
            }

            //podsByPercentile.put(next.pod, i);
        }
        return podsByPercentile;

    }

    public static HashMap<Integer, Integer> returnVulnAbsolute(int index) {

        HashMap<Integer, VulnerabilityAtPod> vulnByPod = Structures.getInstance().getVulnAtPodList(index);
        HashMap<Integer, Integer> podsByPercentile = new HashMap<Integer, Integer>();
        Iterator<Integer> it = vulnByPod.keySet().iterator();
        while (it.hasNext()) {
            int pod = it.next();
            VulnerabilityAtPod next = vulnByPod.get(pod);
            podsByPercentile.put(next.getPod(), next.getVuln());
        }

        return podsByPercentile;

    }

    public static HashMap<Integer, Integer> returnVulnPercent(int index) {

        int numClasses = 10;
        HashMap<Integer, VulnerabilityAtPod> vulnByPod = Structures.getInstance().getVulnAtPodList(index);
        HashMap<Integer, Integer> podsByPercent = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> popMap = DbTools.getInstance().getPodPopulationMap();
        Iterator<Integer> it = vulnByPod.keySet().iterator();
        while (it.hasNext()) {
            int pod = it.next();
            VulnerabilityAtPod next = vulnByPod.get(pod);
            double percent = next.getVuln()*100.0/popMap.get(pod);
            int cl = (int) (percent *1.0 / numClasses);
            podsByPercent.put(next.getPod(), cl);
            System.out.println("Vuln Percent: "+percent+" vuln: "+next.getVuln()+" pop: "+popMap.get(pod)+" ,class: "+cl);
        }

        return podsByPercent;

    }

    public static HashMap<Integer, Integer> resAllocByPercent(int index) {

        int numClasses = 10;
        HashMap<Integer, ResourceAtPod> resByPod = Structures.getInstance().getResourceAllocAtPodList(index);
        HashMap<Integer, Integer> podsByPercent = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> popMap = DbTools.getInstance().getPodPopulationMap();
        Iterator<Integer> it = resByPod.keySet().iterator();
        while (it.hasNext()) {
            int pod = it.next();
            ResourceAtPod next = resByPod.get(pod);
            double percent = (next.getQuantity() / (popMap.get(pod) * Structures.getInstance().getResource(index).getResourceToVuln()));
            int cl = (int) (percent / numClasses);
            podsByPercent.put(next.getPod(), cl);
            System.out.println("Percent pop served after alloc: "+percent+" ,class: "+cl);
        }

        return podsByPercent;

    }
    
        public static HashMap<Integer, Integer> resAllocAbsolute(int index) {

        HashMap<Integer, ResourceAtPod> resByPod = Structures.getInstance().getResourceAllocAtPodList(index);
        HashMap<Integer, Integer> podsByPercent = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> popMap = DbTools.getInstance().getPodPopulationMap();
        Iterator<Integer> it = resByPod.keySet().iterator();
        while (it.hasNext()) {
            int pod = it.next();
            ResourceAtPod next = resByPod.get(pod);
            int abs = (int) (next.getQuantity() / Structures.getInstance().getResource(index).getResourceToVuln());
            podsByPercent.put(next.getPod(), abs);
            System.out.println("Pop served after alloc: "+abs);
        }

        return podsByPercent;

    }

    public static HashMap<Integer, Integer> resourceClassesByPods(int index) {

        int classes = 5;    //classes for percentile calc.
        HashMap<Integer, Integer> podsByPercentile = new HashMap<Integer, Integer>();
        HashMap<Integer, ResourceAtPod> resByPod = Structures.getInstance().getResourceAllocAtPodList(index);
        ResourceAtPod[] arr =  resByPod.values().toArray(new ResourceAtPod[0]);
        Arrays.sort(arr);
        int size = resByPod.size();
        int startingIndex = 1;
        //Iterator<VulnerabilityAtPod> it = vulnByPod.iterator();
        int arrCounter = 0;
        for (int i = 0; i < classes; i++) {
            int ind = (i + 1) * size / classes;
            //ArrayList<Integer> pods = new ArrayList<Integer>();
            for (int j = startingIndex; j <= ind; j++) {
                ResourceAtPod next = arr[arrCounter];
                podsByPercentile.put(next.getPod(), i);
                //pods.add(next.pod);
                startingIndex++;
                arrCounter++;
            }

            //podsByPercentile.put(next.pod, i);
        }

        return podsByPercentile;
    }

}
