/*
 * This class is only for testing the JList in the POD Editor representing the list of pods
 */
package edu.unt.cerl.replan.model;

/**
 *
 * @author martyo
 */
public class TestList {

    String list[];

    public TestList() {
        list = new String[5];
        list[0] = "pod one";
        list[1] = "pod two";
        list[2] = "pod three";
        list[3] = "pod four";
        list[4] = "pod five";
    }

    public int getSize(){
        return list.length;
    }

    public String getElement(int i){
        return list[i];
    }

}
