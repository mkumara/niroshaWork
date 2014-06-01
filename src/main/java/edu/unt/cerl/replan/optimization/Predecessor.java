/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

/**
 *
 * @author rag0122
 */
public class Predecessor {
    
    public int predecessor;
    public double distance;
    
    Predecessor()
    {
        predecessor = -1;
        distance    = -1;
    }

    Predecessor( int p, double d )
    {
        predecessor = p;
        distance    = d;
    }
    
}
