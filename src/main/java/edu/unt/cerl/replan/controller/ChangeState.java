/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.controller;

import edu.unt.cerl.replan.model.POD;

/**
 *
 * @author martyo
 */
public class ChangeState {

    private int type; //0=add, 1=delete, 2=update
    private int fid;
    private POD changedPOD;

    public ChangeState(int set_type, int set_fid){
        type=set_type;
        fid=set_fid;
    }

     public ChangeState(int set_type, int set_fid, POD set_changedPOD){
        type=set_type;
        fid=set_fid;
        changedPOD = set_changedPOD;
    }


    public int get_type(){
        return type;
    }

    public int get_fid(){
        return fid;
    }

    public POD get_changedPOD(){
        return changedPOD;
    }
}
