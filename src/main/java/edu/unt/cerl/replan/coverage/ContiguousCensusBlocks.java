/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.coverage;

import edu.unt.cerl.replan.optimization.CensusBlockDistance;
import edu.unt.cerl.replan.optimization.CensusBlockDistanceComparator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author rag0122
 */
public class ContiguousCensusBlocks {

    TreeSet<CensusBlockDistance> contiguousCensusBlocks = null;
    Comparator<CensusBlockDistance> comparator =
                new CensusBlockDistanceComparator();


    ContiguousCensusBlocks()
    {
        contiguousCensusBlocks = new TreeSet(comparator);
    }

    void add( int id, int pop, double dist )
    {
        CensusBlockDistance temp = new CensusBlockDistance( id, pop, dist );
        contiguousCensusBlocks.add( temp );
    }


    void add( CensusBlockDistance temp )
    {
        contiguousCensusBlocks.add( temp );
    }

    public Iterator iterator()
    {
        return contiguousCensusBlocks.iterator();
    }
}
