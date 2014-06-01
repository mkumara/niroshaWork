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
public class CensusBlockDistanceComparator implements
        Comparator<CensusBlockDistance> {
    
    @Override public int compare( CensusBlockDistance x,
            CensusBlockDistance y )
    {
        // Compares its two arguments for order. Returns:
        //  a negative integer, first argument is less than the second.
        //  zero, first argument is equal to the second.
        //  a positive integer as the first argument is greater than the second.

        int retVal = 0;

        if( x.distance < y.distance )
        {
            retVal = -1;
        }
        else if( x.distance > y.distance )
        {
            retVal = +1;
        }
        else // if( x.distance == y.distance )
        {
            if( x.population < y.population )
            {
                retVal = +1;
            }
            else if( x.population > y.population )
            {
                retVal = -1;
            }
            else // if( x.population == y.population )
            {
                retVal = 0;
            }
        }
/*
        System.out.println( "CensusBlockDistanceComparator"
                + "\tx.distance\t"   + x.distance
                + "\ty.distance\t"   + y.distance
                + "\tx.population\t" + x.population
                + "\ty.population\t" + y.population
                + "\tretVal\t"       + retVal
                );
*/
        return retVal;
    }
}
