/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.optimization;

import org.postgis.Point;

/**
 *
 * @author tamara
 */
public class POD {

    private int id;
    private int numbooths;
    private Point location;

    public POD(int id, Point location){
        this.id        = id;
        this.numbooths = 1;
        this.location  = location;
    }

    public POD(int id, int numbooths, Point location){
        this.id        = id;
        this.numbooths = numbooths;
        this.location  = location;
    }

    public int getId(){
        return this.id;
    }

    public int getNumBooths(){
        return this.numbooths;
    }

    public Point getlocation(){
        return this.location;
    }

    public String toString(){
        return this.id + " - " + this.numbooths + " - " + this.location;
    }
}
