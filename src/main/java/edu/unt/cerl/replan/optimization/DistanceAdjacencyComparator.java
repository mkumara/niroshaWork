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
public class DistanceAdjacencyComparator implements
        Comparator<CensusBlockDistance> {
    
    @Override public int compare( CensusBlockDistance x,
            CensusBlockDistance y )
    {
        if( x.distance < y.distance ) return -1;

        if( x.distance > y.distance ) return  1;

        if( x.target < y.target ) return -1;

        if( x.target > y.target ) return  1;

        return 0;
    }
}
