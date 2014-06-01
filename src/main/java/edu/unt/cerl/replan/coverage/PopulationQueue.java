/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.coverage;

import edu.unt.cerl.replan.optimization.CensusBlockDistance;
import edu.unt.cerl.replan.optimization.CensusBlockDistanceComparator;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author rag0122
 */
public class PopulationQueue {

    Comparator<CensusBlockDistance> populationComparator = null;
    PriorityQueue<CensusBlockDistance> populationQueue  = null;

    public PopulationQueue( int size )
    {
        populationComparator = new CensusBlockDistanceComparator();
        populationQueue      = new PriorityQueue<CensusBlockDistance>
                ( size, populationComparator );
    }

    public void add( int id, int pop, double distance )
    {
        populationQueue.add( new CensusBlockDistance( id, pop, distance ) );
    }

    public boolean isEmpty()
    {
        return populationQueue.isEmpty();
    }

    public CensusBlockDistance poll()
    {
        return populationQueue.poll();
    }

    public CensusBlockDistance remove()
    {
        return populationQueue.remove();
    }
}
