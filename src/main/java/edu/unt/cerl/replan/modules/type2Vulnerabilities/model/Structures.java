/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.model;

import java.util.HashMap;

/**
 *
 * @author saratchandraindrakanti
 */
public class Structures {

    private int numVulnerabilities = 20;
    private HashMap<Integer, VulnerabilityAtPod>[] vulnMatrix;
    private HashMap<Integer, ResourceAtPod>[] resourceNeed;
    private HashMap<Integer, ResourceAtPod>[] resourceAvailbility;
    private HashMap<Integer, ResourceAtPod>[] resourceAllocation;
    private HashMap<Integer, Integer>[] resourceToCA_Availability;
    private HashMap<Integer, Integer>[] resourceToCA_Allocation;
    private HashMap<Integer, Resource>[] resourceList;
    private AllResources allResources;

    private static final Structures instance = new Structures();

    public static Structures getInstance() {
        return instance;
    }

    public Structures() {
        //init
        vulnMatrix = (HashMap<Integer, VulnerabilityAtPod>[]) new HashMap[numVulnerabilities];
        resourceNeed = (HashMap<Integer, ResourceAtPod>[]) new HashMap[numVulnerabilities];
        resourceAvailbility = (HashMap<Integer, ResourceAtPod>[]) new HashMap[numVulnerabilities];
        resourceAllocation = (HashMap<Integer, ResourceAtPod>[]) new HashMap[numVulnerabilities];
        resourceToCA_Availability = (HashMap<Integer, Integer>[]) new HashMap[numVulnerabilities];
        resourceToCA_Allocation = (HashMap<Integer, Integer>[]) new HashMap[numVulnerabilities];
        allResources = new AllResources();
    }

    public HashMap<Integer, VulnerabilityAtPod> getVulnAtPodList(int index) {
        return vulnMatrix[index];
    }

    public void setVulnAtPodList(HashMap<Integer, VulnerabilityAtPod> vulnAtPodList, int index) {
        vulnMatrix[index] = vulnAtPodList;
    }

    public HashMap<Integer, ResourceAtPod> getResourceNeedAtPodList(int index) {
        return resourceNeed[index];
    }

    public void setResourceNeedAtPodList(HashMap<Integer, ResourceAtPod> list, int index) {
        resourceNeed[index] = list;
    }

    public HashMap<Integer, ResourceAtPod> getResourceAvailAtPodList(int index) {
        return resourceAvailbility[index];
    }

    public void setResourceAvailAtPodList(HashMap<Integer, ResourceAtPod> l, int index) {
        resourceAvailbility[index] = l;
    }

    public HashMap<Integer, ResourceAtPod> getResourceAllocAtPodList(int index) {
        return resourceAllocation[index];
    }

    public void setResourceAllocAtPodList(HashMap<Integer, ResourceAtPod> l, int index) {
        resourceAllocation[index] = l;
    }

    public HashMap<Integer, Integer> getResourceToCA_Availability(int index) {
        return resourceToCA_Availability[index];
    }

    public void setResourceToCA_Availability(HashMap<Integer, Integer> resMap, int index) {
        resourceToCA_Availability[index] = resMap;
    }

    public HashMap<Integer, Integer> getResourceToCA_Allocation(int index) {
        return resourceToCA_Allocation[index];
    }

    public void setResourceToCA_Allocation(HashMap<Integer, Integer> resMap, int index) {
        resourceToCA_Allocation[index] = resMap;
    }
    
     
    public HashMap<Integer, Resource> getResourceList(int index) {
        return resourceList[index];
    }

    public void setResourceList(HashMap<Integer, Resource> resourceList, int index) {
        this.resourceList[index] = resourceList;
    }

    public ResourceDescriptor getResource(int index) {
        return allResources.getResource(index);
    }

    public void addResource(ResourceDescriptor r) {
        allResources.addResource(r);
    }
}


/*Vulnerability(Need) Matrix*/ //Each vulnerability against each POD
/*Resource weights*/
/*Net Resource Availabiilty*/ //Net Resource availability, spatial and temporal constraints 
/*Resource Availability Matrix*/ //Availability of each resource at each POD
/*Resource Allocation Matrix*/ //Allocation of resources. Availability matrix can be reused as allocation matrix
