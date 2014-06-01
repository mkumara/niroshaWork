/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.model;

/**
 *
 * @author martyo
 */
public class PODChangedMessage {

    private boolean podTableExists;
    private boolean podTableNewlyCreated;
    private boolean podAdded;
    private boolean podUpdated;
    private boolean podDeleted;
    private int podFid;
    //private int podIdModified;
    //private int newNumberOfPods;

    public PODChangedMessage(boolean tableExists, boolean tableNewlyCreated, boolean added, boolean updated, boolean deleted, int fid) {
        podTableExists = tableExists;
        podTableNewlyCreated = tableNewlyCreated;
        podAdded = added;
        podUpdated = updated;
        podDeleted = deleted;
        podFid = fid;
    }

    public boolean doesPodTableExist() {
        return podTableExists;
    }

    public boolean isPodTableNewlyCreated() {
        return podTableNewlyCreated;
    }

    public boolean wasPodAdded(){
    return podAdded;
    }

    public boolean wasPodUpadated() {
        return podUpdated;
    }
    
    public boolean wasPodDeleted(){
        return podDeleted;
    }
    
    public int whichPodChanged(){
        return podFid;
    }
}
