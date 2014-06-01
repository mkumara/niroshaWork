/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.equalpopulation;

import org.postgis.Point;

/**
 *
 * @author tamara
 */
public class CensusBlock {

    private int id;
    private Point centroid;
    private int population;
    private int pod;

    public CensusBlock(int id, Point centroid, int population){
        this.id = id;
        this.centroid = centroid;
        this.population = population;
    }

    public int getId(){
        return this.id;
    }

    public Point getCentroid(){
        return this.centroid;
    }

    public int getPopulation(){
        return this.population;
    }

    public void setPod(int pod){
        this.pod = pod;
    }

    public int getPod(){
        return this.pod;
    }

    public String toString(){
        return this.id + " - " + this.centroid + " - " + this.pod + " - " + this.population;
    }
}
