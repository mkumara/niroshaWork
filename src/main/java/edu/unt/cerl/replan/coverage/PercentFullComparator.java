/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.coverage;

import java.util.Comparator;

/**
 *
 * @author rag0122
 */
public class PercentFullComparator  implements
        Comparator<PodPercentFull> {

    @Override public int compare( PodPercentFull x, PodPercentFull y )
    {
        // Compares its two arguments for order. Returns:
        //  a negative integer, first argument is less than the second.
        //  zero, first argument is equal to the second.
        //  a positive integer as the first argument is greater than the second.

        // The lower the percentage filled, the higher the priority.

        int retVal = 0;

        if( x.percentFull < y.percentFull )
        {
            retVal = -1;
        }
        else if( x.percentFull > y.percentFull )
        {
            retVal = +1;
        }
        else // if( x.distance == y.distance )
        {
            if( x.pod < y.pod )
            {
                retVal = +1;
            }
            else if( x.pod > y.pod )
            {
                retVal = -1;
            }
            else // if( x.population == y.population )
            {
                retVal = 0;
            }
        }
/**/
        System.out.println( "CensusBlockDistanceComparator"
                + "\tx.percentFull\t"   + x.percentFull
                + "\ty.percentFull\t"   + y.percentFull
                + "\tx.pod\t" + x.pod
                + "\ty.pod\t" + y.pod
                + "\tretVal\t"       + retVal
                );
/**/
        return retVal;
    }
}
