/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.model;

/**
 *
 * @author martyo
 */
public class Tester {

    public static void main(String args[]) {
        PODList myList = new PODList();
    //    myList.add_pod(1, "testing1", "testing2", "testing3", "testing4", 1, 1, true, true, "testing5", 1);
    //    myList.add_pod(1, "testing6", "testing7", "testing8", "testing9", 2, 2, false, false, "testing10", 2);
        System.out.println("before: " + myList.get_pod_id(0));
        myList.update_pod_test(99, 0, "updated", "updated", "updated", "updated", 99, 99, false, true, "updated", 99);
        System.out.println("after: " + myList.get_pod_id(0));
    }
}
