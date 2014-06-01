/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

/**
 *
 * @author rag0122
 */
public class CensusBlockPopulation {

    public int censusBlock;
    public int population;

    public CensusBlockPopulation()
    {
        censusBlock = -1;
        population  = -1;
    }

    public CensusBlockPopulation( int c, int p )
    {
        censusBlock = c;
        population  = p;
    }

    public CensusBlockPopulation( int c, double d, int p )
    {
        censusBlock = c;
        population  = p;
    }
}
