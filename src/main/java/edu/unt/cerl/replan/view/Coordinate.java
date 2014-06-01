/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.view;

/**
 *
 * @author Onara
 */
public class Coordinate {
    
    private double x1,y1;

    public Coordinate(){}
    
    public Coordinate(double x1, double y1) {
        this.x1 = x1;
        this.y1 = y1;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }
    
    
    
}
