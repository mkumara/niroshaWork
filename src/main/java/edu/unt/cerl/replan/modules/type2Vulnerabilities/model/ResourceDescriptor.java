/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.model;

/**
 *
 * @author saratchandraindrakanti
 */
public class ResourceDescriptor {

    private int index;
    private String name;
    private int netAvailability;
    private double resourceToVuln = 1/500.0; //number of resources req for 1 vuln individual

    public ResourceDescriptor(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNetAvailability() {
        return netAvailability;
    }

    public void setNetAvailability(int netAvailability) {
        this.netAvailability = netAvailability;
    }

    public double getResourceToVuln() {
        return resourceToVuln;
    }

    public void setResourceToVuln(double resourceToVuln) {
        this.resourceToVuln = resourceToVuln;
    }

}
