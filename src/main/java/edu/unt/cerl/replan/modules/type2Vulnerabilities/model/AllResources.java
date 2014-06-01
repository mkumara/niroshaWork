/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.modules.type2Vulnerabilities.model;

import java.util.HashMap;

/**
 *
 * @author sarat
 */
public class AllResources {
    private HashMap<Integer, ResourceDescriptor> resourceLookup;
    
    public AllResources() {
        resourceLookup = new HashMap<Integer, ResourceDescriptor>();
    }
    
    public void addResource( ResourceDescriptor rd) {
        resourceLookup.put(resourceLookup.size(), rd);
    }
    
    public ResourceDescriptor getResource(int index) {
        return resourceLookup.get(index);
    }
    
    public int getIndexToUse() {
        return resourceLookup.size();
    }
}
