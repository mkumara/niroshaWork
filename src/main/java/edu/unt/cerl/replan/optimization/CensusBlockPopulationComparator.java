/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.optimization;

import java.util.Comparator;
/**
 *
 * @author rag0122
 */
public class CensusBlockPopulationComparator implements
        Comparator<CensusBlockPopulation> {
    
    @Override public int compare( CensusBlockPopulation x,
            CensusBlockPopulation y )
    {
        if( x.population > y.population ) return -1;

        if( x.population < y.population ) return  1;

        if( x.censusBlock > y.censusBlock ) return -1;

        if( x.censusBlock < y.censusBlock ) return  1;

        return 0;
    }
}