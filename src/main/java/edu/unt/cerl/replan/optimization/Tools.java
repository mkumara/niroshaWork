/*
 * Collection of tools useful for many applications
 */
package edu.unt.cerl.replan.optimization;

import java.util.LinkedList;
import java.util.List;
import org.postgis.Point;

/**
 *
 * @author tamara
 */
public class Tools {

    /**
     * returns the distance between 2 postgis points
     */
    public static double getDistance(Point p1, Point p2) {
        double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y,
                2));
        return dist;
    }

    /**
     * converts an array to a list
     */
    public static List arrayToList(Object[] array){
        List l = new LinkedList();
        for (int i =0; i < array.length; i++){
            l.add(array[i]);
        }
        return l;
    }

}
