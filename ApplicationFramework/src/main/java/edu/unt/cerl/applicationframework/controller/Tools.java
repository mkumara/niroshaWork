
/*
 * Collection of tools useful for many applications
 */
package edu.unt.cerl.applicationframework.controller;

import com.vividsolutions.jts.geom.Point;
import edu.unt.cerl.applicationframework.model.CensusBlock;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author tamara
 */
public class Tools {

    /**
     * returns the distance between  jts points
     */
    public static double getDistance(Point p1, Point p2) {


        double dist = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(),
                2));

              //  System.out.println("p1=" + p1.getX() + "," + p1.getY() + "\t\t p2=" + p2.getX() + "," + p2.getY());

        return dist;
    }


    /**
     * returns the distance between 2 postgis points
     */
    public static double getPGDistance(org.postgis.Point p1, org.postgis.Point p2) {
        double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y,
                2));

        return dist;
    }

    /**
     * converts an array to a list
     */
    public static List arrayToList(Object[] array) {
        List l = new LinkedList();
        l.addAll(Arrays.asList(array));
        return l;
    }

    public static List resultSetToList(ResultSet rs) {
        return null;
    }

    /**
     * This method creates an array of census blocks (represented by their
     * geographic centroids) with corresponding population
     * @param centroids
     * @param population
     * @return
     */
    public static CensusBlock[] createBlockList(ResultSet centroids,
            Map<Integer, Integer> population) {
        CensusBlock[] blocks = null;
        try {
            centroids.last();
            blocks = new CensusBlock[centroids.getRow()];
            centroids.beforeFirst();
            while (centroids.next()) {
                Integer id = new Integer(centroids.getInt("id"));
                org.postgis.Point p = new org.postgis.Point(centroids.getString("centroid"));
                int num_pop = population.get(id);
                blocks[centroids.getRow() - 1] = new CensusBlock(id, p, num_pop);

            }

        } catch (SQLException ex) {
            //    Logger.getLogger(Partitioner.class.getName()).
            //           log(Level.SEVERE, null, ex);
        }
        // System.out.println("in create block list " + blocks[0]);
        return blocks;
    }

    /**
     * This method creates an array of census blocks (represented by their
     * geographic centroids) with corresponding population and stores the
     * census block polygon
     * @param censusblocks
     * @param centroids
     * @param population
     * @return
     */
    public static CensusBlock[] createBlockList(Map censusblocks,
        ResultSet centroids, Map<Integer, Integer> population) {
        CensusBlock[] blocks = null;
        try {
            centroids.last();
            blocks = new CensusBlock[centroids.getRow()];
            centroids.beforeFirst();
            while (centroids.next()) {
                Integer id = new Integer(centroids.getInt("id"));
                org.postgis.Point p = new org.postgis.Point(centroids.getString("centroid"));
                int num_pop = population.get(id);
                blocks[centroids.getRow() - 1] = new CensusBlock(id, p, num_pop);
            }

        } catch (SQLException ex) {
            //    Logger.getLogger(Partitioner.class.getName()).
            //           log(Level.SEVERE, null, ex);
        }
        // System.out.println("in create block list " + blocks[0]);
        return blocks;
    }

}
