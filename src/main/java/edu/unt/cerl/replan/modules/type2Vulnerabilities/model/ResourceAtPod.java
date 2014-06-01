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
public class ResourceAtPod implements Comparable {

    private double quantity;    //Quantity of resource   
    private int pod;    //pod #
    private ArrayList<Resource> resourceList;

    public ResourceAtPod(double quantity, int pod) {
        this.quantity = quantity;
        this.pod = pod;
        resourceList = new ArrayList<Resource>();
    }

    public double getQuantity() {
        return this.quantity;
    }

    public void setQauntity(double res) {
        this.quantity = res;
    }

    public int getPod() {
        return this.pod;
    }

    public void setPod(int pod) {
        this.pod = pod;
    }

    public ArrayList<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(ArrayList<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    @Override
    public int compareTo(Object o) {
        ResourceAtPod otherObj = (ResourceAtPod) o;
        double res = this.quantity - otherObj.quantity;
        int ret = 0;
        if (res > 0) {
            ret = 1;
        } else if (ret < 0) {
            ret = -1;
        }
        return ret;

    }

}
