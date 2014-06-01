/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.model;

import java.util.ArrayList;

/**
 *
 * @author saratchandraindrakanti
 */
public class StructuresOld {

    private int numVulnerabilities = 1;
    private ArrayList<VulnerabilityAtPod>[] vulnMatrix;
    private ArrayList<ResourceAtPod>[] resourceNeed;
    private ArrayList<ResourceAtPod>[] resourceAvailbility;
    private ArrayList<ResourceAtPod>[] resourceAllocation;
    private ResourceDescriptor[] allResources;
    
    private static final Structures instance = new Structures();
    
    public static Structures getInstance() {
        return instance;
    }

    public StructuresOld() {
        //init
        vulnMatrix = (ArrayList<VulnerabilityAtPod>[]) new ArrayList[numVulnerabilities];
        resourceNeed = (ArrayList<ResourceAtPod>[]) new ArrayList[numVulnerabilities];
        resourceAvailbility = (ArrayList<ResourceAtPod>[]) new ArrayList[numVulnerabilities];
        resourceAllocation = (ArrayList<ResourceAtPod>[]) new ArrayList[numVulnerabilities];
        allResources = new ResourceDescriptor[numVulnerabilities];
    }

    public ArrayList<VulnerabilityAtPod> getVulnAtPodList(int index) {
        return vulnMatrix[index];
    }

    public void setVulnAtPodList(ArrayList<VulnerabilityAtPod> vulnAtPodList, int index) {
        vulnMatrix[index] = vulnAtPodList;
    }

    public ArrayList<ResourceAtPod> getResourceNeedAtPodList(int index) {
        return resourceNeed[index];
    }

    public void setResourceNeedAtPodList(ArrayList<ResourceAtPod> list, int index) {
        resourceNeed[index] = list;
    }

    public ArrayList<ResourceAtPod> getResourceAvailAtPodList(int index) {
        return resourceAvailbility[index];
    }

    public void setResourceAvailAtPodList(ArrayList<ResourceAtPod> l, int index) {
        resourceAvailbility[index] = l;
    }

    public ArrayList<ResourceAtPod> getResourceAllocAtPodList(int index) {
        return resourceAllocation[index];
    }

    public void setResourceAllocAtPodList(ArrayList<ResourceAtPod> l, int index) {
        resourceAllocation[index] = l;
    }

    public ResourceDescriptor getResource(int index) {
        return allResources[index];
    }

    public void setResource(ResourceDescriptor r, int index) {
        allResources[index] = r;
    }
}


/*Vulnerability(Need) Matrix*/ //Each vulnerability against each POD
/*Resource weights*/
/*Net Resource Availabiilty*/ //Net Resource availability, spatial and temporal constraints 
/*Resource Availability Matrix*/ //Availability of each resource at each POD
/*Resource Allocation Matrix*/ //Allocation of resources. Availability matrix can be reused as allocation matrix
