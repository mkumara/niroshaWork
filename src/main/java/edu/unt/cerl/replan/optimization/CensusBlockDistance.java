/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

/**
 *
 * @author rag0122
 */
public class CensusBlockDistance {
    
    public int target;
    public int population;
    public double distance;
    
    public CensusBlockDistance() {
        target   = -1;
        distance = -1;
    }

   public CensusBlockDistance( int t, int p, double d ) {
        target   = t;
        population = p;
        distance = d;
    }

    @Override
    public String toString() {
        String result = new String();

        result = target + "\t" + population + "\t" + distance;

        return result;
    }
}
